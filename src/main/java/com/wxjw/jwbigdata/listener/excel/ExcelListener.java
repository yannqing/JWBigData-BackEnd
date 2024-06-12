package com.wxjw.jwbigdata.listener.excel;

import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;
import com.alibaba.excel.metadata.data.ReadCellData;
import com.alibaba.excel.util.ConverterUtils;
import com.alibaba.excel.util.ListUtils;
import com.alibaba.fastjson.JSON;
import com.wxjw.jwbigdata.mapper.FileInfoMapper;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
public class ExcelListener extends AnalysisEventListener<Map<Integer, String>> {

    private final Integer role ;

    private final String fileName;

    private final FileInfoMapper fileInfoMapper;

    public ExcelListener(Integer role, String fileName, FileInfoMapper fileInfoMapper) {
        this.role = role;
        this.fileName = fileName;
        this.fileInfoMapper = fileInfoMapper;
    }

    /**
     * 每隔5条存储数据库，实际使用中可以100条，然后清理list ，方便内存回收
     */
    private static final int BATCH_COUNT = 5;
    private List<Map<Integer, String>> cachedDataList = ListUtils.newArrayListWithExpectedSize(BATCH_COUNT);

    /**
     * 读取每一条数据
     * @param data
     * @param context
     */
    @Override
    public void invoke(Map<Integer, String> data, AnalysisContext context) {
        log.info("解析到一条数据:{}", JSON.toJSONString(data));
        cachedDataList.add(data);
        if (cachedDataList.size() >= BATCH_COUNT) {
            saveData();
            cachedDataList = ListUtils.newArrayListWithExpectedSize(BATCH_COUNT);
        }
    }
    /**
     * 这里会一行行的返回头
     *
     * @param headMap
     * @param context
     */
    @Override
    public void invokeHead(Map<Integer, ReadCellData<?>> headMap, AnalysisContext context) {
        log.info("解析到一条头数据:{}", ConverterUtils.convertToStringMap(headMap, context));
        // 如果想转成成 Map<Integer,String>
        // 方案1： 不要implements ReadListener 而是 extends AnalysisEventListener
        // 方案2： 调用 ConverterUtils.convertToStringMap(headMap, context) 自动会转换
        Map<Integer, String> heads = ConverterUtils.convertToStringMap(headMap, context);

        List<String> columns = heads.entrySet().stream().map(Map.Entry::getValue).collect(Collectors.toList());

        System.out.println(columns);
        //1. 查询表格是否存在，存在则返回给用户，判断是否继续。如果继续，则删除原表格创建新表格。不继续则暂停。

        //创建表格
        fileInfoMapper.createTable(fileName, columns);
        log.info("创建表格test成功");

    }

    /**
     * 解析完所有的数据（以BATCH_COUNT为一个周期）后调用
     * @param context
     */
    @Override
    public void doAfterAllAnalysed(AnalysisContext context) {
        saveData();
        log.info("所有数据解析完成！");
    }

    /**
     * 存储数据库
     */
    private void saveData() {
        log.info("{}条数据，开始存储数据库！", cachedDataList.size());
        log.info("存储数据库成功！");
    }
}

