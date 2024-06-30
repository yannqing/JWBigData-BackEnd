package com.wxjw.jwbigdata.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;

@Mapper
public interface OperationMapper extends BaseMapper<Map<String, String>> {
    @Select("select * from #{tableName}")
    List<List<Object>> getData(String tableName);

    @Delete("drop table #{tableName}")
    void dropTable(String tableName);

    void dynamicInsert(String tableName, List<String> columns, List<String> values);
}
