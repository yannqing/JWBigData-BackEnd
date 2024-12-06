package com.wxjw.jwbigdata.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wxjw.jwbigdata.config.TableConfig;
import com.wxjw.jwbigdata.domain.NewColumn;
import com.wxjw.jwbigdata.domain.NewTable;
import com.wxjw.jwbigdata.domain.RelationOfNewtable;
import com.wxjw.jwbigdata.mapper.NewColumnMapper;
import com.wxjw.jwbigdata.mapper.RelationOfNewtableMapper;
import com.wxjw.jwbigdata.service.NewTableService;
import com.wxjw.jwbigdata.mapper.NewTableMapper;
import com.wxjw.jwbigdata.vo.TableVo.ColumnVo;
import com.wxjw.jwbigdata.vo.TableVo.TableVo;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.BadSqlGrammarException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

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

    @Override
    public JSONArray getFieldsList() {
        JSONArray result = new JSONArray();
        List<RelationOfNewtable> relationOfNewtables = relationOfNewtableMapper.selectList(new QueryWrapper<RelationOfNewtable>().eq("pre_serial", 0).orderByAsc("pre_serial"));
//        循环每个大类
        for (RelationOfNewtable relationOfNewtable : relationOfNewtables) {
            NewTable maintable = newTableMapper.selectById(relationOfNewtable.getNewtableId());
            JSONObject obj = new JSONObject();
            // 大类对象
            obj.put("id",maintable.getId());
            obj.put("label",maintable.getComment());
            JSONArray children = new JSONArray();
            List<RelationOfNewtable> subRelationTables = relationOfNewtableMapper.selectList(new QueryWrapper<RelationOfNewtable>().like("pre_serial", relationOfNewtable.getNewtableId()).or().eq("newtable_id",maintable.getId()).orderByAsc("newtable_id"));
            for (RelationOfNewtable subRelationTable : subRelationTables) {
                NewTable subTable = newTableMapper.selectById(subRelationTable.getNewtableId());
                List<NewColumn> columns = newColumnMapper.selectList(new QueryWrapper<NewColumn>().eq("newtable_id",subTable.getId()));
                List<ColumnVo> columnVos = new ArrayList<>();
                for (NewColumn column : columns) {
                    columnVos.add(new ColumnVo(column,subTable.getComment()));
                }
                children.add(new TableVo(subTable,columnVos));
            }
            obj.put("children",children);
            result.add(obj);
        }
        log.info("查询所有规则成功！");
        return result;
    }


    @Resource
    private JdbcTemplate jdbcTemplate;

    @Override
    public JSONArray humanListByid(String keyWord)  throws BadSqlGrammarException{
        List<Map<String, Object>> humanList = newdbQueryService.queryForList(tableConfig.gethumanTable(),tableConfig.gethumanId(),keyWord);
        JSONArray jsonArray = new JSONArray();
        for (Map<String, Object> objectMap : humanList) {
            JSONObject jsonObject = new JSONObject(objectMap);
            jsonArray.add(jsonObject);
        }
        return jsonArray;
    }

    @Override
    public JSONArray humanListByName(String keyWord)  throws BadSqlGrammarException{
//        List<Map<String, Object>> humanList = newTableMapper.tableNewSelect(tableConfig.gethumanTable(), tableConfig.gethumanName(), "'"+keyWord+"'");
        List<Map<String, Object>> humanList = newdbQueryService.queryForList(tableConfig.gethumanTable(),tableConfig.gethumanName(),keyWord);
        JSONArray jsonArray = new JSONArray();
        for (Map<String, Object> objectMap : humanList) {
            JSONObject jsonObject = new JSONObject(objectMap);
            jsonArray.add(jsonObject);
        }
        return jsonArray;
    }

    @Override
    public JSONArray companyList(String keyWord)  throws BadSqlGrammarException{
        JSONArray jsonArray = new JSONArray();
        List<Map<String, Object>> companyListById = newdbQueryService.queryForList(tableConfig.getcompanyTable(),tableConfig.getcompanyId(),keyWord);
        for (Map<String, Object> objectMap : companyListById) {
            JSONObject jsonObject = new JSONObject(objectMap);
            jsonArray.add(jsonObject);
        }
        List<Map<String, Object>> companyListByName = newdbQueryService.queryForList(tableConfig.getcompanyTable(),tableConfig.getcompanyName(),keyWord);
        for (Map<String, Object> objectMap : companyListByName) {
            JSONObject jsonObject = new JSONObject(objectMap);
            jsonArray.add(jsonObject);
        }
        return jsonArray;
    }

    @Override
    public JSONObject humanIdPortrait(String keyWord)  throws IllegalArgumentException, BadSqlGrammarException{
        String Id = keyWord;
        String humanTableName = tableConfig.gethumanTable();
        String humanPk = tableConfig.gethumanPk();
//        根据ID检索人员主表
        List<Map<String, Object>> maps = newdbQueryService.queryForList(humanTableName, humanPk, Id);
        if(maps.isEmpty()){
            return null;
        }
        else{
            Map<String, Object> result = new LinkedHashMap<>();

//            查询人员主表的标签
            NewTable humanBaseTable = newTableMapper.selectOne(new QueryWrapper<NewTable>().eq("tablename",humanTableName));
            if(humanBaseTable == null){
                throw new IllegalArgumentException("人员基本信息表"+humanTableName+"不存在！");
            }
            String baseTag = humanBaseTable.getComment();
            Integer baseId = humanBaseTable.getId();
            JSONArray baseInfo = new JSONArray();
            for (String s : maps.get(0).keySet()) {
                String key = s;
                String value = maps.get(0).get(key).toString();
                NewColumn newColumn = newColumnMapper.selectOne(new QueryWrapper<NewColumn>().eq("newtable_id", baseId).eq("columnname", key));
                String label = "";
                if(newColumn != null){
                    label = newColumn.getComment();
                }
                else label = key;
                JSONObject oneInfo = new JSONObject();
                oneInfo.put("key",key);
                oneInfo.put("label",label);
                oneInfo.put("value",value);
                baseInfo.add(oneInfo);
            }
            // 将人员主表中的查询结果放入对象
            result.put(baseTag,baseInfo);

            // 查询所有人物相关的数据库表
            List<RelationOfNewtable> tables = relationOfNewtableMapper.selectList(new QueryWrapper<RelationOfNewtable>().like("pre_serial", humanBaseTable.getId()));
            tables.forEach(
                    table ->{
                        Integer relationTableId = table.getId();
                        NewTable relationTable = newTableMapper.selectById(relationTableId);
                        String relationTablename = relationTable.getTablename();
//                        NewColumn foreignKeyColumn = newColumnMapper.selectOne(new QueryWrapper<NewColumn>().eq("newtable_id", relationTableId).eq("flag", 1));
//                        String fk = table.getFk();
                        List<NewColumn> foreignKeyColumn = newColumnMapper.selectList(new QueryWrapper<NewColumn>().eq("newtable_id", relationTableId).eq("columnname", tableConfig.getHumanFk()));
                        if(foreignKeyColumn.size() == 1){
                            String fk = tableConfig.getHumanFk();
                            List<Map<String, Object>> detail;
                            List<NewColumn> columns = newColumnMapper.selectList(new QueryWrapper<NewColumn>().eq("newtable_id", relationTableId));
                            if(!columns.isEmpty()){
                                String fields = "";
                                for (NewColumn column : columns) {
                                    String fname = column.getColumnname();
                                    String alias = column.getComment();
                                    fields += fname + " as '"+alias+"',";
                                }
                                fields = fields.substring(0,fields.length()-1);
                                detail = newdbQueryService.queryForListByFields(relationTablename,fields,fk , Id);
                            }
                            else detail = newdbQueryService.queryForList(relationTablename,fk , Id);
                            if(!detail.isEmpty()){
                                String tag =relationTable.getComment();
                                result.put(tag,detail);
                            }
                        }
                    }
            );

            return new JSONObject(result);
        }
    }

    @Override
    public JSONObject companyPortrait(String keyWord) {
        String companyTableName = tableConfig.getcompanyTable();
        String companyTableId = tableConfig.getcompanyId();
        String companyPk = tableConfig.getcompanyPk();
//        根据关键字检索单位主表,关键字可能是单位id,也可能是单位名称，不管是啥在主表里都是唯一性
        List<Map<String, Object>> maps = newdbQueryService.queryForList(companyTableName, companyTableId, keyWord);
        if(maps.isEmpty()){
            return null;
        }
        else {
            JSONObject result = new JSONObject();
//            查询单位主表的标签
            NewTable companyBaseTable = newTableMapper.selectOne(new QueryWrapper<NewTable>().eq("tablename", companyTableName));
            if (companyBaseTable == null) {
                throw new IllegalArgumentException("单位基本信息表" + companyTableName + "不存在！");
            }
            String baseTag = companyBaseTable.getComment();
            // 将单位主表中的查询结果放入对象
            result.put(baseTag, maps.get(0));


            // 查询所有单位相关的数据库表
            List<RelationOfNewtable> tables = relationOfNewtableMapper.selectList(new QueryWrapper<RelationOfNewtable>().like("pre_serial", companyBaseTable.getId()));
            tables.forEach(
                    table -> {
                        Integer relationTableId = table.getId();
                        NewTable relationTable = newTableMapper.selectById(relationTableId);
                        String relationTablename = relationTable.getTablename();
//                        NewColumn foreignKeyColumn = newColumnMapper.selectOne(new QueryWrapper<NewColumn>().eq("newtable_id", relationTableId).eq("flag", 1));

                        List<NewColumn> foreignKeyColumn = newColumnMapper.selectList(new QueryWrapper<NewColumn>().eq("newtable_id", relationTableId).eq("columnname", tableConfig.getCompanyFk()));
                        if(foreignKeyColumn.size() == 1){
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




