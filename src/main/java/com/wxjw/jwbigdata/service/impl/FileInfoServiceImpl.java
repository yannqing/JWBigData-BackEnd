package com.wxjw.jwbigdata.service.impl;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.ExcelWriter;
import com.alibaba.excel.write.metadata.WriteSheet;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wxjw.jwbigdata.domain.FileInfo;
import com.wxjw.jwbigdata.domain.NewTable;
import com.wxjw.jwbigdata.domain.User;
import com.wxjw.jwbigdata.listener.excel.ExcelListener;
import com.wxjw.jwbigdata.mapper.*;
import com.wxjw.jwbigdata.service.FileInfoService;
import com.wxjw.jwbigdata.vo.FileVo.ChildrenVo;
import com.wxjw.jwbigdata.vo.FileVo.OnlineFileVo;
import com.wxjw.jwbigdata.vo.FileVo.TreeVo;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

/**
* @author yannqing
* @description 针对表【file_info】的数据库操作Service实现
* @createDate 2024-06-08 10:36:47
*/
@Service
@Slf4j
public class FileInfoServiceImpl extends ServiceImpl<FileInfoMapper, FileInfo>
    implements FileInfoService {
    @Resource
    private UserMapper userMapper;
    @Resource
    private FileInfoMapper fileInfoMapper;
    @Resource
    private NewTableMapper jwTableMapper;
    @Autowired
    private OperationMapper operationMapper;
    @Resource
    private OperationServiceImpl operationService;
//    @Autowired
//    private DataSource dataSource;

    /**
     * 上传本地文件
     * @param file 文件
     * @param fileName 文件名
     * @param fileType 文件类型
     * @param userId 用户id
     * @throws IOException
     */
    @Override
    public void uploadFile(MultipartFile file, String fileName, String fileType, Integer userId) throws IOException {
        //参数校验
        User loginUser = userMapper.selectById(userId);
        if (loginUser == null) {
            throw new IllegalArgumentException("用户不存在！");
        }
        if (file == null) {
            throw new IllegalArgumentException("上传文件为空！");
        }
        //读取excel
        EasyExcel.read(file.getInputStream(), new ExcelListener(loginUser.getRole(), fileName, userId, fileInfoMapper, operationMapper)).doReadAll();
    }

    @Override
    public void delFile(String[] fileId) {
        //参数校验
        if (fileId.length == 0) {
            throw new IllegalArgumentException("参数为空！");
        }
        //遍历所有的文件id
        Arrays.stream(fileId).forEach(id -> {
            //查找是否存在子文件
            List<FileInfo> childrenFiles = fileInfoMapper.selectList(new QueryWrapper<FileInfo>().eq("parent_id", id));
            if (!childrenFiles.isEmpty()) {
                //如果存在则删除
                List<Integer> childrenIds = childrenFiles.stream().map(FileInfo::getId).collect(Collectors.toList());
                fileInfoMapper.deleteBatchIds(childrenIds);
                //删除数据表
                for (FileInfo child : childrenFiles) {
                    operationMapper.dropTable(child.getTableName());
                }
            }
            //删除父文件
            fileInfoMapper.deleteById(id);
        });
        log.info("删除库文件");
    }

    @Override
    public void switchFileStatus(Integer status, Integer fileId) {
        fileInfoMapper.update(new UpdateWrapper<FileInfo>().eq("id", fileId).set("status", status));
        log.info("更改文件{}显示状态{}", fileId, status);
    }

    @Override
    public List<TreeVo> getTree(Integer userId) {
        List<TreeVo> tree = new ArrayList<>();
        TreeVo privateTree = getSingleTree(1, userId);
        TreeVo resultTree = getSingleTree(2, userId);
        TreeVo publicTree = getSingleTree(3, userId);
        tree.add(privateTree);
        tree.add(resultTree);
        tree.add(publicTree);
        return tree;
    }

    /**
     * 获取单个库的所有节点
     * @param fileId 1个人库，2结果库，3公共库
     * @param userId 如果是个人库需要判断
     * @return
     */
    private TreeVo getSingleTree(Integer fileId, Integer userId) {
        TreeVo tree = new TreeVo();
        switch (fileId) {
            case 1: {
                //个人库
                tree.setLabel("个人库");
                tree.setDraggable(false);
                tree.setChildren(getChildren(1));
                break;
            }
            case 2: {
                //结果库
                tree.setLabel("结果库");
                tree.setDraggable(false);
                tree.setChildren(getChildren(2));
                break;
            }
            case 3: {
                //公共库
                tree.setLabel("公共库");
                tree.setDraggable(false);
                tree.setChildren(getChildren(3));

            }
        }
        return tree;
    }

    private List<ChildrenVo> getChildren(Integer parentId) {
        List<FileInfo> childrenFileInfos = fileInfoMapper.selectList(new QueryWrapper<FileInfo>().eq("parent_id", parentId));

        List<ChildrenVo> children = new ArrayList<>();
        for (FileInfo fileInfo : childrenFileInfos) {
            ChildrenVo child = new ChildrenVo();
            child.setId(fileInfo.getId().toString());
            child.setLabel(fileInfo.getFileName() == null ? fileInfo.getTableName() : fileInfo.getFileName());
            child.setDraggable(fileInfo.getIsEnd() == 1);
            child.setChildren(fileInfo.getIsEnd() == 1 ? null : getChildren(fileInfo.getId()));
            children.add(child);
        }
        return children;
    }

    @Override
    public void uploadFileOnline(Integer userId, Integer[] fileIdArray) {
        if (userId == null || fileIdArray == null) {
            throw new IllegalArgumentException("参数为空！");
        }
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new IllegalArgumentException("用户不存在");
        }
        int parentId = 0;
        if (user.getRole() == 1) {
            //admin
            parentId = 3;
        }else {
            //user
            parentId = 1;
        }
        for (Integer integer : fileIdArray) {
            FileInfo fileInfo = new FileInfo();
            fileInfo.setParentId(parentId);
            fileInfo.setFileName(null);
            fileInfo.setTableName(jwTableMapper.selectById(integer).getTablename());
            fileInfo.setIsEnd(1);
            fileInfo.setCreateBy(null);
            fileInfoMapper.insert(fileInfo);
        }
    }

    @Override
    public List<byte[]> exportFile(Integer userId, Integer[] fileIdArray) {
        List<byte[]> excelBytesList = new ArrayList<>();
        for (Integer fileId : fileIdArray) {
            FileInfo fileInfo = fileInfoMapper.selectById(fileId);
            if (fileInfo == null) {
                // 处理文件不存在的情况，例如抛出异常或记录日志
                log.error("此id {} 不存在，请检查！", fileId);
                continue;
            }
            String tableName = fileInfo.getTableName();
            List<List<Object>> dataList = operationMapper.getData(tableName);
            try (ByteArrayOutputStream out = new ByteArrayOutputStream();
                 ExcelWriter excelWriter = EasyExcel.write(out).build()) {

                WriteSheet writeSheet = EasyExcel.writerSheet("Sheet1").build();
                excelWriter.write(dataList, writeSheet);

                excelWriter.finish(); // 完成写入

                excelBytesList.add(out.toByteArray()); // 将生成的 Excel 字节数组添加到列表中
            } catch (IOException e) {
                throw new RuntimeException("导出 Excel 文件失败", e);
            }
        }
        return excelBytesList;
    }

    @Override
    public List<OnlineFileVo> getOnlineFiles(Integer userId) {
        List<OnlineFileVo> onlineFiles = new ArrayList<>();

        List<NewTable> jwTables = jwTableMapper.selectList(null);
        for (NewTable jw : jwTables) {
            OnlineFileVo onlineFileVo = new OnlineFileVo(jw.getId().toString(), jw.getTablename());
            onlineFiles.add(onlineFileVo);
        }
        return onlineFiles;
    }


    @Override
    public String[][] queryFile(Integer userId, Integer fileId, String[] columnArray, String keyWord) {
        FileInfo fileInfo = fileInfoMapper.selectById(fileId);

        String columnName = columnArray[0];

        Set<Integer> rowIndex = new HashSet<>();

        for (String s : columnArray) {
            List<String> columnData = fileInfoMapper.selectColumns(fileInfo.getTableName(), s);
            for (int j = 0; j < columnData.size(); j++) {
                if (columnData.get(j).equals(keyWord)) {
                    rowIndex.add(j);
                }
            }
        }
        String[][] res = new String[rowIndex.size() + 1][];

        List<String> tableColumns = fileInfoMapper.getTableColumns(fileInfo.getTableName());
        String[] column = tableColumns.toArray(String[]::new);

        res[0] = column;
        int index = 0;
        for(Integer row : rowIndex) {
            List<Map<String, String>> byPosition = fileInfoMapper.getByPosition(1, row, fileInfo.getTableName());
            for (Map<String, String> stringStringMap : byPosition) {
                List<String> arr = new ArrayList<>();
                for (int j = 0; j < res[0].length; j++) {
                    arr.add(stringStringMap.get(res[0][j]));
                }String[] array = arr.stream().toArray(String[]::new);
                res[++index] = array;
            }
        }
        return res;
    }

    @Override
    public String[][] getFields(Integer userId, Integer[] fileIdArray) {
        String[][] fields = new String[fileIdArray.length][];
        for (int i = 0; i < fileIdArray.length; i++) {
            FileInfo fileInfo = fileInfoMapper.selectById(fileIdArray[i]);
            List<String> tableColumns = fileInfoMapper.getTableColumns(fileInfo.getTableName());
            String[] tableColumnsArray = tableColumns.toArray(String[]::new);
            fields[i] = tableColumnsArray;
        }
        return fields;
    }

    @Override
    public String[][] compareFiles(Integer userId, Integer[] fileIdArray, String[][] fieldArray, boolean[][] saveFieldArray, String compareType) {
        List<Set<Integer>> rowIndexes = new ArrayList<>();

        for (int j = 0; j < fieldArray.length; j++) {
            //将主表所有行添加
            //获取主表
            FileInfo mainFileInfo = fileInfoMapper.selectById(fileIdArray[0]);
            if (j == 0) {
                Set<Integer> mainTableRow = new HashSet<>();
                Integer mainTableCount = fileInfoMapper.countData(mainFileInfo.getTableName());
                for (int i = 0; i < mainTableCount; i++) {
                    mainTableRow.add(i);
                }
                rowIndexes.add(mainTableRow);
            }
            //数据对比，存储符合的行索引
            //获取主表要对比的列数据
            List<String> mainColumnData = fileInfoMapper.selectColumns(mainFileInfo.getTableName(), fieldArray[j][0]);
            //循环所有的附表
            for (int i = 1; i < fileIdArray.length; i++) {
                Set<Integer> index;
                index = new HashSet<>();
                //获取其他表
                FileInfo otherFileInfo = fileInfoMapper.selectById(fileIdArray[i]);
                List<String> otherColumnData = fileInfoMapper.selectColumns(otherFileInfo.getTableName(), fieldArray[j][i]);
                for (int k = 0; k < mainColumnData.size(); k++) {
                    for (int l = 0; l < otherColumnData.size(); l++) {
                        if (mainColumnData.get(k).equals(otherColumnData.get(l)) && compareType.equals("正向")) {
                            index.add(l);
                        } else if (!mainColumnData.get(k).equals(otherColumnData.get(l)) && compareType.equals("反向")) {
                            index.add(l);
                        }
                    }
                }
                if (rowIndexes.size() - 1 < i) {
                    rowIndexes.add(index);
                }else {
                    int finalI = i;
                    index.forEach(key -> {
                        rowIndexes.get(finalI).add(key);
                    });
                }
            }
        }

        //需要展示的列索引
        List<List<Integer>> columnIndexes = new ArrayList<>();
        for (int i = 0; i < saveFieldArray.length; i++) {
            List<Integer> columnIndex = new ArrayList<>();
            for (int j = 0; j < saveFieldArray[i].length; j++) {
                if (saveFieldArray[i][j]) {
                    columnIndex.add(j);
                }
            }
            columnIndexes.add(columnIndex);
        }
        System.out.println(columnIndexes);

        //需要展示的字段名
        List<List<String>> columnNames = new ArrayList<>();
        for (int i = 0; i < fileIdArray.length; i++) {
            List<String> tableColumns = fileInfoMapper.getTableColumns(fileInfoMapper.selectById(fileIdArray[i]).getTableName());
            List<String> columns = new ArrayList<>();
            for (int j = 0; j < columnIndexes.get(i).size(); j++) {
                String column = tableColumns.get(columnIndexes.get(i).get(j));
                columns.add(column);
            }
            columnNames.add(columns);
        }

        List<List<String>> result = new ArrayList<>();

        //循环列，查找符合条件的行
        for (int i = 0; i < columnNames.size(); i++) {
            //表
            FileInfo fileInfo = fileInfoMapper.selectById(fileIdArray[i]);
            for (int j = 0; j < columnNames.get(i).size(); j++) {
                //查找这一列的所有数据
                List<String> strings = fileInfoMapper.selectColumns(fileInfo.getTableName(), columnNames.get(i).get(j));
                //筛选行
                //添加字段（表头）到结果列中
                List<String> resultColumn = new ArrayList<>();
                resultColumn.add(columnNames.get(i).get(j));

                for (int k = 0; k < strings.size(); k++) {
                    if (rowIndexes.get(i).contains(k)) {
                        resultColumn.add(strings.get(k));
                    }
                }
                //添加结果列到 result 中
                result.add(resultColumn);
            }
        }


        //结果处理
        String[][] res = new String[result.size()][];

        for (int i = 0; i < result.size(); i++) {
            for (int j = 0; j < result.get(i).size(); j++) {
                res[i][j] = result.get(i).get(j);
            }
        }

        return res;
    }

    @Override
    public String[][] openFile(Integer userId, Integer fileId) {
        //获取表数据
        FileInfo fileInfo = fileInfoMapper.selectById(fileId);
        //1. 获取表的所有字段（表头）
        List<String> tableColumns = fileInfoMapper.getTableColumns(fileInfo.getTableName());
        //2. 根据每个字段来获取字段下面的具体数据
        List<List<String>> tableData = new ArrayList<>();
        for (String tableColumn : tableColumns) {
            List<String> data = fileInfoMapper.selectColumns(fileInfo.getTableName(), tableColumn);
            tableData.add(data);
        }
        int columnSize = tableData.size();
        int rowSize = tableData.get(0).size();
        String[][] result = new String[rowSize][];
        for (int i = 0; i < rowSize; i++) {
            result[i] = new String[columnSize];
            for (int j = 0; j < columnSize; j ++) {
                if (i == 0) {
                    result[i][j] = tableColumns.get(j);
                }else {
                    result[i][j] = tableData.get(j).get(i - 1);
                }
            }
        }
        return result;
    }

    @Override
    public void saveFile(Integer userId, Integer fileId, String[][] content) {
        FileInfo fileInfo = fileInfoMapper.selectById(fileId);
        //获取更新的数据
        List<List<String>> tableData = Arrays.stream(content).map(Arrays::asList).toList();
        //获取表的字段
        List<String> tableColumns = fileInfoMapper.getTableColumns(fileInfo.getTableName());

        //删除表
        operationMapper.dropTable(fileInfo.getTableName());

        //建表
        fileInfoMapper.createTable(fileInfo.getTableName(), tableColumns);
        //插入数据
        int index = 0;      // 保证表头不被当作数据插入
        for(List<String> data : tableData) {
            if (index ++ != 0) {
                operationMapper.dynamicInsert(fileInfo.getTableName(), tableColumns, data.subList(0, tableColumns.size()));
            }
        }
    }
}




