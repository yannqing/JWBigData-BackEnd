package com.wxjw.jwbigdata.service.impl;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.ExcelWriter;
import com.alibaba.excel.ExcelWriter;
import com.alibaba.excel.write.builder.ExcelWriterBuilder;
import com.alibaba.excel.write.metadata.WriteSheet;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.exc.IgnoredPropertyException;
import com.wxjw.jwbigdata.common.OperType;
import com.wxjw.jwbigdata.config.TableConfig;
import com.wxjw.jwbigdata.domain.*;
import com.wxjw.jwbigdata.listener.excel.ExcelListener;
import com.wxjw.jwbigdata.mapper.*;
import com.wxjw.jwbigdata.service.FileInfoService;
import com.wxjw.jwbigdata.utils.JwtUtils;
import com.wxjw.jwbigdata.vo.FileVo.*;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.MutablePropertyValues;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.BadSqlGrammarException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;

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
    @Resource
    private NewColumnMapper newColumnMapper;
    @Resource
    private RelationOfNewtableMapper relationOfNewtableMapper;
    @Resource
    private OperationMapper operationMapper;
    @Resource
    private OperationServiceImpl operationService;
    @Resource
    private NewdbQueryService newdbQueryService;
    @Resource
    private ObjectMapper objectMapper;
    @Resource
    private OperlogMapper operlogMapper;
    @Resource
    private UserTableMapper userTableMapper;
    @Resource
    private DepartmentTableMapper departmentTableMapper;
    @Autowired
    private TableConfig tableConfig;
