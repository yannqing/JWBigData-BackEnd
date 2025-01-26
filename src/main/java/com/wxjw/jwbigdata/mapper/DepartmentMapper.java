package com.wxjw.jwbigdata.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.wxjw.jwbigdata.domain.Department;
import org.apache.ibatis.annotations.Options;


/**
* @author 67121
* @description 针对表【department】的数据库操作Mapper
* @createDate 2024-06-05 11:50:06
* @Entity generator.domain.Department
*/
public interface DepartmentMapper extends BaseMapper<Department> {
    @Options(useGeneratedKeys = true, keyProperty = "id", keyColumn = "id")
    int insert(Department department);
}




