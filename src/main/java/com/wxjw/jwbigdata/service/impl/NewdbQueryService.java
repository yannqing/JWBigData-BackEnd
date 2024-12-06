package com.wxjw.jwbigdata.service.impl;

import com.baomidou.dynamic.datasource.annotation.DS;
import jakarta.annotation.Resource;
import org.springframework.jdbc.BadSqlGrammarException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.SingleColumnRowMapper;
import org.springframework.stereotype.Service;

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

    public List<Map<String, Object>> queryForListByFields(String table, String fields,String field, String value) throws BadSqlGrammarException {
        return jdbcTemplate.queryForList("select "+fields+" from "+table+" where "+field+" = '"+value+"'");
    }

    public String getCreateTableQuery(String tableName) {
        List<Map<String, Object>> createTableQueries = jdbcTemplate.queryForList(
                "SHOW CREATE TABLE " + tableName);
        // 通常情况下，仅一个结果，但是为了安全处理，我们返回第一个（如果存在）
        return createTableQueries.isEmpty() ? null : createTableQueries.get(0).get("Create Table").toString();
    }
}
