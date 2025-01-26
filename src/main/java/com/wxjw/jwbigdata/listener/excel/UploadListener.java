package com.wxjw.jwbigdata.listener.excel;

import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;
import com.alibaba.excel.metadata.data.ReadCellData;
import com.alibaba.excel.util.ConverterUtils;
import com.alibaba.excel.util.ListUtils;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.wxjw.jwbigdata.common.OperType;
import com.wxjw.jwbigdata.domain.FileInfo;
import com.wxjw.jwbigdata.domain.Operlog;
import com.wxjw.jwbigdata.domain.Uploadcolumn;
import com.wxjw.jwbigdata.domain.Uploadtable;
import com.wxjw.jwbigdata.mapper.*;
import com.wxjw.jwbigdata.utils.DateFormat;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.BadSqlGrammarException;
import org.springframework.jdbc.UncategorizedSQLException;

import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author black
 * @version 1.0.0
 * @className UploadListener
 * @description TODO
 * @date 2025-01-04 19:41
 **/
@Slf4j
public class UploadListener extends AnalysisEventListener<Map<Integer, String>> {

    private final Integer userId;
    private final Integer fileId;
    private final UploadtableMapper uploadtableMapper;
    private final UploadcolumnMapper uploadcolumnMapper;
    private final UploadMapper uploadMapper;
    private List<String> headData;
    private List<String> dataTypes;
    private String tableName;
    private OperlogMapper operlogMapper;
    private int index;
    private List<Integer> errorList;

    public UploadListener(Integer userId, String tableName, Integer fileId, UploadtableMapper uploadtableMapper, UploadcolumnMapper uploadcolumnMapper, UploadMapper uploadMapper, OperlogMapper operlogMapper) {
        this.userId = userId;
        this.fileId = fileId;
        this.uploadtableMapper = uploadtableMapper;
        this.uploadcolumnMapper = uploadcolumnMapper;
        this.uploadMapper = uploadMapper;
        this.operlogMapper = operlogMapper;
        this.tableName = tableName;
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
    public void invoke(Map<Integer, String> data, AnalysisContext context) throws BadSqlGrammarException {
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
    public void invokeHead(Map<Integer, ReadCellData<?>> headMap, AnalysisContext context) throws BadSqlGrammarException,IllegalArgumentException {
        log.info("解析到一条头数据:{}", ConverterUtils.convertToStringMap(headMap, context));
        Map<Integer, String> heads = ConverterUtils.convertToStringMap(headMap, context);
        index = 0;
        errorList.clear();
        headData = new ArrayList<>();
        dataTypes = new ArrayList<>();
        for (String value : heads.values()) {
            List<Uploadcolumn> uploadcolumns = uploadcolumnMapper.selectList(new QueryWrapper<Uploadcolumn>().eq("uploadtable_id", fileId).eq("comment", value));
            if (uploadcolumns.size() == 0)
                throw new IllegalArgumentException("列名：'" + value + "'在表" + tableName + "中不存在！请按照模板上传数据！");
            else if (uploadcolumns.size() == 1) {
                headData.add(uploadcolumns.get(0).getColumnname());
                dataTypes.add(uploadcolumns.get(0).getDatatype());
            } else {
                for (Uploadcolumn uploadcolumn : uploadcolumns) {
                    if (headData.contains(uploadcolumn.getColumnname())) {
                        continue;
                    } else {
                        headData.add(uploadcolumn.getColumnname());
                        dataTypes.add(uploadcolumn.getDatatype());
                    }
                }
            }
        }
//        System.out.println(headData);
    }


    @Override
    public void doAfterAllAnalysed(AnalysisContext context) throws IllegalArgumentException {
        saveData(index);
        if(errorList.size() == 0){
            Operlog operlog = new Operlog();
            operlog.setUserId(userId);
            operlog.setOperType(OperType.uploadData);
            operlog.setOperData(tableName);
            operlog.setOperTime(new Date());
            operlogMapper.insert(operlog);
        }
        else{
            String errorIndex = errorList.stream().map(Objects::toString).collect(Collectors.joining(","));
            throw new IllegalArgumentException("表格中有"+errorList.size()+"行不符合规范，行序号为："+errorIndex);
        }

        log.info("所有数据解析完成！");
    }

    /**
     * 存储数据库
     */
    private void saveData(int index) {
        log.info("第{}批,{}条数据，开始存储数据库！",index+1, cachedDataList.size());
        if(headData == null){
            throw new IllegalArgumentException("表格标题为空！");
        }
        insertMysql(headData, tableName, cachedDataList,index);
        cachedDataList.clear();
        log.info("存储数据库成功！");
    }

    public void insertMysql(List<String> heads, String tableName, List<Map<Integer, String>> columns,int index) throws BadSqlGrammarException {
        for (int i = 0; i < columns.size(); i++) {
            List<String> column = columns.get(i).entrySet().stream().map(Map.Entry::getValue).collect(Collectors.toList());
            // 数据列比标题列少:补“”
            while (column.size() < heads.size()) {
                column.add("");
            }
            // 数据列比标题列多：删除
            while (column.size() > heads.size()) {
                int lastIndex = column.size() - 1;
                column.remove(lastIndex);
            }

            try{
                uploadMapper.dynamicInsert(tableName, heads, column);
//                log.info("插入一条数据：{}", column);
            }
            catch (UncategorizedSQLException ex){
                errorList.add(index*BATCH_COUNT+i+1);
                continue;
            }
            catch (BadSqlGrammarException ex){
                errorList.add(index*BATCH_COUNT+i+1);
                continue;
            }
            catch (org.springframework.dao.DataAccessException ex){
                errorList.add(index*BATCH_COUNT+i+1);
                continue;
            }

        }
    }
}
