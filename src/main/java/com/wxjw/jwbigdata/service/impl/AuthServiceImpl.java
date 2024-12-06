package com.wxjw.jwbigdata.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wxjw.jwbigdata.domain.Department;
import com.wxjw.jwbigdata.domain.User;
import com.wxjw.jwbigdata.mapper.DepartmentMapper;
import com.wxjw.jwbigdata.mapper.UserMapper;
import com.wxjw.jwbigdata.service.AuthService;
import com.wxjw.jwbigdata.utils.RedisCache;
import com.wxjw.jwbigdata.vo.AuthVo.DeptVo;
import com.wxjw.jwbigdata.vo.AuthVo.UserInfoVo;
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
public class AuthServiceImpl extends ServiceImpl<UserMapper, User>
    implements AuthService {

    @Resource
    private DepartmentMapper departmentMapper;
    @Resource
    private UserMapper userMapper;
    @Resource
    private PasswordEncoder passwordEncoder;
    @Resource
    private RedisCache redisCache;

    @Override
    public UserInfoVo getMyInfo(Integer userId) {
        if (userId == null) {
            throw new IllegalArgumentException("参数不能为空！");
        }
        User loginUser = this.getById(userId);
        Department department = departmentMapper.selectById(loginUser.getDepartmentId());
        if (department == null) {
            throw new IllegalArgumentException("该用户暂时无部门");
        }
        log.info("用户{}获取个人信息成功", loginUser.getUsername());
        return new UserInfoVo(loginUser, department.getName());
    }

    @Override
    public List<DeptVo> getDeptList() {
        List<Department> departments = departmentMapper.selectList(null);
        List<DeptVo> depts = new ArrayList<>();
        departments.forEach(dept -> depts.add(new DeptVo(dept.getId(), dept.getName(),dept.getStatus()==0?true:false)));
        log.info("查询全部部门信息");
        return depts;
    }

    @Override
    public void updateMyInfo(Integer userId, String dept, String duties, String phone) {
        User loginUser = userMapper.selectById(userId);
        if (loginUser == null) {
            throw new IllegalArgumentException("用户不存在，请重试！");
        }
//        Department department = departmentMapper.selectOne(new QueryWrapper<Department>().eq("name", dept));
        userMapper.update(new UpdateWrapper<User>()
                .eq("id", userId)
                .set("department_id", dept)
                .set("position", duties)
                .set("phone", phone));
        log.info("用户{}更新个人信息", loginUser.getUsername());
    }

    @Override
    public void updateMyPwd(Integer userId, String oldPwd, String newPwd, HttpServletRequest request) {
        //判断用户是否存在
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new IllegalArgumentException("用户不存在！");
        }
        //判断输入的密码与原密码是否相同
        boolean isMatch = passwordEncoder.matches(oldPwd, user.getPassword());
        if (!isMatch) {
            throw new IllegalArgumentException("输入的密码与原密码不同，请重试！");
        }
        //修改密码
        //TODO 新密码有格式要求吗？
        userMapper.update(new UpdateWrapper<User>()
                .eq("id" ,userId)
                .set("password", passwordEncoder.encode(newPwd)));
        //删除token
        String token = request.getHeader("token");
        boolean result = redisCache.deleteObject("token:" + token);
        if (!result) {
            throw new RuntimeException("删除token失败");
        }
        log.info("用户{}修改自己的密码为：{}", user.getUsername(), newPwd);
    }
}




