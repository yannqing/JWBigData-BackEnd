package com.wxjw.jwbigdata.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.wxjw.jwbigdata.domain.FileInfo;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;


/**
* @author 彦青
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

    List<LinkedHashMap<String,String>> selectColumnsByParams(@Param("tableName")String tableName, @Param("columns") List<String> columns,@Param("keyWord") String keyword);

    /**
     * 获取字段名
     * @param tableName 要获取字段的表名
     * @return List<String>
     */
    @Select("SELECT COLUMN_NAME FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA='jw' AND TABLE_NAME = #{tableName} ORDER BY ORDINAL_POSITION")
    List<String> getTableColumns(String tableName);

    @Select("SELECT COLUMN_NAME FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA='jw' AND TABLE_NAME = #{tableName}  ORDER BY ORDINAL_POSITION")
    List<Object> getTableColumnsAsObject(String tableName);

    @Select("SELECT CONCAT(#{seq},'-',COLUMN_NAME) FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA='jw' AND TABLE_NAME = #{tableName}  ORDER BY ORDINAL_POSITION")
    List<Object> getTableColumnsBySeq(String tableName,String seq);

    @Select("SELECT COLUMN_NAME \"id\",COLUMN_NAME \"label\" FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA='jw' AND TABLE_NAME = #{tableName}  ORDER BY ORDINAL_POSITION")
    List<Map<String,String>> getTableAndColumns(String tableName);

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

    /**
     * 创建表
     * @param sql 创建表的语句
     */
    void createTableBySQL(@Param("sql")String sql);

    @Insert("INSERT INTO ${tableName} values ${datas}")
    void insertData(@Param("tableName") String tableName,@Param("datas") String datas);

    /**
     * 查询某表数据
     * @param tableName 要查询的表名
     * @return
     */
    @Select("SELECT * FROM `${tableName}`")
    List<LinkedHashMap<String,Object>> getData(@Param("tableName") String tableName);
}




