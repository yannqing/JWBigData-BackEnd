package com.wxjw.jwbigdata.service.impl;
import java.util.Arrays;
import java.util.Date;

import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wxjw.jwbigdata.domain.User;
import com.wxjw.jwbigdata.mapper.DepartmentMapper;
import com.wxjw.jwbigdata.mapper.UserMapper;
import com.wxjw.jwbigdata.service.UserService;
import com.wxjw.jwbigdata.utils.JwtUtils;
import com.wxjw.jwbigdata.vo.UserVo.UserVo;
import io.micrometer.common.util.StringUtils;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
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
    @Resource
    private DepartmentMapper departmentMapper;
    @Resource
    private ObjectMapper objectMapper;
    @Resource
    private PasswordEncoder passwordEncoder;

    @Override
    public List<UserVo> getUserList() {
        List<User> users = userMapper.selectList(new QueryWrapper<User>()
                .eq("role", 0));
        List<UserVo> userVos = new ArrayList<>();
        users.forEach(user -> {
            UserVo vo = new UserVo(user, departmentMapper.selectById(user.getDepartmentId()).getName());
            userVos.add(vo);
        });
        log.info("查询所有用户成功！");
        return userVos;
    }

    @Override
    public void addUser(String userName, String realName, Integer dept, String duties, String phone, HttpServletRequest request) throws JsonProcessingException {
        //验证userName与realName是否为有效
        if (StringUtils.isBlank(userName) || StringUtils.isBlank(realName)) {
            throw new IllegalArgumentException("账号和姓名不能为空");
        }
        //从token得到创建者的userId
        String token = request.getHeader("token");
        String userInfo = JwtUtils.getUserInfoFromToken(token);
        User loginUser = objectMapper.readValue(userInfo, User.class);

        User user = new User();
        user.setUserAccount(userName);
        user.setUsername(realName);
        user.setPassword(passwordEncoder.encode("123456"));
        user.setDepartmentId(dept);
        user.setPosition(duties);
        user.setCreatedUser(loginUser.getId());
        user.setPhone(phone);
        user.setStatus(1);
        userMapper.insert(user);
        log.info("用户{}新增了一个用户{}", loginUser.getUsername(), user.getUsername());
    }

    @Override
    public void switchUserStatus(String userId, Integer status) {
        //校验参数是否有效
        User changedUser = userMapper.selectById(userId);
        if (changedUser == null) {
            throw new IllegalArgumentException("用户不存在，请重试！");
        }
        if (status != 1 && status != 0) {
            throw new IllegalArgumentException("状态错误！无法修改");
        }
        //修改状态
        userMapper.update(new UpdateWrapper<User>()
                .eq("id", userId)
                .set("status", status)
        );
        log.info("修改{}用户的状态成功", changedUser.getUsername());
    }

    @Override
    public void delUser(String[] userIds) {
        Arrays.stream(userIds).forEach(userId ->{
            if (userMapper.selectById(userId) == null) {
                throw new IllegalArgumentException("用户不存在，请重试！");
            }
        });
        Arrays.stream(userIds).forEach(userId -> userMapper.deleteById(userId));
        log.info("删除用户成功，删除的用户id：{}", (Object) userIds);
    }

    @Override
    public void initPwd(String[] userIds) {
        Arrays.stream(userIds).forEach(userId ->{
            if (userMapper.selectById(userId) == null) {
                throw new IllegalArgumentException("用户不存在，请重试！");
            }
        });

        Arrays.stream(userIds).forEach(userId -> userMapper.update(new UpdateWrapper<User>()
                    .eq("id", userId).
                    set("password", passwordEncoder.encode("123456")))
        );
        log.info("初始化密码成功，初始化用户id：{}", Arrays.toString(userIds));
    }

    @Override
    public void switchUserAuth(String userId, Integer[] auths) {
        //参数校验
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new IllegalArgumentException("用户不存在，请重试！");
        }
        if (auths.length != 4) {
            throw new IllegalArgumentException("数组长度错误！");
        }
        Arrays.stream(auths).forEach(auth -> {
            if (auth != 0 && auth != 1) {
                throw new IllegalArgumentException("权限参数错误！");
            }
        });
        //修改权限
        userMapper.update(new UpdateWrapper<User>()
                .eq("id", userId)
                .set("role", auths[0])
                .set("portrait", auths[1])
                .set("compare", auths[2])
                .set("model", auths[3])
        );
        log.info("修改用户{}权限成功", user.getUsername());
    }
}




