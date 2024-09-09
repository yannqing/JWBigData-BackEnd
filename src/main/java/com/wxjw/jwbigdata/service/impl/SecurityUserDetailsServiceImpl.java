package com.wxjw.jwbigdata.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.wxjw.jwbigdata.domain.User;
import com.wxjw.jwbigdata.mapper.UserMapper;
import com.wxjw.jwbigdata.vo.SecurityUser;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class SecurityUserDetailsServiceImpl implements UserDetailsService {

    @Resource
    private UserMapper userMapper;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        //获取到用户的全部信息
//        User user = userDao.getUserByUsername(username);
        log.info("loading user by username: {}", username);
        User user = userMapper.selectOne(new QueryWrapper<User>().eq("user_account", username));
        log.debug("login user: {}", user);
        if (user == null) {
            throw new UsernameNotFoundException("用户不存在");
        }

        return new SecurityUser(user);
    }
}
