package com.wxjw.jwbigdata.mapper;

import com.wxjw.jwbigdata.domain.JwTable;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
* @author Paul
* @description 针对表【jw_table】的数据库操作Mapper
* @createDate 2024-06-14 14:08:08
* @Entity com.wxjw.jwbigdata.domain.JwTable
*/
public interface JwTableMapper extends BaseMapper<JwTable> {
    List<Map<String,Object>> tableSelect(@Param("tableName") String tableName, @Param("fieldName") String fieldName, @Param("value") String value);
}




