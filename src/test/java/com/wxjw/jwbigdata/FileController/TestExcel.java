package com.wxjw.jwbigdata.FileController;

import com.alibaba.excel.EasyExcel;
import com.baomidou.mybatisplus.extension.toolkit.SqlRunner;
import com.wxjw.jwbigdata.config.MyBatisPlusConfig;
import com.wxjw.jwbigdata.listener.excel.ExcelListener;
import com.wxjw.jwbigdata.mapper.FileInfoMapper;
import com.wxjw.jwbigdata.mapper.OperationMapper;
import com.wxjw.jwbigdata.service.FileInfoService;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Repeat;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

@SpringBootTest
public class TestExcel {

    @Resource
    private FileInfoMapper fileInfoMapper;

    @Resource
    private FileInfoService fileInfoService;

    @Autowired
    private OperationMapper operationMapper;

    /**
     * 不创建对象的读
     */
    @Test
    public void noModelRead() {
        String fileName = "C:\\Users\\67121\\Desktop\\time.xlsx";
        // 这里 只要，然后读取第一个sheet 同步读取会自动finish
        EasyExcel.read(fileName, new ExcelListener(0, "uuid7", 0, fileInfoMapper, operationMapper)).doReadAll();
    }

    @Test
    public void testIsExitsTable() {
//        String[] columnName = {"date"};
//        fileInfoService.queryFile(0, 154, columnName, "4月16日");
//        List<String> jw = fileInfoMapper.getTableColumns("28a9652ecd0a4dc8bb7c019edb29ae1e");
//        System.out.println(jw);

//        List<Map<String, String>> userByPosition = fileInfoMapper.getByPosition(1, 2, "28a9652ecd0a4dc8bb7c019edb29ae1e");
//        System.out.println(userByPosition);

        String[] column = Arrays.asList("date", "wei").stream().toArray(String[]::new);

        fileInfoService.queryFile(1, 154, column, "4月10日");
    }

    public static void main(String[] args) {
//        List<Map<String, Object>> maps = SqlRunner.selectList("select * from file_info");

    }
}
