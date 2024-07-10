package com.wxjw.jwbigdata.mapper;

import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@Slf4j
@SpringBootTest
class FileInfoMapperTest {

    @Resource
    FileInfoMapper fileInfoMapper;

    @Test
    void createTable() {

    }

    @Test
    void existTable() {
    }

    @Test
    void selectColumns() {
        log.info("=====测试查询某一字段的全部数据开始=====");
        List<String> strings = fileInfoMapper.selectColumns(fileInfoMapper.selectById(153).getTableName(), "date");
        System.out.println(strings);
        log.info("=====测试查询某一字段的全部数据结束=====");
    }

    @Test
    void getTableColumns() {
    }

    @Test
    void getByPosition() {
    }

    @Test
    void countData() {
        log.info("=====测试查询某表的数据个数开始=====");
        Integer count = fileInfoMapper.countData(fileInfoMapper.selectById(153).getTableName());
        System.out.println(count);
        log.info("=====测试查询某表的数据个数结束=====");
    }
}