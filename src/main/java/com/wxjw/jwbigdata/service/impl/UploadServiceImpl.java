package com.wxjw.jwbigdata.service.impl;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wxjw.jwbigdata.mapper.OperationMapper;
import jakarta.annotation.Resource;
import org.springframework.jdbc.BadSqlGrammarException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * @author black
 * @version 1.0.0
 * @className UploadServiceImpl
 * @description TODO
 * @date 2024-12-31 10:59
 **/
@DS("uploaddb")
@Service
public class UploadServiceImpl {
    @Resource
    private JdbcTemplate jdbcTemplate;

    public List<String> getUploadList() throws BadSqlGrammarException{
        return jdbcTemplate.queryForList("select `TABLE_NAME` from information_schema.`TABLES` where `TABLE_SCHEMA` = 'uploaddb'",String.class);
    }
}
