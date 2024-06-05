package com.wxjw.jwbigdata.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wxjw.jwbigdata.service.UserService;
import generator.domain.User;
import generator.mapper.UserMapper;
import org.springframework.stereotype.Service;

/**
* @author 67121
* @description 针对表【user】的数据库操作Service实现
* @createDate 2024-06-03 18:53:58
*/
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User>
    implements UserService {

}




