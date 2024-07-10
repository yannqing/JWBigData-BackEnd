package com.wxjw.jwbigdata.service;

import jakarta.annotation.Resource;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class FileInfoServiceTest {

    @Resource
    private FileInfoService fileInfoService;

    @Test
    void openFile() {
        String[][] strings = fileInfoService.openFile(1, 153);
        System.out.println(strings);
    }
}