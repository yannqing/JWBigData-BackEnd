package com.wxjw.jwbigdata.mapper;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface OperationMapper {
    @Select("select * from #{tableName}")
    List<List<Object>> getData(String tableName);

    @Delete("drop table #{tableName}")
    void dropTable(String tableName);
}
