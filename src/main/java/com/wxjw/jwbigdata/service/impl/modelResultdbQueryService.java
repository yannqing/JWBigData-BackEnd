package com.wxjw.jwbigdata.service.impl;

import com.baomidou.dynamic.datasource.annotation.DS;
import jakarta.annotation.Resource;
import org.springframework.jdbc.BadSqlGrammarException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * @author panhao
 * @version 1.0.0
 * @className modelResultdbQueryService
 * @description TODO
 * @date 2024-10-19 18:44
 **/
@DS("modelresultdb")
@Service
public class modelResultdbQueryService {
    @Resource
    private JdbcTemplate jdbcTemplate;

    public List<Map<String, Object>> tableExist(String tableName) throws BadSqlGrammarException {
        return jdbcTemplate.queryForList("select * from information_schema.`TABLES` where TABLE_NAME= '"+tableName+"'");
    }

    public List<Map<String, Object>> resultTable(String tableName) throws BadSqlGrammarException {
        return jdbcTemplate.queryForList("select * from `"+tableName+"`");
    }

    public List<Map<String, Object>> resultDetail(String table, String field, String value) throws BadSqlGrammarException {
        return jdbcTemplate.queryForList("select * from `"+table+"` where "+field+" = '"+value+"'");
    }

    public void createNewTable(String tableName,String sqlStatement) throws BadSqlGrammarException{
        jdbcTemplate.execute("CREATE TABLE `" + tableName + "` " + sqlStatement);
    }
}
