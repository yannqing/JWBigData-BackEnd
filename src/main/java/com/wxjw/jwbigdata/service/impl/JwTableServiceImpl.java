package com.wxjw.jwbigdata.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wxjw.jwbigdata.config.TableConfig;
import com.wxjw.jwbigdata.domain.JwField;
import com.wxjw.jwbigdata.domain.JwTable;
import com.wxjw.jwbigdata.domain.User;
import com.wxjw.jwbigdata.mapper.JwFieldMapper;
import com.wxjw.jwbigdata.mapper.JwRuleMapper;
import com.wxjw.jwbigdata.service.JwTableService;
import com.wxjw.jwbigdata.mapper.JwTableMapper;
import com.wxjw.jwbigdata.vo.TableVo.TableVo;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.oauth2.resource.OAuth2ResourceServerProperties;
import org.springframework.stereotype.Service;

import java.sql.PreparedStatement;
import java.util.List;
import java.util.Map;

/**
* @author Paul
* @description 针对表【jw_table】的数据库操作Service实现
* @createDate 2024-06-14 14:08:08
*/
@Service
@Slf4j
public class JwTableServiceImpl extends ServiceImpl<JwTableMapper, JwTable>
    implements JwTableService{

    @Resource
    private JwTableMapper jwTableMapper;

    @Resource
    private JwFieldMapper jwFieldMapper;

    @Autowired
    private TableConfig tableConfig;

    @Override
    public JSONArray getFieldsList() {
        List<JwTable> tables = jwTableMapper.selectList(null);
        JSONArray result = new JSONArray();
        tables.forEach(
                table ->{
                    Integer tableId = table.getTableId();
                    List<JwField> fields = jwFieldMapper.selectList(new QueryWrapper<JwField>().eq("table_id",tableId));
                    result.add(new TableVo(table,fields));
                }
        );
        log.info("查询所有规则成功！");
        return result;
    }

    @Override
    public JSONArray humanIdPortrait(String keyWord) {
        String humanId = keyWord;
        List<Map<String, Object>> maps = jwTableMapper.tableSelect(tableConfig.gethumanTable(), tableConfig.gethumanId(), humanId);
        if(maps.isEmpty()){
            return null;
        }
        else{
            JSONArray results = new JSONArray();
            JSONObject result = new JSONObject();
            // 将基本表中的查询结果放入对象
            JwTable humanBaseTable = jwTableMapper.selectOne(new QueryWrapper<JwTable>().eq("table_name", tableConfig.gethumanTable()));
            if(humanBaseTable == null){
                return null;
            }
            String baseTag = humanBaseTable.getTag();
            result.put(baseTag,humanBaseTable);

            // 查询所有人物相关的数据库表
            List<JwTable> tables = jwTableMapper.selectList(new QueryWrapper<JwTable>().eq("type", 0));
            tables.forEach(
                    table ->{
                        List<JwField> jwFields = jwFieldMapper.selectList(new QueryWrapper<JwField>().eq("table_id", table.getTableId()).eq("field_name", tableConfig.gethumanId()));
                        if(jwFields.isEmpty())
                        {
                            return;
                        }
                        String tag = table.getTag();
                        String tableName = table.getTableName();
                        List<Map<String, Object>> detail = jwTableMapper.tableSelect(tableName, tableConfig.gethumanId(), humanId);
                        result.put(tag,detail);
                    }
            );
            results.add(result);
            return results;
        }
    }

    @Override
    public JSONArray humanNamePortrait(String keyWord) {
        String humanName = keyWord;
        List<Map<String, Object>> maps = jwTableMapper.tableSelect(tableConfig.gethumanTable(), tableConfig.gethumanName(), humanName);
        if(maps.isEmpty()){
            return null;
        }
        else{
            JSONArray results = new JSONArray();
            maps.forEach(
                    map ->{
                        JSONObject result = new JSONObject();
                        // 将基本表中的查询结果放入对象
                        JwTable humanBaseTable = jwTableMapper.selectOne(new QueryWrapper<JwTable>().eq("table_name", tableConfig.gethumanTable()));
                        String baseTag = humanBaseTable.getTag();
                        result.put(baseTag,humanBaseTable);

                        String humanId = map.get(tableConfig.gethumanId()).toString();
                        // 查询所有人物相关的数据库表
                        List<JwTable> tables = jwTableMapper.selectList(new QueryWrapper<JwTable>().eq("type", 0));
                        tables.forEach(
                                table ->{
                                    List<JwField> jwFields = jwFieldMapper.selectList(new QueryWrapper<JwField>().eq("table_id", table.getTableId()).eq("field_name", tableConfig.gethumanId()));
                                    if(jwFields.isEmpty())
                                    {
                                        return;
                                    }
                                    String tag = table.getTag();
                                    String tableName = table.getTableName();
                                    List<Map<String, Object>> detail = jwTableMapper.tableSelect(tableName, tableConfig.gethumanId(), humanId);
                                    result.put(tag,detail);
                                }
                        );
                        results.add(result);
                    }
            );
            return results;
        }
    }

    @Override
    public JSONArray companyNamePortrait(String keyWord) {
        String companyName = keyWord;
        List<Map<String, Object>> maps = jwTableMapper.tableSelect(tableConfig.getcompanyTable(), tableConfig.getcompanyName(), companyName);
        if(maps.isEmpty()){
            return null;
        }
        else{
            JSONArray results = new JSONArray();
            maps.forEach(
                    map ->{
                        JSONObject result = new JSONObject();
                        // 将基本表中的查询结果放入对象
                        JwTable humanBaseTable = jwTableMapper.selectOne(new QueryWrapper<JwTable>().eq("table_name", tableConfig.getcompanyTable()));
                        String baseTag = humanBaseTable.getTag();
                        result.put(baseTag,humanBaseTable);

                        String companyId = map.get(tableConfig.getcompanyId()).toString();
                        // 查询所有人物相关的数据库表
                        List<JwTable> tables = jwTableMapper.selectList(new QueryWrapper<JwTable>().eq("type", 0));
                        tables.forEach(
                                table ->{
                                    List<JwField> jwFields = jwFieldMapper.selectList(new QueryWrapper<JwField>().eq("table_id", table.getTableId()).eq("field_name", tableConfig.getcompanyId()));
                                    if(jwFields.isEmpty())
                                    {
                                        return;
                                    }
                                    String tag = table.getTag();
                                    String tableName = table.getTableName();
                                    List<Map<String, Object>> detail = jwTableMapper.tableSelect(tableName, tableConfig.getcompanyId(), companyId);
                                    result.put(tag,detail);
                                }
                        );
                        results.add(result);
                    }
            );
            return results;
        }
    }
}




