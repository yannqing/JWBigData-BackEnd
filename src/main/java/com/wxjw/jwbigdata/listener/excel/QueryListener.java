package com.wxjw.jwbigdata.listener.excel;

import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;
import com.alibaba.excel.metadata.data.ReadCellData;
import com.alibaba.excel.util.ConverterUtils;
import com.alibaba.excel.util.ListUtils;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.wxjw.jwbigdata.domain.FileInfo;
import com.wxjw.jwbigdata.mapper.FileInfoMapper;
import com.wxjw.jwbigdata.mapper.OperationMapper;
import com.wxjw.jwbigdata.utils.DateFormat;
import lombok.extern.slf4j.Slf4j;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
public class QueryListener extends AnalysisEventListener<Map<Integer, String>> {

    private final Integer role ;
    private final String fileName;
    private final Integer createdUser;
    private final FileInfoMapper fileInfoMapper;
    private final OperationMapper operationMapper;
    private Integer headSize;
    private Map<Integer, String> headData;
    private String tableName;

    public QueryListener(Integer role, String fileName, Integer createdUser, FileInfoMapper fileInfoMapper, OperationMapper operationMapper) {
        this.role = role;
        this.fileName = fileName;
        this.createdUser = createdUser;
        this.fileInfoMapper = fileInfoMapper;
        this.operationMapper = operationMapper;
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
        Map<Integer, String> heads = ConverterUtils.convertToStringMap(headMap, context);
        headData = heads;

        List<String> columns = heads.entrySet().stream().map(Map.Entry::getValue).collect(Collectors.toList());

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

