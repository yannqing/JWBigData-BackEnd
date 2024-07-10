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
import java.util.*;

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
        Integer[] fileId = new Integer[]{153, 151, 149};
        String[][] fieldArray = new String[][]{
                {"date", "date", "wei"},
                {"yang", "she", "wei"},
                {"wang", "zeng", "zhang"}
        };
        boolean[][] saveFieldArray = new boolean[3][];
        boolean[] saveField1 = new boolean[]{true, false, true, false, true, false, true};
        boolean[] saveField2 = new boolean[]{true, false, true, false, true, false, true};
        boolean[] saveField3 = new boolean[]{true, false, true, false, true, false, true};
        saveFieldArray[0] = saveField1;
        saveFieldArray[1] = saveField2;
        saveFieldArray[2] = saveField3;
        fileInfoService.compareFiles(1, fileId, fieldArray, saveFieldArray, "正向");
    }

    public static void main(String[] args) {
//        List<Map<String, Object>> maps = SqlRunner.selectList("select * from file_info");
        String[][] result = new String[2 + 1][];
        List<String> tableColumns = Arrays.asList("table", "qwe", "column");
        result[0] = tableColumns.toArray(new String[]{});
        System.out.println(Arrays.deepToString(result));
    }
}
