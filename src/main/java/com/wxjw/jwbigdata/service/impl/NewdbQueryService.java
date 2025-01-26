package com.wxjw.jwbigdata.service.impl;

import com.baomidou.dynamic.datasource.annotation.DS;
import jakarta.annotation.Resource;
import org.springframework.jdbc.BadSqlGrammarException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.SingleColumnRowMapper;
import org.springframework.stereotype.Service;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * @author black
 * @version 1.0.0
 * @className QueryService
 * @description TODO
 * @date 2024-10-16 11:37
 **/
@DS("newdb")
@Service
public class NewdbQueryService {
    @Resource
    private JdbcTemplate jdbcTemplate;

    public List<Map<String, Object>> queryForList(String table) throws BadSqlGrammarException {
        return jdbcTemplate.queryForList("select * from "+table);
    }

    public List<Map<String, Object>> queryForList(String table, String field, String value) throws BadSqlGrammarException {
        return jdbcTemplate.queryForList("select * from "+table+" where "+field+" = '"+value+"'");
    }

    public List<Map<String, Object>> queryLikeList(String table, String field, String value) throws BadSqlGrammarException {
        return jdbcTemplate.queryForList("select * from "+table+" where "+field+" like '%"+value+"%'");
    }

    public List<Map<String, Object>> queryForListById(String table, String field, int id) throws BadSqlGrammarException {
        return jdbcTemplate.queryForList("select * from "+table+" where "+field+" = "+id);
    }

    public List<Map<String, Object>> queryForListByFields(String table, String fields,String field, String value) throws BadSqlGrammarException {
        return jdbcTemplate.queryForList("select "+fields+" from "+table+" where "+field+" = '"+value+"'");
    }

    public String getCreateTableQuery(String tableName) {
        List<Map<String, Object>> createTableQueries = jdbcTemplate.queryForList(
                "SHOW CREATE TABLE " + tableName);
        // 通常情况下，仅一个结果，但是为了安全处理，我们返回第一个（如果存在）
        return createTableQueries.isEmpty() ? null : createTableQueries.get(0).get("Create Table").toString();
    }

    public List<Integer> queryForIdList(String table,String fk, String field, String value) throws BadSqlGrammarException {
        return jdbcTemplate.queryForList("select "+fk+" from "+table+" where CONCAT_WS(' '"+field+") like '%"+value+"%'",Integer.class);
    }

    public List<Map<String, Object>> queryForDetail(String table, String fieldsName, String field, String value) throws BadSqlGrammarException {
        return jdbcTemplate.queryForList("select "+fieldsName+" from "+table+" where CONCAT_WS(' '"+field+") like '%"+value+"%'");
    }


    public List<Map<String, Object>> queryForListIn(String table, String field, String value) throws BadSqlGrammarException {
        return jdbcTemplate.queryForList("select * from "+table+" where "+field+" in ("+value+")");
    }
}
