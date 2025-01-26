package com.wxjw.jwbigdata.service.impl;

import java.util.*;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wxjw.jwbigdata.common.OperType;
import com.wxjw.jwbigdata.domain.*;
import com.wxjw.jwbigdata.mapper.*;
import com.wxjw.jwbigdata.service.UserService;
import com.wxjw.jwbigdata.utils.JwtUtils;
import com.wxjw.jwbigdata.vo.UserVo.UserVo;
import io.micrometer.common.util.StringUtils;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

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
    private OperlogMapper operlogMapper;
    @Resource
    private PasswordEncoder passwordEncoder;
    @Resource
    private UserTableMapper userTableMapper;
    @Resource
    private DepartmentTableMapper departmentTableMapper;

    @Override
    public List<UserVo> getUserList() {
        List<User> users = userMapper.selectList(new QueryWrapper<User>());
        List<UserVo> userVos = new ArrayList<>();
        users.forEach(user -> {
            Department department = departmentMapper.selectById(user.getDepartmentId());
            List<UserTable> userTables = userTableMapper.selectList(new QueryWrapper<UserTable>().eq("user_id", user.getId()));
            List<Integer> tabList = new ArrayList<>();
            for (UserTable userTable : userTables) {
                tabList.add(userTable.getNewTableId());
            }

            UserVo vo;
            if (department != null) {
                vo = new UserVo(user, department.getName(),tabList);
            } else {
                vo = new UserVo(user, "",tabList);
            }
            userVos.add(vo);
        });
        log.info("查询所有用户成功！");
        return userVos;
    }

    @Override
    public void addUser(String userName, String realName, String dept, String duties, String phone,List<Integer> selectedTables, HttpServletRequest request) throws JsonProcessingException {
        //验证userName与realName是否为有效
        if (StringUtils.isBlank(userName) || StringUtils.isBlank(realName)) {
            throw new IllegalArgumentException("账号和姓名不能为空");
        }
        //从token得到创建者的userId
        String token = request.getHeader("token");
        String userInfo = JwtUtils.getUserInfoFromToken(token);
        User loginUser = objectMapper.readValue(userInfo, User.class);

        List<Department> department = departmentMapper.selectList(new QueryWrapper<Department>().eq("name", dept));

        User user = new User();
        user.setUserAccount(userName);
        user.setUsername(realName);
        user.setPassword(passwordEncoder.encode("123456"));
        user.setDepartmentId(department.size()==0 ? 0 : department.get(0).getId());
        user.setPosition(duties);
        user.setCreatedUser(loginUser.getId());
        user.setCreatedTime(new Date());
        user.setPhone(phone);
        user.setStatus(1);
        userMapper.insert(user);

        Integer newId = user.getId();
        for (Integer selectedTable : selectedTables) {
            UserTable userTable = new UserTable();
            userTable.setUserId(newId);
            userTable.setNewTableId(selectedTable);
            userTableMapper.insert(userTable);
        }

        Operlog operlog = new Operlog();
        operlog.setUserId(loginUser.getId());
        operlog.setOperType(OperType.addUser);
        operlog.setOperData(userName);
        operlog.setOperTime(new Date());
        operlogMapper.insert(operlog);

        log.info("用户{}新增了一个用户{}", loginUser.getUsername(), user.getUsername());
    }

    @Override
    public void updateUserInfo(int userId, String dept, String duties, String phone, List<Integer> selectedTables, HttpServletRequest request) throws JsonProcessingException {
        //从token得到创建者的userId
        String token = request.getHeader("token");
        String userInfo = JwtUtils.getUserInfoFromToken(token);
        User loginUser = objectMapper.readValue(userInfo, User.class);

        List<Department> department = departmentMapper.selectList(new QueryWrapper<Department>().eq("name", dept));
        User user = new User();
        user.setId(userId);
        user.setDepartmentId(department.size() == 0 ? 0 : department.get(0).getId());
        user.setPosition(duties);
        user.setPhone(phone);
        userMapper.updateById(user);

        userTableMapper.delete(new QueryWrapper<UserTable>().eq("user_id",userId));

        if(selectedTables != null){
            for (Integer selectedTable : selectedTables) {
                UserTable userTable = new UserTable();
                userTable.setUserId(userId);
                userTable.setNewTableId(selectedTable);
                userTableMapper.insert(userTable);
            }
        }

        Operlog operlog = new Operlog();
        operlog.setUserId(loginUser.getId());
        operlog.setOperType(OperType.updateUserInfo);
        operlog.setOperData(loginUser.getUserAccount());
        operlog.setOperTime(new Date());
        operlogMapper.insert(operlog);

        log.info("用户{}编辑了一个用户{}", loginUser.getUsername(), user.getUsername());
    }

    @Override
    public void switchUserStatus(int userId, int status, HttpServletRequest request) throws JsonProcessingException {
        //从token得到创建者的userId
        String token = request.getHeader("token");
        String userInfo = JwtUtils.getUserInfoFromToken(token);
        User loginUser = objectMapper.readValue(userInfo, User.class);

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

        Operlog operlog = new Operlog();
        operlog.setUserId(loginUser.getId());
        operlog.setOperType(status == 0?OperType.switchUserOn:OperType.switchUserOff);
        operlog.setOperData(changedUser.getUserAccount());
        operlog.setOperTime(new Date());
        operlogMapper.insert(operlog);
        log.info("修改{}用户的状态成功", changedUser.getUsername());
    }

    @Override
    public void switchDeptStatus(int deptId, int status, HttpServletRequest request) throws JsonProcessingException  {
        //从token得到创建者的userId
        String token = request.getHeader("token");
        String userInfo = JwtUtils.getUserInfoFromToken(token);
        User loginUser = objectMapper.readValue(userInfo, User.class);

        //校验参数是否有效
        Department department = departmentMapper.selectById(deptId);
        if (department == null) {
            throw new IllegalArgumentException("部门不存在，请重试！");
        }
        if (status != 1 && status != 0) {
            throw new IllegalArgumentException("状态错误！无法修改");
        }
        //修改状态
        departmentMapper.update(new UpdateWrapper<Department>()
                .eq("id", deptId)
                .set("status", status)
        );

        Operlog operlog = new Operlog();
        operlog.setUserId(loginUser.getId());
        operlog.setOperType(status == 0?OperType.switchDeptOn:OperType.switchDeptOff);
        operlog.setOperData(department.getName());
        operlog.setOperTime(new Date());
        operlogMapper.insert(operlog);
        log.info("修改{}部门的状态成功", department.getName());
    }

    @Override
    public void delUser(int[] userIds, HttpServletRequest request) throws JsonProcessingException {
        //从token得到创建者的userId
        String token = request.getHeader("token");
        String userInfo = JwtUtils.getUserInfoFromToken(token);
        User loginUser = objectMapper.readValue(userInfo, User.class);

        Arrays.stream(userIds).forEach(userId -> {
            if (userMapper.selectById(userId) == null) {
                throw new IllegalArgumentException("用户不存在，请重试！");
            }
        });
        Arrays.stream(userIds).forEach(
                userId -> {
                    User user = userMapper.selectById(userId);
                    if(user == null)
                        return;
                    Operlog operlog = new Operlog();
                    operlog.setUserId(loginUser.getId());
                    operlog.setOperType(OperType.deleteUser);
                    operlog.setOperData(user.getUserAccount());
                    operlog.setOperTime(new Date());
                    operlogMapper.insert(operlog);
                    userMapper.deleteById(userId);
                });

        log.info("删除用户成功，删除的用户id：{}", (Object) userIds);
    }

    @Override
    public void delDept(int[] deptIds, HttpServletRequest request) throws JsonProcessingException {
        //从token得到创建者的userId
        String token = request.getHeader("token");
        String userInfo = JwtUtils.getUserInfoFromToken(token);
        User loginUser = objectMapper.readValue(userInfo, User.class);

//        Arrays.stream(deptIds).forEach(deptId -> {
//            if (departmentMapper.selectById(deptId) == null) {
//                throw new IllegalArgumentException("部门不存在，请重试！");
//            }
//        });
        Arrays.stream(deptIds).forEach(deptId -> {
            Department department = departmentMapper.selectById(deptId);
            if(department == null)
                return;
            Operlog operlog = new Operlog();
            operlog.setUserId(loginUser.getId());
            operlog.setOperType(OperType.deleteDept);
            operlog.setOperData(department.getName());
            operlog.setOperTime(new Date());
            operlogMapper.insert(operlog);
            departmentMapper.deleteById(deptId);
        });

        log.info("删除部门成功，删除的部门id：{}", (Object) deptIds);
    }

    @Override
    public void updataDeptInfo(int deptId, String deptName,List<Integer> selectedTables, HttpServletRequest request) throws JsonProcessingException {
        //从token得到创建者的userId
        String token = request.getHeader("token");
        String userInfo = JwtUtils.getUserInfoFromToken(token);
        User loginUser = objectMapper.readValue(userInfo, User.class);

        departmentMapper.update(new UpdateWrapper<Department>().eq("id", deptId).
                set("name", deptName));
        departmentTableMapper.delete(new QueryWrapper<DepartmentTable>().eq("department_id",deptId));
        for (Integer selectedTable : selectedTables) {
            DepartmentTable departmentTable = new DepartmentTable();
            departmentTable.setDepartmentId(deptId);
            departmentTable.setNewtableId(selectedTable);
            departmentTableMapper.insert(departmentTable);
        }
        Operlog operlog = new Operlog();
        operlog.setUserId(loginUser.getId());
        operlog.setOperType(OperType.updateDept);
        operlog.setOperData(deptName);
        operlog.setOperTime(new Date());
        operlogMapper.insert(operlog);
        log.info("部门{}修改成功！", deptName);
    }

    @Override
    public void initPwd(int[] userIds, HttpServletRequest request) throws JsonProcessingException {
        //从token得到创建者的userId
        String token = request.getHeader("token");
        String userInfo = JwtUtils.getUserInfoFromToken(token);
        User loginUser = objectMapper.readValue(userInfo, User.class);

        Arrays.stream(userIds).forEach(userId -> {
            if (userMapper.selectById(userId) == null) {
                throw new IllegalArgumentException("用户不存在，请重试！");
            }
        });

        Arrays.stream(userIds).forEach(userId -> userMapper.update(new UpdateWrapper<User>()
                .eq("id", userId).
                set("password", passwordEncoder.encode("123456")))
        );

        Operlog operlog = new Operlog();
        operlog.setUserId(loginUser.getId());
        operlog.setOperType(OperType.initPwd);
        operlog.setOperData(userIds.toString());
        operlog.setOperTime(new Date());
        operlogMapper.insert(operlog);
        log.info("初始化密码成功，初始化用户id：{}", Arrays.toString(userIds));
    }

    @Override
    public void switchUserAuth(Integer userId, Boolean[] auths, HttpServletRequest request) throws JsonProcessingException {
        //从token得到创建者的userId
        String token = request.getHeader("token");
        String userInfo = JwtUtils.getUserInfoFromToken(token);
        User loginUser = objectMapper.readValue(userInfo, User.class);
        //参数校验
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new IllegalArgumentException("用户不存在，请重试！");
        }
        if (auths.length != 5) {
            throw new IllegalArgumentException("数组长度错误！");
        }

        //修改权限
        userMapper.update(new UpdateWrapper<User>()
                .eq("id", userId)
                .set("role", auths[0] == false ? 0 : 1)
                .set("portrait", auths[1] == false ? 0 : 1)
                .set("search", auths[2] == false ? 0 : 1)
                .set("compare", auths[3] == false ? 0 : 1)
                .set("model", auths[4] == false ? 0 : 1)
        );
        Operlog operlog = new Operlog();
        operlog.setUserId(loginUser.getId());
        operlog.setOperType(OperType.switchUserAuth);
        operlog.setOperData(user.getUserAccount()+"-"+(auths[0]== false ? "管理员(无)":"管理员(有)")+"-"+(auths[1]== false ? "画像(无)":"画像(有)")+"-"+(auths[2]== false ? "比对(无)":"比对(有)")+"-"+(auths[3]== false ? "模型检索(无)":"模型检索(有)"));
        operlog.setOperTime(new Date());
        operlogMapper.insert(operlog);
        log.info("修改用户{}权限成功", user.getUsername());
    }

    @Override
    public void addDept(String deptName,List<Integer> selectedTables, HttpServletRequest request) throws JsonProcessingException {
        //从token得到创建者的userId
        String token = request.getHeader("token");
        String userInfo = JwtUtils.getUserInfoFromToken(token);
        User loginUser = objectMapper.readValue(userInfo, User.class);

        Department department = new Department();
        department.setName(deptName);
        department.setCreatedUser(loginUser.getId().toString());
        department.setIsDelete(0);
        department.setStatus(0);
        departmentMapper.insert(department);

        Integer newId = department.getId();
        for (Integer selectedTable : selectedTables) {
            DepartmentTable departmentTable = new DepartmentTable();
            departmentTable.setDepartmentId(newId);
            departmentTable.setNewtableId(selectedTable);
            departmentTableMapper.insert(departmentTable);
        }
        Operlog operlog = new Operlog();
        operlog.setUserId(loginUser.getId());
        operlog.setOperType(OperType.addDept);
        operlog.setOperData(deptName);
        operlog.setOperTime(new Date());
        operlogMapper.insert(operlog);
        log.info("用户{}新增了一个部门{}", loginUser.getUsername(), deptName);
    }
}




