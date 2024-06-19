package com.wxjw.jwbigdata.FileController;

import com.alibaba.excel.EasyExcel;
import com.wxjw.jwbigdata.listener.excel.ExcelListener;
import com.wxjw.jwbigdata.mapper.FileInfoMapper;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Repeat;

import java.io.File;

@SpringBootTest
public class TestExcel {

    @Resource
    private FileInfoMapper fileInfoMapper;

    /**
     * 不创建对象的读
     */
    @Test
    public void noModelRead() {
        String fileName = "C:\\Users\\67121\\Desktop\\time.xlsx";
        // 这里 只要，然后读取第一个sheet 同步读取会自动finish
        EasyExcel.read(fileName, new ExcelListener(0, "love2", fileInfoMapper)).sheet().doRead();
    }
}
