package com.wxjw.jwbigdata.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.wxjw.jwbigdata.domain.FileInfo;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;

import java.util.List;


/**
* @author 67121
* @description 针对表【file_info】的数据库操作Mapper
* @createDate 2024-06-08 10:36:47
* @Entity generator.domain.FileInfo
*/
public interface FileInfoMapper extends BaseMapper<FileInfo> {

    void createTable(@Param("tableName")String tableName, @Param("columns") List<String> columns);

    int existTable(String tableName);
}




