package com.wxjw.jwbigdata.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.wxjw.jwbigdata.domain.User;
import org.apache.ibatis.annotations.Options;

/**
* @author 67121
* @description 针对表【yan_user】的数据库操作Mapper
* @createDate 2024-03-07 17:19:45
* @Entity generator.domain.User
*/
public interface UserMapper extends BaseMapper<User> {
    @Options(useGeneratedKeys = true, keyProperty = "id", keyColumn = "id")
    int insert(User user);
}




