package com.wxjw.jwbigdata.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wxjw.jwbigdata.domain.User;
import com.wxjw.jwbigdata.mapper.UserMapper;
import com.wxjw.jwbigdata.service.UserService;
import com.wxjw.jwbigdata.vo.UserVo.UserVo;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

/**
* @author yannqing
* @description 针对登录和个人信息管理的 Service 实现
* @createDate 2024-06-03 18:53:58
*/
@Slf4j
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User>
    implements UserService {

    @Resource
    private UserMapper userMapper;

    @Override
    public List<UserVo> getUserList() {
        List<User> users = userMapper.selectList(new QueryWrapper<User>()
                .eq("role", 0));
        return List.of();
    }
}




