package com.wxjw.jwbigdata.mapper;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.wxjw.jwbigdata.domain.Modeltask;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
* @author Paul
* @description 针对表【modeltask】的数据库操作Mapper
* @createDate 2024-09-04 21:47:20
* @Entity com.wxjw.jwbigdata.domain.Modeltask
*/
@DS("modelresultdb")
public interface ModeltaskMapper extends BaseMapper<Modeltask> {
    int createNewTable(@Param("tableName") String tableName, @Param("sqlStatement") String sqlStatement);

    Map<String,Object> tableExist(@Param("tableName") String tableName);

    List<Map<String,Object>> resultTable(@Param("tableName") String tableName);

    Map<String,Object> resultDetail(@Param("tableName") String tableName,@Param("fieldName") String fieldName,@Param("id") String id);
}




