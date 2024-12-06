package com.wxjw.jwbigdata.mapper;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.wxjw.jwbigdata.domain.NewTable;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;

/**
* @author Paul
* @description 针对表【newtable】的数据库操作Mapper
* @createDate 2024-09-02 22:32:58
* @Entity com.wxjw.jwbigdata.domain.Newtable
*/
@DS("sparadb")
public interface NewTableMapper extends BaseMapper<NewTable> {

//    List<Map<String,Object>> tableSelect(@Param("tableName") String tableName, @Param("fieldName") String fieldName, @Param("value") String value);

//    @DS("newdb")
////    @Select("select * from pbasicinfo where name = #{value}")
//    List<Map<String,Object>> tableNewSelect(@Param("tableName") String tableName, @Param("fieldName") String fieldName, @Param("value") String value);
//
//    @DS("newdb")
////    @Select("select * from pbasicinfo where name = #{value}")
//    List<Map<String,Object>> tableNewSelect1();
//
//    @DS("newdb")
////    @Select("select * from ${tableName} where ${fieldName1} = #{value} or ${fieldName2} = ${value}")
//    List<Map<String,Object>> tableNewMultiSelect(@Param("tableName") String tableName, @Param("fieldName1") String fieldName1,@Param("fieldName2") String fieldName2, @Param("value") String value);
}




