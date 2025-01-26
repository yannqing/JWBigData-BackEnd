package com.wxjw.jwbigdata.mapper;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.wxjw.jwbigdata.domain.Uploadtable;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.io.Serializable;
import java.util.List;

/**
* @author Paul
* @description 针对表【uploadtable(上传表参数)】的数据库操作Mapper
* @createDate 2025-01-03 14:52:04
* @Entity com.wxjw.jwbigdata.domain.Uploadtable
*/
@DS("sparadb")
public interface UploadtableMapper extends BaseMapper<Uploadtable> {

}




