package com.wxjw.jwbigdata.mapper;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

@DS("uploaddb")
@Mapper
public interface UploadMapper extends BaseMapper<Map<String, String>> {

    void dynamicInsert(String tableName, List<String> columns, List<String> values);
}
