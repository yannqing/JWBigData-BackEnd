package com.wxjw.jwbigdata.mapper;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.wxjw.jwbigdata.domain.NewTable;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
* @author Paul
* @description 针对表【newtable】的数据库操作Mapper
* @createDate 2024-09-02 22:32:58
* @Entity com.wxjw.jwbigdata.domain.Newtable
*/

public interface NewTableMapper extends BaseMapper<NewTable> {
    @DS("sparadb")
    List<Map<String,Object>> tableSelect(@Param("tableName") String tableName, @Param("fieldName") String fieldName, @Param("value") String value);

    @DS("cleandb")
    List<Map<String,Object>> tableCleanSelect(@Param("tableName") String tableName, @Param("fieldName") String fieldName, @Param("value") String value);

    @DS("cleandb")
    List<Map<String,Object>> tableCleanMultiSelect(@Param("tableName") String tableName, @Param("fieldName1") String fieldName1,@Param("fieldName2") String fieldName2, @Param("value") String value);
}




