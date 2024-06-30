package com.wxjw.jwbigdata.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.wxjw.jwbigdata.domain.FileInfo;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;
import java.util.Map;


/**
* @author 67121
* @description 针对表【file_info】的数据库操作Mapper
* @createDate 2024-06-08 10:36:47
* @Entity generator.domain.FileInfo
*/
public interface FileInfoMapper extends BaseMapper<FileInfo> {

    void createTable(@Param("tableName")String tableName, @Param("columns") List<String> columns);

    int existTable(String tableName);

    List<String> selectColumns(@Param("tableName")String tableName, @Param("columns") String columns);

    /**
     * 获取字段名
     * @param tableName 要获取字段的表名
     * @return List<String>
     */
    @Select("SELECT COLUMN_NAME FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_NAME = #{tableName} ")
    List<String> getTableColumns(String tableName);

    @Select("SELECT * FROM ${tableName} LIMIT #{limit} OFFSET #{offset}")
    List<Map<String, String>> getByPosition(@Param("limit") int limit, @Param("offset") int offset, @Param("tableName") String tableName);
}




