package com.wxjw.jwbigdata.mapper;

import jakarta.annotation.Resource;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class OperationMapperTest {

    @Resource
    private OperationMapper operationMapper;

    @Test
    void getData() {
    }

    @Test
    void dropTable() {
        operationMapper.dropTable("e246cb22e4204300a95d4f406293d9e0_copy1");
    }

    @Test
    void dynamicInsert() {
    }
}