//    @Autowired
//    private DataSource dataSource;

    /**
     * 上传本地文件
     *
     * @param file     文件
     * @param fileName 文件名
     * @param userId   用户id
     * @throws IOException
     */
    @Override
    @Transactional
    public void uploadFile(MultipartFile file, String fileName, Integer userId) throws IOException, BadSqlGrammarException {
        //参数校验
        User loginUser = userMapper.selectById(userId);
        if (loginUser == null) {
            throw new IllegalArgumentException("用户不存在！");
        }
        if (file == null) {
            throw new IllegalArgumentException("上传文件为空！");
        }
        //读取excel
        EasyExcel.read(file.getInputStream(), new ExcelListener(loginUser.getRole(), fileName, userId, fileInfoMapper, operationMapper,operlogMapper)).doReadAll();
    }

    @Override
    public void delFile(String[] fileId, HttpServletRequest request) throws JsonProcessingException {
        //参数校验
        if (fileId.length == 0) {
            throw new IllegalArgumentException("参数为空！");
        }
        //从token得到创建者的userId
        String token = request.getHeader("token");
        String userInfo = JwtUtils.getUserInfoFromToken(token);
        User loginUser = objectMapper.readValue(userInfo, User.class);

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
            FileInfo file = fileInfoMapper.selectById(id);
            if(file == null)
                return;
            Integer parentId = file.getParentId();
            String name = file.getFileName();
            //删除文件
            fileInfoMapper.deleteById(id);
            operationMapper.dropTable(file.getTableName());
            //判断是否要删除父文件夹
            List<FileInfo> brotherFiles = fileInfoMapper.selectList(new QueryWrapper<FileInfo>().eq("parent_id", parentId));
            if (brotherFiles.isEmpty() && parentId > 3) {
                fileInfoMapper.deleteById(parentId);
            }
            Operlog operlog = new Operlog();
            operlog.setUserId(loginUser.getId());
            operlog.setOperType(OperType.delFile);
            operlog.setOperData(name);
            operlog.setOperTime(new Date());
            operlogMapper.insert(operlog);
        });
        log.info("删除库文件");
    }

    @Override
    public void switchFileStatus(Integer status, Integer fileId, HttpServletRequest request) throws JsonProcessingException {
        fileInfoMapper.update(new UpdateWrapper<FileInfo>().eq("id", fileId).set("status", status));
        //从token得到创建者的userId
        String token = request.getHeader("token");
        String userInfo = JwtUtils.getUserInfoFromToken(token);
        User loginUser = objectMapper.readValue(userInfo, User.class);
        FileInfo fileInfo = fileInfoMapper.selectById(fileId);
        if(fileInfo == null)
            throw new IllegalArgumentException("文件不存在！");
        Operlog operlog = new Operlog();
        operlog.setUserId(loginUser.getId());
        operlog.setOperType(status == 0?OperType.switchFileOff:OperType.switchFileOn);
        operlog.setOperData(fileInfo.getFileName());
        operlog.setOperTime(new Date());
        operlogMapper.insert(operlog);
        log.info("更改文件{}显示状态{}", fileId, status);
    }

    @Override
    public List<TreeVo> getTree(Integer userId) {
        List<TreeVo> tree = new ArrayList<>();
        TreeVo privateTree = getSingleTree(1, userId);
        TreeVo resultTree = getSingleTree(3, userId);
        TreeVo publicTree = getSingleTree(2, userId);
        tree.add(privateTree);
        tree.add(publicTree);
        tree.add(resultTree);
        return tree;
    }

    /**
     * 获取单个库的所有节点
     *
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
                tree.setChildren(getChildren(1,userId,1));
                break;
            }
            case 2: {
                //结果库
                tree.setLabel("公共库");
                tree.setDraggable(false);
                tree.setChildren(getChildren(2,userId,2));
                break;
            }
            case 3: {
                //公共库
                tree.setLabel("结果库");
                tree.setDraggable(false);
                tree.setChildren(getChildren(3,userId,3));

            }
        }
        return tree;
    }

    private List<ChildrenVo> getChildren(Integer parentId,Integer userId,Integer type) {
        FileInfo parentFile = fileInfoMapper.selectById(parentId);
        List<FileInfo> childrenFileInfos;
        // 公共库
        if(type == 2 && parentId == 2){
            childrenFileInfos = fileInfoMapper.selectList(new QueryWrapper<FileInfo>().eq("parent_id", parentId).eq("status",1));
        }
        // 个人库
        else if(type == 1 && parentId == 1){
            childrenFileInfos = fileInfoMapper.selectList(new QueryWrapper<FileInfo>().eq("create_by",userId).eq("parent_id",parentId).eq("status",1));
        }
        // 结果库
        else if(type == 3 && parentId == 3){
            childrenFileInfos = fileInfoMapper.selectList(new QueryWrapper<FileInfo>().eq("parent_id", parentId).eq("create_by",userId).eq("status",1));
        }
        else if(type == 2){
            childrenFileInfos = fileInfoMapper.selectList(new QueryWrapper<FileInfo>().eq("parent_id", parentId).eq("status",1));
        }
        else childrenFileInfos = fileInfoMapper.selectList(new QueryWrapper<FileInfo>().eq("parent_id", parentId).eq("create_by",userId).eq("status",1));

        List<ChildrenVo> children = new ArrayList<>();
        for (FileInfo fileInfo : childrenFileInfos) {
            ChildrenVo child = new ChildrenVo();
            child.setId(fileInfo.getId().toString());
            child.setLabel(fileInfo.getFileName() == null ? fileInfo.getTableName() : fileInfo.getFileName());
            child.setDraggable(fileInfo.getIsEnd() == 1);
            child.setFather(parentFile.getFileName());
            child.setChildren(fileInfo.getIsEnd() == 1 ? null : getChildren(fileInfo.getId(),userId,type));
            if(fileInfo.getIsEnd() == 0 && child.getChildren().size()==0)
                continue;
            else children.add(child);
        }
        return children;
    }

    @Override
    public void uploadFileOnline(Integer userId, List<Integer> fileIdArray){
        if (userId == null || fileIdArray == null) {
            throw new IllegalArgumentException("参数为空！");
        }
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new IllegalArgumentException("用户不存在!");
        }
        int parentId = 0;
        if (user.getRole() == 1) {
            //admin
            parentId = 2;
        } else {
            //user
            parentId = 1;
        }
        NewTable humanTable = jwTableMapper.selectOne(new QueryWrapper<NewTable>().eq("tablename", tableConfig.gethumanTable()));
        if(humanTable == null){
            throw new IllegalArgumentException("人员主表不存在！");
        }
        List<NewColumn> humanColumns = newColumnMapper.selectList(new QueryWrapper<NewColumn>().eq("newtable_id", humanTable.getId()));
        if(humanColumns.size() == 0){
            throw new IllegalArgumentException("人员主表字段未维护！");
        }
        HashSet<String> humanFieldsSet = new HashSet<>();
        String humanSelectFields = "";
        for (NewColumn humanColumn : humanColumns) {
            if(!humanColumn.getColumnname().equals(tableConfig.gethumanPk())){
                humanFieldsSet.add(humanColumn.getComment());
                humanSelectFields += " A.`"+humanColumn.getColumnname()+"` AS `"+humanColumn.getComment()+"`,";
            }
        }
        for (Integer fileId : fileIdArray) {
            NewTable selectTable = jwTableMapper.selectById(fileId);
            if(selectTable == null)
                continue;
            String tablename = selectTable.getTablename();
            List<RelationOfNewtable> relationOfNewtables = relationOfNewtableMapper.selectList(new QueryWrapper<RelationOfNewtable>().eq("newtable_id", fileId));
            if(relationOfNewtables.isEmpty()){
                continue;
            }
            List<String> types = new ArrayList<>();
            for (RelationOfNewtable relationOfNewtable : relationOfNewtables) {
                types.add(relationOfNewtable.getLabel());
            }
            String uuid = UUID.randomUUID().toString().replace("-", "");
            String newTableName = tablename + uuid;
            String createTableQuery = "CREATE TABLE `"+newTableName+"` AS SELECT ";
            List<NewColumn> columns = newColumnMapper.selectList(new QueryWrapper<NewColumn>().eq("newtable_id", fileId));
            if(columns.isEmpty()){
                continue;
            }
            if(types.contains("human")){
                // 人员类型的表需要关联人员主表
                createTableQuery += humanSelectFields;

                for (NewColumn column : columns) {
                    String fieldName = column.getColumnname();
                    String fieldAlias = column.getComment();
                    if(!fieldName.equals("id") && !fieldName.equals("ID") && !fieldName.equals(tableConfig.getHumanFk()))
                    {
                        while(humanFieldsSet.contains(fieldAlias)){
                            fieldAlias += "_1";
                        }
                        humanFieldsSet.add(fieldAlias);
                        createTableQuery = createTableQuery + "B.`"+fieldName+"` AS `"+fieldAlias+"`,";
                    }
                }
                createTableQuery = createTableQuery.substring(0,createTableQuery.length()-1);
                createTableQuery = createTableQuery + " FROM newdb."+tableConfig.gethumanTable()+" A INNER JOIN newdb."+tablename+" B ON A."+tableConfig.gethumanPk()+"=B."+tableConfig.getHumanFk();

            }
            else{
                HashSet<String> AliasSet = new HashSet<>();
                for (NewColumn column : columns) {
                    String fieldName = column.getColumnname();
                    String fieldAlias = column.getComment();
                    if(!fieldName.equals("id") && !fieldName.equals("ID") && !fieldName.equals(tableConfig.getHumanFk()))
                    {
                        while(AliasSet.contains(fieldAlias)){
                            fieldAlias += "_1";
                        }
                        AliasSet.add(fieldAlias);
                        createTableQuery = createTableQuery + "`"+fieldName+"` AS `"+fieldAlias+"`,";
                    }
                }
                createTableQuery = createTableQuery.substring(0,createTableQuery.length()-1);
                createTableQuery = createTableQuery + " FROM newdb."+tablename;
            }

            fileInfoMapper.createTableBySQL(createTableQuery);

//            List<Map<String, Object>> orinalData = newdbQueryService.queryForList(selectTable.getTablename());
//            int len = orinalData.size();
//            for (int i = 0; i < len / 100; i++) {
//                String datas = "";
//                for (int j = 0; j < 100; j++) {
//                    datas += "(";
//                    Map<String, Object> row = orinalData.get(i * 100 + j);
//                    for (String s : row.keySet()) {
//                        datas += "'" + row.get(s) + "',";
//                    }
//                    datas = datas.substring(0, datas.length() - 1) + "),";
//                }
//                datas = datas.substring(0, datas.length() - 1);
//                fileInfoMapper.insertData(newTableName, datas);
//            }
//            for (int k = (len / 100) * 100; k < len; k++) {
//                String datas = "(";
//                Map<String, Object> row = orinalData.get(k);
//                for (String s : row.keySet()) {
//                    datas += "'" + row.get(s) + "',";
//                }
//                datas = datas.substring(0, datas.length() - 1) + ")";
//                fileInfoMapper.insertData(newTableName, datas);
//            }
            FileInfo fileInfo = new FileInfo();
            fileInfo.setParentId(parentId);
            fileInfo.setFileName(selectTable.getComment());
            fileInfo.setTableName(newTableName);
            fileInfo.setIsEnd(1);
            fileInfo.setStatus(1);
            fileInfo.setCreateBy(userId);
            fileInfo.setCreateTime(new Date());
            fileInfoMapper.insert(fileInfo);

            Operlog operlog = new Operlog();
            operlog.setUserId(userId);
            operlog.setOperType(OperType.uploadFileOnline);
            operlog.setOperData(selectTable.getComment());
            operlog.setOperTime(new Date());
            operlogMapper.insert(operlog);
        }
    }

    @Override
    public void exportFile(Integer userId, List<Integer> fileIdArray, HttpServletResponse response) throws IOException {
//        List<byte[]> excelBytesList = new ArrayList<>();
        String fileName = "export.xlsx";
        response.setContentType("application/vnd.ms-excel");
        response.setCharacterEncoding("utf-8");
        response.setHeader("Content-disposition", "attachment;filename=" + fileName);
        ExcelWriter excelWriter = EasyExcel.write(response.getOutputStream()).build();
        try {
//            ExcelWriterBuilder write = EasyExcel.write(response.getOutputStream());
            int i = 0;
            for (Integer fileId : fileIdArray) {
                FileInfo fileInfo = fileInfoMapper.selectById(fileId);
                if (fileInfo == null) {
                    // 处理文件不存在的情况，例如抛出异常或记录日志
                    log.error("此id {} 不存在，请检查！", fileId);
                    continue;
                }
                String tableName = fileInfo.getTableName();
                List<Map<String, Object>> dataList = operationMapper.getData(tableName);
                List<String> tableColumns = fileInfoMapper.getTableColumns(tableName);
                List<List<Object>> lists = MapToList(dataList,tableColumns);

                excelWriter.write(lists, EasyExcel.writerSheet(i++).build());
                Operlog operlog = new Operlog();
                operlog.setUserId(userId);
                operlog.setOperType(OperType.exportFile);
                operlog.setOperData(fileInfo.getFileName());
                operlog.setOperTime(new Date());
                operlogMapper.insert(operlog);


//            try (ByteArrayOutputStream out = new ByteArrayOutputStream();
//                 ExcelWriter excelWriter = EasyExcel.write(out).build()) {
//
//                WriteSheet writeSheet = EasyExcel.writerSheet("Sheet1").build();
//                excelWriter.write(dataList, writeSheet);
//
//                excelWriter.finish(); // 完成写入
//
//                excelBytesList.add(out.toByteArray()); // 将生成的 Excel 字节数组添加到列表中
//            } catch (IOException e) {
//                throw new RuntimeException("导出 Excel 文件失败", e);
//            }
            }
        } catch (Exception e) {

            e.printStackTrace();
        } finally {
            excelWriter.finish();
        }


//        return excelBytesList;
    }

    private List<List<Object>> MapToList(List<Map<String, Object>> listMap,List<String> keys) {
        List<List<Object>> listList = new ArrayList<>();
        for (Map<String, Object> map : listMap) {
            List<Object> innerList = new ArrayList<>();
            if (listList.isEmpty()) {
                innerList = new ArrayList<>(keys);
                listList.add(innerList);
            } else {
                for (String key : keys) {
                    innerList.add(map.get(key)==null?"":map.get(key));
                }
                listList.add(innerList);
            }

        }
        return listList;
    }

    @Override
    public List<OnlineFileVo> getOnlineFiles(HttpServletRequest request) throws JsonProcessingException{
        //从token得到创建者的userId
        String token = request.getHeader("token");
        String userInfo = JwtUtils.getUserInfoFromToken(token);
        User loginUser = objectMapper.readValue(userInfo, User.class);
        Integer userId = loginUser.getId();

        List<OnlineFileVo> onlineFiles = new ArrayList<>();
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new IllegalArgumentException("用户不存在!");
        }
        Integer departmentId = user.getDepartmentId();
        List<UserTable> user_id = userTableMapper.selectList(new QueryWrapper<UserTable>().eq("user_id", userId));
        List<DepartmentTable> department_id = departmentTableMapper.selectList(new QueryWrapper<DepartmentTable>().eq("department_id", departmentId));
        Set<Integer> tableIds = new HashSet<>();
        tableIds.add(-1);
        for (UserTable userTable : user_id) {
            tableIds.add(userTable.getNewTableId());
        }
        for (DepartmentTable departmentTable : department_id) {
            tableIds.add(departmentTable.getNewtableId());
        }

        List<NewTable> jwTables = jwTableMapper.selectList(null);
        for (NewTable jw : jwTables) {
            if(tableIds.contains(jw.getId())){
                OnlineFileVo onlineFileVo = new OnlineFileVo(jw.getId().toString(), jw.getComment());
                onlineFiles.add(onlineFileVo);
            }
        }
        return onlineFiles;
    }


    @Override
    public List<List<String>> queryFile(Integer userId, Integer fileId, String[] columnArray, String keyWord) {
        FileInfo fileInfo = fileInfoMapper.selectById(fileId);
        if(fileInfo == null)
            throw new IllegalArgumentException("文件不存在");
        List<LinkedHashMap<String, String>> dataList = fileInfoMapper.selectColumnsByParams(fileInfo.getTableName(), Arrays.stream(columnArray).toList(), keyWord);
        List<List<String>> result = new ArrayList<>();

//        Set<Integer> rowIndex = new HashSet<>();
//        String joinColumns = String.join(",", columnArray);
//
//        for (String s : columnArray) {
//            List<String> columnData = fileInfoMapper.selectColumns(fileInfo.getTableName(), s);
//            for (int j = 0; j < columnData.size(); j++) {
//                if (columnData.get(j).equals(keyWord)) {
//                    rowIndex.add(j);
//                }
//            }
//        }
//        String[][] res = new String[rowIndex.size() + 1][];

        List<String> tableColumns = fileInfoMapper.getTableColumns(fileInfo.getTableName());
        result.add(0,tableColumns);
        for (Map<String, String> stringStringMap : dataList) {
            List tmp = new ArrayList();
            for (String tableColumn : tableColumns) {
                tmp.add(stringStringMap.get(tableColumn));
            }
            result.add(tmp);
        }

        Operlog operlog = new Operlog();
        operlog.setUserId(userId);
        operlog.setOperType(OperType.queryFile);
        operlog.setOperData(fileInfo.getFileName()+":"+keyWord);
        operlog.setOperTime(new Date());
        operlogMapper.insert(operlog);

//        String[] column = tableColumns.toArray(String[]::new);
//
//        res[0] = column;
//        int index = 0;
//        for (Integer row : rowIndex) {
//            List<Map<String, String>> byPosition = fileInfoMapper.getByPosition(1, row, fileInfo.getTableName());
//            for (Map<String, String> stringStringMap : byPosition) {
//                List<Object> arr = new ArrayList<>();
//                for (int j = 0; j < res[0].length; j++) {
//                    arr.add(stringStringMap.get(res[0][j]));
//                }
//                String[] array = arr.stream().toArray(String[]::new);
//                res[++index] = array;
//            }
//        }
        return result;
    }

    @Override
    public JSONArray getFields(Integer userId, List<Integer> fileIdArray) {
        JSONArray fields = new JSONArray();
        for (Integer fileId : fileIdArray) {
            JSONObject field = new JSONObject();
            field.put("fileId",fileId);
            FileInfo fileInfo = fileInfoMapper.selectById(fileId);
            if(fileInfo == null)
                continue;
            List<Map<String,String>> tableColumns = fileInfoMapper.getTableAndColumns(fileInfo.getTableName());
            field.put("options",tableColumns);
            fields.add(field);
        }
        return fields;
    }

    @Override
    public List<List<Object>> compareFiles(Integer userId, Integer[] fileIdArray, String[][] fieldArray, List<SaveFileIdVo> saveFieldArray) {
        List<List<Object>> result = new ArrayList<>();
        List<Object> columns = new ArrayList<>();
        List<List<Object>> tmpResult = new ArrayList<>();
        String fileNames = "";

        // 循环每一个文件
        for(int i = 0;i<fileIdArray.length;i++){
            // 第一个文件作为主文件
            if(i == 0){
                FileInfo mainFileInfo = fileInfoMapper.selectById(fileIdArray[i]);
                if(mainFileInfo == null)
                    throw new IllegalArgumentException("选择的文件不存在！");
                fileNames += mainFileInfo.getFileName();
                List<LinkedHashMap<String,Object>> mainData = fileInfoMapper.getData(mainFileInfo.getTableName());
                List<String> mainFileColumns = fileInfoMapper.getTableColumns(mainFileInfo.getTableName());
                for (Map<String,Object> datum : mainData) {
                    List<Object> currentRow = new ArrayList<>();
                    for (String mainFileColumn : mainFileColumns) {
                        currentRow.add(datum.get(mainFileColumn)==null?"":datum.get(mainFileColumn));
                    }
                    result.add(currentRow);
                }
                List<Object> tableColumns = fileInfoMapper.getTableColumnsBySeq(mainFileInfo.getTableName(),String.valueOf(i+1));
                for (Object tableColumn : tableColumns) {
                    columns.add(tableColumn);
                }
            }
            else{
                FileInfo compareTable = fileInfoMapper.selectById(fileIdArray[i]);
                if(compareTable == null)
                    throw new IllegalArgumentException("选择的文件不存在！");
                fileNames += "-"+compareTable.getFileName();
                List<LinkedHashMap<String,Object>> compareData = fileInfoMapper.getData(compareTable.getTableName());
                List<String> originalCompareColumns = fileInfoMapper.getTableColumns(compareTable.getTableName());
                List<Object> compareColumns = fileInfoMapper.getTableColumnsBySeq(compareTable.getTableName(),String.valueOf(i+1));
                // 循环第一张表中的每一行
                for (List<Object> resultRow : result) {
                    // 循环第二张表中的每一行
                    for (Map<String,Object> compareDatum : compareData) {
                        boolean isMatch = true;
                        // 循环每一对字段
                        for (int j = 0;j<fieldArray[0].length;j++) {
                            String field1 = "1-"+fieldArray[0][j];
                            String field2 = fieldArray[i][j];
                            // 当前字段在第一张表中的序号
                            int fieldIndex1 = columns.indexOf(field1);
//                            int fieldIndex2 = compareColumns.indexOf(field2);
                            if(fieldIndex1 < 0 ){
                                isMatch = false;
                                break;
                            }
                            if(resultRow.get(fieldIndex1) == null || resultRow.get(fieldIndex1).toString().isEmpty() || compareDatum.get(field2) == null || compareDatum.get(field2).toString().isEmpty() || !resultRow.get(fieldIndex1).toString().equals(compareDatum.get(field2).toString()))
                            {
                                isMatch = false;
                                break;
                            }
                        }
                        if(isMatch) {
                            List<Object> tmp = new ArrayList<>();
                            tmp.addAll(resultRow);
                            for (String originalCompareColumn : originalCompareColumns) {
                                tmp.add(compareDatum.get(originalCompareColumn)==null?"":compareDatum.get(originalCompareColumn));
                            }
                            tmpResult.add(tmp);
                        }
                    }
                }
                if(!tmpResult.isEmpty()){
                    result.clear();
                    result.addAll(tmpResult);
                    tmpResult.clear();
                    columns.addAll(compareColumns);
                }
                else{
                    return null;
                }
            }

        }

        List<Object> returnColumns = new ArrayList<>();
        for (SaveFileIdVo saveFileIdVo : saveFieldArray) {
            returnColumns.add(saveFileIdVo.getLabel());
        }

        for (int columnNum = columns.size()-1;columnNum>=0;columnNum--) {
            if(!returnColumns.contains(columns.get(columnNum).toString())){
                for (List<Object> resultobjects : result) {
                    resultobjects.remove(columnNum);
                }
            }
        }

        result.add(0,returnColumns);

        Operlog operlog = new Operlog();
        operlog.setUserId(userId);
        operlog.setOperType(OperType.compareFile);
        operlog.setOperData(fileNames);
        operlog.setOperTime(new Date());
        operlogMapper.insert(operlog);

        return result;

//        List<Set<Integer>> rowIndexes = new ArrayList<>();
//
//        for (int j = 0; j < fieldArray.length; j++) {
//            //将主表所有行添加
//            //获取主表
//            FileInfo mainFileInfo = fileInfoMapper.selectById(fileIdArray[0]);
//            if (j == 0) {
//                Set<Integer> mainTableRow = new HashSet<>();
//                Integer mainTableCount = fileInfoMapper.countData(mainFileInfo.getTableName());
//                for (int i = 0; i < mainTableCount; i++) {
//                    mainTableRow.add(i);
//                }
//                rowIndexes.add(mainTableRow);
//            }
//            //数据对比，存储符合的行索引
//            //获取主表要对比的列数据
//            List<String> mainColumnData = fileInfoMapper.selectColumns(mainFileInfo.getTableName(), fieldArray[j][0]);
//            //循环所有的附表
//            for (int i = 1; i < fileIdArray.length; i++) {
//                Set<Integer> index;
//                index = new HashSet<>();
//                //获取其他表
//                FileInfo otherFileInfo = fileInfoMapper.selectById(fileIdArray[i]);
//                List<String> otherColumnData = fileInfoMapper.selectColumns(otherFileInfo.getTableName(), fieldArray[j][i]);
//                for (int k = 0; k < mainColumnData.size(); k++) {
//                    for (int l = 0; l < otherColumnData.size(); l++) {
//                        if (mainColumnData.get(k).equals(otherColumnData.get(l)) && compareType.equals("正向")) {
//                            index.add(l);
//                        } else if (!mainColumnData.get(k).equals(otherColumnData.get(l)) && compareType.equals("反向")) {
//                            index.add(l);
//                        }
//                    }
//                }
//                if (rowIndexes.size() - 1 < i) {
//                    rowIndexes.add(index);
//                } else {
//                    int finalI = i;
//                    index.forEach(key -> {
//                        rowIndexes.get(finalI).add(key);
//                    });
//                }
//            }
//        }
//
//        //需要展示的列索引
//        List<List<Integer>> columnIndexes = new ArrayList<>();
//        for (int i = 0; i < saveFieldArray.size(); i++) {
//            List<Integer> columnIndex = new ArrayList<>();
//            for (int j = 0; j < saveFieldArray[i].length; j++) {
//                if (saveFieldArray[i][j]) {
//                    columnIndex.add(j);
//                }
//            }
//            columnIndexes.add(columnIndex);
//        }
//        System.out.println(columnIndexes);
//
//        //需要展示的字段名
//        List<List<String>> columnNames = new ArrayList<>();
//        for (int i = 0; i < fileIdArray.length; i++) {
//            List<String> tableColumns = fileInfoMapper.getTableColumns(fileInfoMapper.selectById(fileIdArray[i]).getTableName());
//            List<String> columns = new ArrayList<>();
//            for (int j = 0; j < columnIndexes.get(i).size(); j++) {
//                String column = tableColumns.get(columnIndexes.get(i).get(j));
//                columns.add(column);
//            }
//            columnNames.add(columns);
//        }
//
//        List<List<String>> result = new ArrayList<>();
//
//        //循环列，查找符合条件的行
//        for (int i = 0; i < columnNames.size(); i++) {
//            //表
//            FileInfo fileInfo = fileInfoMapper.selectById(fileIdArray[i]);
//            for (int j = 0; j < columnNames.get(i).size(); j++) {
//                //查找这一列的所有数据
//                List<String> strings = fileInfoMapper.selectColumns(fileInfo.getTableName(), columnNames.get(i).get(j));
//                //筛选行
//                //添加字段（表头）到结果列中
//                List<String> resultColumn = new ArrayList<>();
//                resultColumn.add(columnNames.get(i).get(j));
//
//                for (int k = 0; k < strings.size(); k++) {
//                    if (rowIndexes.get(i).contains(k)) {
//                        resultColumn.add(strings.get(k));
//                    }
//                }
//                //添加结果列到 result 中
//                result.add(resultColumn);
//            }
//        }
//
//
//        //结果处理
//        String[][] res = new String[result.size()][];
//
//        for (int i = 0; i < result.size(); i++) {
//            for (int j = 0; j < result.get(i).size(); j++) {
//                res[i][j] = result.get(i).get(j);
//            }
//        }
//
//        return res;
    }

    @Override
    public List<List<Object>> compareFilesReverse(Integer userId, Integer[] fileIdArray, String[][] fieldArray, List<SaveFileIdVo> saveFieldArray) {
        List<List<Object>> result = new ArrayList<>();
        List<Object> columns = new ArrayList<>();
        String fileNames = "";

        // 循环每一个文件
        for(int i = 0;i<fileIdArray.length;i++){
            // 第一个文件作为主文件
            if(i == 0){
                FileInfo mainFileInfo = fileInfoMapper.selectById(fileIdArray[i]);
                if(mainFileInfo == null)
                    throw new IllegalArgumentException("选择的文件不存在！");
                fileNames += mainFileInfo.getFileName();
                List<LinkedHashMap<String,Object>> mainData = fileInfoMapper.getData(mainFileInfo.getTableName());
                List<String> mainFileColumns = fileInfoMapper.getTableColumns(mainFileInfo.getTableName());
                for (Map<String,Object> datum : mainData) {
                    List<Object> currentRow = new ArrayList<>();
                    for (String mainFileColumn : mainFileColumns) {
                        currentRow.add(datum.get(mainFileColumn)==null?"":datum.get(mainFileColumn));
                    }
                    result.add(currentRow);
                }
                List<Object> tableColumns = fileInfoMapper.getTableColumnsAsObject(mainFileInfo.getTableName());
                for (Object tableColumn : tableColumns) {
                    columns.add(tableColumn);
                }
            }
            else{
                FileInfo compareTable = fileInfoMapper.selectById(fileIdArray[i]);
                if(compareTable == null)
                    throw new IllegalArgumentException("选择的文件不存在！");
                fileNames += "-"+compareTable.getFileName();
                List<LinkedHashMap<String,Object>> compareData = fileInfoMapper.getData(compareTable.getTableName());
                Iterator<List<Object>> iterator = result.iterator();
                while(iterator.hasNext()){
                    List<Object> resultRow = iterator.next();
                    outerLoop:
                    for (Map<String,Object> compareDatum : compareData) {
                        for (int j = 0;j<fieldArray[0].length;j++) {
                            String field1 = fieldArray[0][j];
                            String field2 = fieldArray[i][j];
                            int fieldIndex1 = columns.indexOf(field1);
//                            int fieldIndex2 = compareColumns.indexOf(field2);
                            if(fieldIndex1 < 0 ){
                                throw new IllegalArgumentException("主表匹配字段实际在表中不存在！");
                            }
                            else if(resultRow.get(fieldIndex1) != null && !resultRow.get(fieldIndex1).toString().isEmpty() && compareDatum.get(field2) != null && !compareDatum.get(field2).toString().isEmpty() && resultRow.get(fieldIndex1).toString().equals(compareDatum.get(field2).toString()))
                            {
                                iterator.remove();
                                break outerLoop;
                            }
                        }

                    }

                }
            }

        }

        if(result.size() == 0){
            return result;
        }
        else{
            List<Object> returnColumns = new ArrayList<>();
            for (SaveFileIdVo saveFileIdVo : saveFieldArray) {
                Integer pIndex = saveFileIdVo.getPIndex();
                if(pIndex == 0){
                    returnColumns.add(saveFileIdVo.getId());
                }
            }

            for (int columnNum = columns.size()-1;columnNum>=0;columnNum--) {
                if(!returnColumns.contains(columns.get(columnNum).toString())){
                    for (List<Object> resultobjects : result) {
                        resultobjects.remove(columnNum);
                    }
                }
            }

            result.add(0,returnColumns);
            Operlog operlog = new Operlog();
            operlog.setUserId(userId);
            operlog.setOperType(OperType.compareFileReverse);
            operlog.setOperData(fileNames);
            operlog.setOperTime(new Date());
            operlogMapper.insert(operlog);
            return result;
        }
    }

    @Override
    public String[][] openFile(Integer userId, Integer fileId) {
        //获取表数据
        FileInfo fileInfo = fileInfoMapper.selectById(fileId);
        if(fileInfo == null)
            throw new IllegalArgumentException("选择的文件不存在！");
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
        String[][] result = new String[rowSize + 1][columnSize];
        for (int i = 0; i < rowSize + 1; i++) {
            result[i] = new String[columnSize];
            for (int j = 0; j < columnSize; j++) {
                if (i == 0) {
                    result[i][j] = tableColumns.get(j);
                } else {
                    result[i][j] = tableData.get(j).get(i - 1);
                }
            }
        }
        Operlog operlog = new Operlog();
        operlog.setUserId(userId);
        operlog.setOperType(OperType.openFile);
        operlog.setOperData(fileInfo.getFileName());
        operlog.setOperTime(new Date());
        operlogMapper.insert(operlog);
        return result;
    }

    @Override
    public void saveFile(Integer userId, Integer fileId, String[][] content) {
        FileInfo fileInfo = fileInfoMapper.selectById(fileId);
        if(fileInfo == null)
            throw new IllegalArgumentException("保存的文件不存在！");
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
        for (List<String> data : tableData) {
            if (index++ != 0) {
                operationMapper.dynamicInsert(fileInfo.getTableName(), tableColumns, data.subList(0, tableColumns.size()));
            }
        }
        Operlog operlog = new Operlog();
        operlog.setUserId(userId);
        operlog.setOperType(OperType.saveFile);
        operlog.setOperData(fileInfo.getFileName());
        operlog.setOperTime(new Date());
        operlogMapper.insert(operlog);
    }

    @Override
    public List<fileVo> getFileList() {
        List<FileInfo> fileInfos = fileInfoMapper.selectList(new QueryWrapper<FileInfo>().eq("is_end",1));
        ArrayList<fileVo> fileVos = new ArrayList<>();
        String fileType;
        for (FileInfo fileInfo : fileInfos) {
            Integer parentId = fileInfo.getParentId();
            if(parentId == 1 || parentId == 3){ // 父节点是个人库、结果库
                continue;
            }
            else if(parentId == 2){  // 父节点直接是公共库（在线文件）
                fileType = "在线文件";
            }
            else{
                FileInfo parentFile = fileInfoMapper.selectById(parentId);
                if(parentFile.getParentId() == 2){  // 公共库中上传到本地文件
                    fileType = "本地文件";
                }
                else{
                    continue;
                }
            }

            User creator = userMapper.selectById(fileInfo.getCreateBy());
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            fileVos.add(new fileVo(fileInfo.getId(),fileInfo.getFileName(),fileType,creator==null?"":creator.getUsername(),formatter.format(fileInfo.getCreateTime()),fileInfo.getStatus()==0?false:true));
        }
        return fileVos;
    }

    @Override
    public void addFile(Integer userId, String[][] content) {
        //获取更新的数据
        List<String[]> tableData = new ArrayList(Arrays.asList(content));
        //获取表的字段
        String[] firstLine = tableData.get(0);
        List<String> tableColumns = new ArrayList<>();
        for (String s : firstLine) {
            if(!s.isEmpty()){
                tableColumns.add(s);
            }
            else
                break;
        }

        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
        String fileName = userId.toString()+now.format(formatter);
        //建表
        fileInfoMapper.createTable(fileName, tableColumns);
        tableData.remove(0);
        //插入数据
        for (String[] data : tableData) {
            operationMapper.dynamicInsert(fileName, tableColumns, new ArrayList(Arrays.asList(data)).subList(0, tableColumns.size()));
        }

        FileInfo fileInfo = new FileInfo();
        fileInfo.setParentId(3);
        fileInfo.setFileName(fileName);
        fileInfo.setTableName(fileName);
        fileInfo.setIsEnd(1);
        fileInfo.setStatus(1);
        fileInfo.setCreateBy(userId);
        fileInfo.setCreateTime(new Date());
        fileInfoMapper.insert(fileInfo);

        Operlog operlog = new Operlog();
        operlog.setUserId(userId);
        operlog.setOperType(OperType.saveFile);
        operlog.setOperData(fileInfo.getFileName());
        operlog.setOperTime(new Date());
        operlogMapper.insert(operlog);
    }
}




