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
import com.wxjw.jwbigdata.service.RelationOfNewtableService;
import com.wxjw.jwbigdata.vo.TableVo.TableVo;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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

    @Override
    public JSONArray getFieldsList() {
        List<NewTable> tables = newTableMapper.selectList(null);
        JSONArray result = new JSONArray();
        tables.forEach(
                table ->{
                    Integer tableId = table.getId();
                    List<NewColumn> columns = newColumnMapper.selectList(new QueryWrapper<NewColumn>().eq("newtable_id",tableId));
                    result.add(new TableVo(table,columns));
                }
        );
        log.info("查询所有规则成功！");
        return result;
    }

    @Override
    public JSONArray humanListByid(String keyWord) {
        List<Map<String, Object>> humanList = newTableMapper.tableCleanSelect(tableConfig.gethumanTable(), tableConfig.gethumanId(), keyWord);
        JSONArray jsonArray = new JSONArray();
        for (Map<String, Object> objectMap : humanList) {
            JSONObject jsonObject = new JSONObject(objectMap);
            jsonArray.add(jsonObject);
        }
        return jsonArray;
    }

    @Override
    public JSONArray humanListByName(String keyWord) {
        List<Map<String, Object>> humanList = newTableMapper.tableCleanSelect(tableConfig.gethumanTable(), tableConfig.gethumanName(), keyWord);
        JSONArray jsonArray = new JSONArray();
        for (Map<String, Object> objectMap : humanList) {
            JSONObject jsonObject = new JSONObject(objectMap);
            jsonArray.add(jsonObject);
        }
        return jsonArray;
    }

    @Override
    public JSONArray companyList(String keyWord) {
        JSONArray jsonArray = new JSONArray();
        List<Map<String, Object>> humanListById = newTableMapper.tableCleanSelect(tableConfig.getcompanyTable(), tableConfig.getcompanyId(), keyWord);
        for (Map<String, Object> objectMap : humanListById) {
            JSONObject jsonObject = new JSONObject(objectMap);
            jsonArray.add(jsonObject);
        }
        List<Map<String, Object>> humanListByName = newTableMapper.tableCleanSelect(tableConfig.getcompanyTable(), tableConfig.getcompanyName(), keyWord);
        for (Map<String, Object> objectMap : humanListByName) {
            JSONObject jsonObject = new JSONObject(objectMap);
            jsonArray.add(jsonObject);
        }
        return jsonArray;
    }

    @Override
    public JSONObject humanIdPortrait(String keyWord) {
        String humanId = keyWord;
//        根据身份证号检索人员主表
        List<Map<String, Object>> maps = newTableMapper.tableCleanSelect(tableConfig.gethumanTable(), tableConfig.gethumanId(), humanId);
        if(maps.isEmpty()){
            return null;
        }
        else{
            JSONObject result = new JSONObject();
//            查询人员主表的标签
            NewTable humanBaseTable = newTableMapper.selectOne(new QueryWrapper<NewTable>().eq("tablename",tableConfig.gethumanTable()));
            if(humanBaseTable == null){
                throw new IllegalArgumentException("人员基本信息表"+tableConfig.gethumanTable()+"不存在！");
            }
            String baseTag = humanBaseTable.getComment();
            // 将人员主表中的查询结果放入对象
            result.put(baseTag,maps.get(0));

            // 查询所有人物相关的数据库表
            List<RelationOfNewtable> tables = relationOfNewtableMapper.selectList(new QueryWrapper<RelationOfNewtable>().like("pre_serial", humanBaseTable.getId()));
            tables.forEach(
                    table ->{
                        Integer relationTableId = table.getId();
                        NewTable relationTable = newTableMapper.selectById(relationTableId);
                        String relationTablename = relationTable.getTablename();
                        NewColumn foreignKeyColumn = newColumnMapper.selectOne(new QueryWrapper<NewColumn>().eq("newtable_id", relationTableId).eq("flag", 1));
                        if(foreignKeyColumn != null){
                            List<Map<String, Object>> detail = newTableMapper.tableCleanSelect(relationTablename, foreignKeyColumn.getColumnname(), humanId);
                            if(!detail.isEmpty()){
                                String tag =relationTable.getComment();
                                result.put(tag,detail);
                            }
                        }
                    }
            );

            return result;
        }
    }

    @Override
    public JSONObject companyPortrait(String keyWord) {
//        根据关键字检索单位主表
        List<Map<String, Object>> maps = newTableMapper.tableCleanSelect(tableConfig.getcompanyTable(), tableConfig.getcompanyId(), keyWord);
        if(maps.isEmpty()){
            return null;
        }
        else {
            JSONObject result = new JSONObject();
//            查询单位主表的标签
            NewTable companyBaseTable = newTableMapper.selectOne(new QueryWrapper<NewTable>().eq("tablename", tableConfig.getcompanyTable()));
            if (companyBaseTable == null) {
                throw new IllegalArgumentException("单位基本信息表" + tableConfig.getcompanyTable() + "不存在！");
            }
            String baseTag = companyBaseTable.getComment();
            // 将单位主表中的查询结果放入对象
            result.put(baseTag, maps.get(0));

            // 查询所有人物相关的数据库表
            List<RelationOfNewtable> tables = relationOfNewtableMapper.selectList(new QueryWrapper<RelationOfNewtable>().like("pre_serial", companyBaseTable.getId()));
            tables.forEach(
                    table -> {
                        Integer relationTableId = table.getId();
                        NewTable relationTable = newTableMapper.selectById(relationTableId);
                        String relationTablename = relationTable.getTablename();
                        NewColumn foreignKeyColumn = newColumnMapper.selectOne(new QueryWrapper<NewColumn>().eq("newtable_id", relationTableId).eq("flag", 1));
                        if (foreignKeyColumn != null) {
                            List<Map<String, Object>> detail = newTableMapper.tableCleanSelect(relationTablename, foreignKeyColumn.getColumnname(), keyWord);
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




