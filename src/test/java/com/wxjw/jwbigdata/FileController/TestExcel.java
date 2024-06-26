package com.wxjw.jwbigdata.FileController;

import com.alibaba.excel.EasyExcel;
import com.baomidou.mybatisplus.extension.toolkit.SqlRunner;
import com.wxjw.jwbigdata.config.MyBatisPlusConfig;
import com.wxjw.jwbigdata.listener.excel.ExcelListener;
import com.wxjw.jwbigdata.mapper.FileInfoMapper;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Repeat;

import java.io.File;
import java.util.List;
import java.util.Map;

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
        EasyExcel.read(fileName, new ExcelListener(0, "love5", 0, fileInfoMapper)).doReadAll();
    }

    @Test
    public void testIsExitsTable() {
//        int love = fileInfoMapper.existTable("love5");
//        SqlRunner runner = new SqlRunner();
        List<Map<String, Object>> maps = SqlRunner.db().selectList("select * from file_info");
        System.out.println(maps);
    }

    public static void main(String[] args) {
//        List<Map<String, Object>> maps = SqlRunner.selectList("select * from file_info");

    }
}
