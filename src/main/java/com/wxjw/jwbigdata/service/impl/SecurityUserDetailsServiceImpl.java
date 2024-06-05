package com.yannqing.dockerdesktop.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.yannqing.dockerdesktop.domain.User;
import com.yannqing.dockerdesktop.mapper.PermissionsMapper;
import com.yannqing.dockerdesktop.mapper.RoleMapper;
import com.yannqing.dockerdesktop.mapper.UserMapper;
import com.yannqing.dockerdesktop.vo.SecurityUser;
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
    @Resource
    private PermissionsMapper permissionsMapper;

    @Resource
    private RoleMapper roleMapper;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        //获取到用户的全部信息
//        User user = userDao.getUserByUsername(username);
        User user = userMapper.selectOne(new QueryWrapper<User>().eq("username", username));
        if (user == null) {
            throw new UsernameNotFoundException("用户不存在");
        }
        //获取到用户的所有权限信息
        List<String> permissions = permissionsMapper.queryPermissionsByUserId(user.getUser_id());
        List<SimpleGrantedAuthority> authorities = permissions.stream().map(SimpleGrantedAuthority::new).toList();
        //获取用户role角色
        int rid = roleMapper.selectRoleByUserId(user.getUser_id());
        SecurityUser securityUser = new SecurityUser(user,rid);
        securityUser.setSimpleGrantedAuthorities(authorities);
        return securityUser;
    }
}
