package com.wxjw.jwbigdata.mapper;

import com.wxjw.jwbigdata.domain.JwRule;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
* @author Paul
* @description 针对表【jw_rule】的数据库操作Mapper
* @createDate 2024-06-14 14:06:48
* @Entity com.wxjw.jwbigdata.domain.JwRule
*/
public interface JwRuleMapper extends BaseMapper<JwRule> {
    int createNewTable(@Param("tableName") String tableName, @Param("sqlStatement") String sqlStatement);


}




