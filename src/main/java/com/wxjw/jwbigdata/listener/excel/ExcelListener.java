package com.wxjw.jwbigdata.listener.excel;

import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;
import com.alibaba.excel.metadata.data.DataFormatData;
import com.alibaba.excel.metadata.data.ReadCellData;
import com.alibaba.excel.util.ConverterUtils;
import com.alibaba.excel.util.ListUtils;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.toolkit.SqlRunner;
import com.wxjw.jwbigdata.common.OperType;
import com.wxjw.jwbigdata.domain.FileInfo;
import com.wxjw.jwbigdata.domain.Operlog;
import com.wxjw.jwbigdata.mapper.FileInfoMapper;
import com.wxjw.jwbigdata.mapper.OperationMapper;
import com.wxjw.jwbigdata.mapper.OperlogMapper;
import com.wxjw.jwbigdata.utils.DateFormat;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.BadSqlGrammarException;
import org.springframework.jdbc.UncategorizedSQLException;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
public class ExcelListener extends AnalysisEventListener<Map<Integer, String>> {

    private final Integer role;
    private final String fileName;
    private final Integer createdUser;
    private final FileInfoMapper fileInfoMapper;
    private final OperationMapper operationMapper;
    private Integer headSize;
    private Map<Integer, String> headData;
    private String tableName;
    private OperlogMapper operlogMapper;
    private int index;
    private List<Integer> errorList;

    public ExcelListener(Integer role, String fileName, Integer createdUser, FileInfoMapper fileInfoMapper, OperationMapper operationMapper, OperlogMapper operlogMapper) {
        this.role = role;
        this.fileName = fileName;
        this.createdUser = createdUser;
        this.fileInfoMapper = fileInfoMapper;
        this.operationMapper = operationMapper;
        this.operlogMapper = operlogMapper;
        errorList = new ArrayList<>();
    }

    /**
     * 每隔5条存储数据库，实际使用中可以100条，然后清理list ，方便内存回收
     */
    private static final int BATCH_COUNT = 100;
    private List<Map<Integer, String>> cachedDataList = ListUtils.newArrayListWithExpectedSize(BATCH_COUNT);

    /**
     * 读取每一条数据
     *
     * @param data
     * @param context
     */
    @Override
    public void invoke(Map<Integer, String> data, AnalysisContext context) {
        log.info("解析到一条数据:{}", JSON.toJSONString(data));
        cachedDataList.add(data);
        if (cachedDataList.size() >= BATCH_COUNT) {
            saveData(index++);
            cachedDataList = ListUtils.newArrayListWithExpectedSize(BATCH_COUNT);
        }
    }

    /**
     * 这里会一行行的返回头
     *
     * @param headMap
     * @param context
     */

