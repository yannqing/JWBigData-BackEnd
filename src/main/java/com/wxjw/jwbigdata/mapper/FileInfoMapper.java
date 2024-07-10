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

    /**
     * 创建表
     * @param tableName 创建表的表名
     * @param columns 要创建的表的字段（默认都是varchar255）
     */
    void createTable(@Param("tableName")String tableName, @Param("columns") List<String> columns);


    /**
     * 查询某表下面某一列的具体数据
     * @param tableName 要查询的表名
     * @param columns 要查询的某一字段
     * @return
     */
    List<String> selectColumns(@Param("tableName")String tableName, @Param("columns") String columns);

    /**
     * 获取字段名
     * @param tableName 要获取字段的表名
     * @return List<String>
     */
    @Select("SELECT COLUMN_NAME FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_NAME = #{tableName} ")
    List<String> getTableColumns(String tableName);

    /**
     * 查询某表某索引开始的几条数据
     * @param limit 查询的数据条数
     * @param offset 查询的索引起始位置
     * @param tableName 要查询的表名
     * @return
     */
    @Select("SELECT * FROM ${tableName} LIMIT #{limit} OFFSET #{offset}")
    List<Map<String, String>> getByPosition(@Param("limit") int limit, @Param("offset") int offset, @Param("tableName") String tableName);

    /**
     * 查询某表的数据条目
     * @param tableName
     * @return
     */
    @Select("select count(*) from ${tableName}")
    Integer countData(String tableName);
}




