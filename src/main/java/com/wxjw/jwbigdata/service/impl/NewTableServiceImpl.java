package com.wxjw.jwbigdata.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wxjw.jwbigdata.config.TableConfig;
import com.wxjw.jwbigdata.domain.*;
import com.wxjw.jwbigdata.mapper.*;
import com.wxjw.jwbigdata.service.NewTableService;
import com.wxjw.jwbigdata.vo.TableVo.AuthTable;
import com.wxjw.jwbigdata.vo.TableVo.ColumnVo;
import com.wxjw.jwbigdata.vo.TableVo.TableVo;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.BadSqlGrammarException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * @author Paul
 * @description 针对表【newtable】的数据库操作Service实现
 * @createDate 2024-09-02 22:32:58
 */
@Service
@Slf4j
public class NewTableServiceImpl extends ServiceImpl<NewTableMapper, NewTable>
        implements NewTableService {
    @Resource
    private NewTableMapper newTableMapper;

    @Resource
    private NewColumnMapper newColumnMapper;

    @Autowired
    private TableConfig tableConfig;

    @Resource
    private RelationOfNewtableMapper relationOfNewtableMapper;

    @Resource
    private NewdbQueryService newdbQueryService;

    @Resource
    private UserMapper userMapper;

    @Resource
    private UserTableMapper userTableMapper;

    @Resource
    private DepartmentTableMapper departmentTableMapper;

    @Override
    public JSONArray getFieldsList(Integer userId) {
        JSONArray result = new JSONArray();
        User user = userMapper.selectById(userId);
        if(user == null)
            throw new IllegalArgumentException("用户不存在！");
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

        List<RelationOfNewtable> relationOfNewtables = relationOfNewtableMapper.selectList(new QueryWrapper<RelationOfNewtable>().eq("pre_serial", 0).orderByAsc("pre_serial"));
//        循环每个大类
        for (RelationOfNewtable relationOfNewtable : relationOfNewtables) {
            NewTable maintable = newTableMapper.selectById(relationOfNewtable.getNewtableId());
            if(maintable == null)
                continue;
            JSONObject obj = new JSONObject();
            // 大类对象
            obj.put("id", maintable.getId());
            obj.put("label", maintable.getComment());
            JSONArray children = new JSONArray();
            List<RelationOfNewtable> subRelationTables = relationOfNewtableMapper.selectList(new QueryWrapper<RelationOfNewtable>().like("pre_serial", relationOfNewtable.getNewtableId()).in("newtable_id", tableIds).or().eq("newtable_id", maintable.getId()).orderByAsc("newtable_id"));
            for (RelationOfNewtable subRelationTable : subRelationTables) {
                NewTable subTable = newTableMapper.selectById(subRelationTable.getNewtableId());
                if(subTable == null)
                    continue;
                List<NewColumn> columns = newColumnMapper.selectList(new QueryWrapper<NewColumn>().eq("newtable_id", subTable.getId()));
                List<ColumnVo> columnVos = new ArrayList<>();
                for (NewColumn column : columns) {
                    columnVos.add(new ColumnVo(column, subTable.getComment()));
                }
                children.add(new TableVo(subTable, columnVos));
            }
            obj.put("children", children);
            result.add(obj);
        }
        log.info("查询所有表格字段成功！");
        return result;
    }


    @Resource
    private JdbcTemplate jdbcTemplate;

    @Override
    public JSONArray humanListByid(String keyWord) throws BadSqlGrammarException {
        List<Map<String, Object>> humanList = newdbQueryService.queryForList(tableConfig.gethumanTable(), tableConfig.gethumanId(), keyWord);
        JSONArray jsonArray = new JSONArray();
        for (Map<String, Object> objectMap : humanList) {
            JSONObject jsonObject = new JSONObject(objectMap);
            jsonArray.add(jsonObject);
        }
        return jsonArray;
    }

    @Override
    public JSONArray humanListByName(String keyWord) throws BadSqlGrammarException {
//        List<Map<String, Object>> humanList = newTableMapper.tableNewSelect(tableConfig.gethumanTable(), tableConfig.gethumanName(), "'"+keyWord+"'");
        List<Map<String, Object>> humanList = newdbQueryService.queryLikeList(tableConfig.gethumanTable(), tableConfig.gethumanName(), keyWord);
        JSONArray jsonArray = new JSONArray();
        for (Map<String, Object> objectMap : humanList) {
            JSONObject jsonObject = new JSONObject(objectMap);
            jsonArray.add(jsonObject);
        }
        return jsonArray;
    }

    @Override
    public JSONArray companyList(String keyWord) throws BadSqlGrammarException {
        JSONArray jsonArray = new JSONArray();
        List<Map<String, Object>> companyListById = newdbQueryService.queryForList(tableConfig.getcompanyTable(), tableConfig.getcompanyId(), keyWord);
        for (Map<String, Object> objectMap : companyListById) {
            JSONObject jsonObject = new JSONObject(objectMap);
            jsonArray.add(jsonObject);
        }
        List<Map<String, Object>> companyListByName = newdbQueryService.queryForList(tableConfig.getcompanyTable(), tableConfig.getcompanyName(), keyWord);
        for (Map<String, Object> objectMap : companyListByName) {
            JSONObject jsonObject = new JSONObject(objectMap);
            jsonArray.add(jsonObject);
        }
        return jsonArray;
    }

    @Override
    public JSONObject searchListByKeyWord(List<Integer> tables, String keyWord) {
        JSONObject result = new JSONObject();
        for (Integer tableId : tables) {
            NewTable newTable = newTableMapper.selectById(tableId);
            if(newTable == null)
                continue;
            String label = newTable.getComment();
            List<RelationOfNewtable> relationOfNewtables = relationOfNewtableMapper.selectList(new QueryWrapper<RelationOfNewtable>().eq("newtable_id", tableId));
            List<String> tableTypes = new ArrayList<>();
            for (RelationOfNewtable relationOfNewtable : relationOfNewtables) {
                tableTypes.add(relationOfNewtable.getLabel());
            }
            String tablename = newTable.getTablename();

            List<NewColumn> columns = newColumnMapper.selectList(new QueryWrapper<NewColumn>().eq("newtable_id", tableId).ne("columnname", "id").ne("columnname", "Id"));
            String fields = "";
            String fieldsName = "";
            for (NewColumn column : columns) {
                if (!column.getColumnname().equals(tableConfig.getHumanFk())) {
                    String fname = column.getColumnname();
                    String alias = column.getComment();
                    fields += "," + column.getColumnname();
                    fieldsName += fname + " as '" + alias + "',";
                } else {
                    fields += "," + column.getColumnname();
                    fieldsName += column.getColumnname() + ",";
                }
            }

            if (fields.isEmpty()) {
                continue;
            } else {
                fieldsName = fieldsName.substring(0, fieldsName.length() - 1);
                List<Map<String, Object>> details = newdbQueryService.queryForDetail(tablename, fieldsName, fields, keyWord);
                JSONArray currentTableResult = new JSONArray();
                for (Map<String, Object> objectMap : details) {
                    LinkedHashMap<String, Object> tmpMap = new LinkedHashMap<>();
                    // 如果是人员类型,并且非主表，自动关联人员主表
                    if (tableTypes.contains("human") && !tablename.equals(tableConfig.gethumanTable()) && objectMap.get(tableConfig.getHumanFk()) != null) {
                        int id = Integer.parseInt(objectMap.get(tableConfig.getHumanFk()).toString());
                        List<Map<String, Object>> mainTable = newdbQueryService.queryForListById(tableConfig.gethumanTable(), tableConfig.gethumanPk(), id);
                        if (!mainTable.isEmpty()) {
                            tmpMap.put("姓名", mainTable.get(0).get(tableConfig.gethumanName()));
                            tmpMap.put("身份证号", mainTable.get(0).get(tableConfig.gethumanId()));
                        } else {
                            continue;
                        }
                    }
                    tmpMap.putAll(objectMap);
                    tmpMap.remove("id");
                    tmpMap.remove("Id");
                    tmpMap.remove(tableConfig.getHumanFk());
                    currentTableResult.add(tmpMap);
                }
                if(currentTableResult.size()>0){
                    result.put(label,currentTableResult);
                }
            }
        }
        return result;
    }

    @Override
    public ArrayList<AuthTable> getUserAuthTables(Integer userId) {
        User user = userMapper.selectById(userId);
        if(user == null)
            throw new IllegalArgumentException("人员ID:"+userId+"不存在！");
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

        ArrayList<AuthTable> authTables = new ArrayList<>();

        List<NewTable> newTables = newTableMapper.selectList(new QueryWrapper<NewTable>().in("Id", tableIds).orderByAsc("Id"));
        for (NewTable newTable : newTables) {
            authTables.add(new AuthTable(newTable));
        }
        return authTables;

    }

    @Override
    public ArrayList<AuthTable> getAllTables() {
        List<NewTable> newTables = newTableMapper.selectList(new QueryWrapper<>());
        ArrayList<AuthTable> authTables = new ArrayList<>();
        for (NewTable newTable : newTables) {
            authTables.add(new AuthTable(newTable));
        }
        return authTables;
    }

    @Override
    public JSONObject humanIdPortrait(String keyWord, Integer userId) throws IllegalArgumentException, BadSqlGrammarException {
        String Id = keyWord;
        String humanTableName = tableConfig.gethumanTable();
        String humanPk = tableConfig.gethumanPk();
//        根据ID检索人员主表
        List<Map<String, Object>> maps = newdbQueryService.queryForList(humanTableName, humanPk, Id);
        if (maps.isEmpty()) {
            return null;
        } else {
            Map<String, Object> result = new LinkedHashMap<>();
            User user = userMapper.selectById(userId);
            if(user == null)
                throw new IllegalArgumentException("人员ID:"+userId+"不存在！");
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

//            查询人员主表的标签
            NewTable humanBaseTable = newTableMapper.selectOne(new QueryWrapper<NewTable>().eq("tablename", humanTableName));
            if (humanBaseTable == null) {
                throw new IllegalArgumentException("人员基本信息表" + humanTableName + "不存在！");
            }
            String baseTag = humanBaseTable.getComment();
            Integer baseId = humanBaseTable.getId();
            JSONArray baseInfo = new JSONArray();
            // 循环人员主表中的所有列
            for (String s : maps.get(0).keySet()) {
                String key = s;
                if(key.equals("id") || key.equals("Id") || key.equals("ID")){
                    continue;
                }
                String value = maps.get(0).get(key).toString();
                NewColumn newColumn = newColumnMapper.selectOne(new QueryWrapper<NewColumn>().eq("newtable_id", baseId).eq("columnname", key));
                String label = "";
                if (newColumn != null) {
                    label = (newColumn.getComment()==null || newColumn.getComment().isEmpty())?key:newColumn.getComment();
                } else label = key;
                JSONObject oneInfo = new JSONObject();
                oneInfo.put("key", key);
                oneInfo.put("label", label);
                oneInfo.put("value", value);
                baseInfo.add(oneInfo);
            }
            // 将人员主表中的查询结果放入对象
            result.put(baseTag, baseInfo);

            // 查询所有人物相关的数据库表
            List<RelationOfNewtable> tables = relationOfNewtableMapper.selectList(new QueryWrapper<RelationOfNewtable>().like("pre_serial", humanBaseTable.getId()).in("newtable_id", tableIds));
            tables.forEach(
                    table -> {
                        Integer relationTableId = table.getNewtableId();
                        NewTable relationTable = newTableMapper.selectById(relationTableId);
                        if(relationTable == null)
                            return;
                        String relationTablename = relationTable.getTablename();
//                        NewColumn foreignKeyColumn = newColumnMapper.selectOne(new QueryWrapper<NewColumn>().eq("newtable_id", relationTableId).eq("flag", 1));
//                        String fk = table.getFk();
                        List<NewColumn> foreignKeyColumn = newColumnMapper.selectList(new QueryWrapper<NewColumn>().eq("newtable_id", relationTableId).eq("columnname", tableConfig.getHumanFk()));
                        if (foreignKeyColumn.size() == 1) {
                            String fk = tableConfig.getHumanFk();
                            List<Map<String, Object>> detail;
                            List<NewColumn> columns = newColumnMapper.selectList(new QueryWrapper<NewColumn>().eq("newtable_id", relationTableId).notIn("columnname", Arrays.asList("Id", "pbinfo_id")));
                            if (!columns.isEmpty()) {
                                String fields = "";
                                for (NewColumn column : columns) {
                                    String fname = column.getColumnname();
                                    String alias = column.getComment();
                                    fields += fname + " as '" + alias + "',";
                                }
                                fields = fields.substring(0, fields.length() - 1);
                                detail = newdbQueryService.queryForListByFields(relationTablename, fields, fk, Id);
                            } else detail = newdbQueryService.queryForList(relationTablename, fk, Id);
                            if (!detail.isEmpty()) {
                                String tag = relationTable.getComment();
                                result.put(tag, detail);
                            }
                        }
                    }
            );

            return new JSONObject(result);
        }
    }

    @Override
    public JSONObject companyPortrait(String keyWord, Integer userId) {
        String companyTableName = tableConfig.getcompanyTable();
        String companyTableId = tableConfig.getcompanyId();
        String companyPk = tableConfig.getcompanyPk();
//        根据关键字检索单位主表,关键字可能是单位id,也可能是单位名称，不管是啥在主表里都是唯一性
        List<Map<String, Object>> maps = newdbQueryService.queryForList(companyTableName, companyTableId, keyWord);
        if (maps.isEmpty()) {
            return null;
        } else {
            JSONObject result = new JSONObject();
            User user = userMapper.selectById(userId);
            if(user == null)
                throw new IllegalArgumentException("人员ID:"+userId+"不存在！");
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
//            查询单位主表的标签
            NewTable companyBaseTable = newTableMapper.selectOne(new QueryWrapper<NewTable>().eq("tablename", companyTableName));
            if (companyBaseTable == null) {
                throw new IllegalArgumentException("单位基本信息表" + companyTableName + "不存在！");
            }
            String baseTag = companyBaseTable.getComment();
            // 将单位主表中的查询结果放入对象
            result.put(baseTag, maps.get(0));


            // 查询所有单位相关的数据库表
            List<RelationOfNewtable> tables = relationOfNewtableMapper.selectList(new QueryWrapper<RelationOfNewtable>().like("pre_serial", companyBaseTable.getId()).in("newtable_id", tableIds));
            tables.forEach(
                    table -> {
                        Integer relationTableId = table.getNewtableId();
                        NewTable relationTable = newTableMapper.selectById(relationTableId);
                        if(relationTable == null){
                            return;
                        }
                        String relationTablename = relationTable.getTablename();
//                        NewColumn foreignKeyColumn = newColumnMapper.selectOne(new QueryWrapper<NewColumn>().eq("newtable_id", relationTableId).eq("flag", 1));

                        List<NewColumn> foreignKeyColumn = newColumnMapper.selectList(new QueryWrapper<NewColumn>().eq("newtable_id", relationTableId).eq("columnname", tableConfig.getCompanyFk()));
                        if (foreignKeyColumn.size() == 1) {
                            String fk = tableConfig.getCompanyFk();
                            List<Map<String, Object>> detail = newdbQueryService.queryForList(relationTablename, fk, companyPk);
                            if (!detail.isEmpty()) {
                                String tag = relationTable.getComment();
                                result.put(tag, detail);
                            }
                        }
                    }
            );

            return result;
        }
    }


}