    @Override
    public void invokeHead(Map<Integer, ReadCellData<?>> headMap, AnalysisContext context) throws BadSqlGrammarException {
        log.info("解析到一条头数据:{}", ConverterUtils.convertToStringMap(headMap, context));
        Map<Integer, String> heads = ConverterUtils.convertToStringMap(headMap, context);
        headData = heads;

        List<String> columns = heads.entrySet().stream().map(Map.Entry::getValue).collect(Collectors.toList());

        System.out.println(columns);
        //1. 查询此文件是否存在数据库中
        String fileTime = DateFormat.getFileLongTime();
        FileInfo isExitFile = fileInfoMapper.selectOne(new QueryWrapper<FileInfo>().eq("name", fileName + fileTime));
        if (isExitFile == null) {
            //2. 添加file_info记录，文件
            FileInfo fileInfo = new FileInfo();
            if (role == 1) {
                //admin
                fileInfo.setParentId(2);    //public
                fileInfo.setFileName(fileName + fileTime);
                fileInfo.setTableName(null);
                fileInfo.setIsEnd(0);
                fileInfo.setCreateBy(createdUser);
                fileInfo.setCreateTime(new Date());
                fileInfo.setStatus(1);
            } else {
                //user
                fileInfo.setParentId(1);    // private
                fileInfo.setFileName(fileName + fileTime);
                fileInfo.setTableName(null);
                fileInfo.setIsEnd(0);
                fileInfo.setCreateBy(createdUser);
                fileInfo.setCreateTime(new Date());
                fileInfo.setStatus(1);
            }


            //3. 创建表格
            String uuid = UUID.randomUUID().toString().replace("-", "");
            tableName = uuid;
            List<String> renamedColumns = reNameColumns(columns);
            fileInfoMapper.createTable(tableName, renamedColumns);

            log.info("创建表格{}成功", fileName + fileTime);
            fileInfoMapper.insert(fileInfo);
            //4. 新建表格信息同步到file_info中
            FileInfo parentFile = fileInfoMapper.selectOne(new QueryWrapper<FileInfo>().eq("name", fileName + fileTime));
            String sheetName = context.readSheetHolder().getSheetName();
            FileInfo fileInfo1 = new FileInfo();
            fileInfo1.setParentId(parentFile.getId());
            fileInfo1.setFileName(sheetName == null ? "Sheet1" : sheetName);
            fileInfo1.setTableName(uuid);
            fileInfo1.setIsEnd(1);
            fileInfo1.setCreateBy(createdUser);
            fileInfo1.setCreateTime(new Date());
            fileInfo1.setStatus(1);

            fileInfoMapper.insert(fileInfo1);

            Operlog operlog = new Operlog();
            operlog.setUserId(createdUser);
            operlog.setOperType(OperType.uploadFile);
            operlog.setOperData(fileName + fileTime + "/" + sheetName);
            operlog.setOperTime(new Date());
            operlogMapper.insert(operlog);
            log.info("新建表格{}数据同步成功", uuid);
        }

    }

    List<String> reNameColumns(List<String> columns) {
        List<String> newList = new ArrayList<>(columns);
        HashSet<String> set = new HashSet<>(columns);
        int size = columns.size();
        while (set.size() < size) {
            List<String> tmp = new ArrayList<>();
            for (String column : newList) {
                if (column == null) {
                    column = "未命名";
                }
                if (tmp.contains(column)) {
                    tmp.add(column + "(1)");
                } else
                    tmp.add(column);
            }
            newList = tmp;
            set = new HashSet<>(newList);
        }
        return newList;
    }

    /**
     * 解析完所有的数据（以BATCH_COUNT为一个周期）后调用
     *
     * @param context
     */
    @Override
    public void doAfterAllAnalysed(AnalysisContext context) {
        saveData(index);
        if (errorList.size() > 0) {
            String errorIndex = errorList.stream().map(Objects::toString).collect(Collectors.joining(","));
            throw new IllegalArgumentException("表格中有" + errorList.size() + "行不符合规范，行序号为：" + errorIndex);
        }
        log.info("所有数据解析完成！");
    }

    /**
     * 存储数据库
     */
    private void saveData(int index) {
        log.info("{}条数据，开始存储数据库！", cachedDataList.size());
        if (headData == null) {
            throw new IllegalArgumentException("表格标题为空！");
        }
        insertMysql(headData, tableName, cachedDataList, index);
        cachedDataList.clear();
        log.info("存储数据库成功！");
    }


    public void insertMysql(Map<Integer, String> heads, String tableName, List<Map<Integer, String>> columns, int index) {
        List<String> header = heads.entrySet().stream().map(Map.Entry::getValue).collect(Collectors.toList());
        header = reNameColumns(header);
        for (int i = 0; i < columns.size(); i++) {
            List<String> column = columns.get(i).entrySet().stream().map(Map.Entry::getValue).collect(Collectors.toList());
            while (column.size() < header.size()) {
                column.add("");
            }
            while (column.size() > header.size()) {
                int lastIndex = column.size() - 1;
                column.remove(lastIndex);
            }
            try {
                operationMapper.dynamicInsert(tableName, header, column);
                log.info("插入一条数据：{}", column);
            } catch (UncategorizedSQLException ex) {
                errorList.add(index * BATCH_COUNT + i + 1);
                continue;
            } catch (BadSqlGrammarException ex) {
                errorList.add(index * BATCH_COUNT + i + 1);
                continue;
            } catch (org.springframework.dao.DataAccessException ex) {
                errorList.add(index * BATCH_COUNT + i + 1);
                continue;
            }
        }
    }
}

