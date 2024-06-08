package com.wxjw.jwbigdata.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.wxjw.jwbigdata.common.Code;
import com.wxjw.jwbigdata.service.UserService;
import com.wxjw.jwbigdata.utils.ResultUtils;
import com.wxjw.jwbigdata.vo.BaseResponse;
import com.wxjw.jwbigdata.vo.UserVo.UserVo;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.xml.transform.Result;
import java.util.List;

@RestController
public class UserController {

    @Resource
    private UserService userService;

    /**
     * 获取所有用户的列表
     * @return
     */
    @GetMapping("/getUserList")
    public BaseResponse<List<UserVo>> getUserList() {
        List<UserVo> userList = userService.getUserList();
        return ResultUtils.success(Code.SUCCESS, userList, "获取所有用户");
    }

    /**
     * 新增用户
     * @param userName 账号
     * @param realName 姓名
     * @param dept 部门id
     * @param duties 职位
     * @param phone 电话
     * @return
     */
    @PostMapping("/addUser")
    public BaseResponse<Object> addUser(String userName, String realName, Integer dept, String duties, String phone, HttpServletRequest request) throws JsonProcessingException {
        userService.addUser(userName, realName, dept, duties, phone, request);
        return ResultUtils.success(Code.SUCCESS, null, "新增用户成功！");
    }
    @PostMapping("/switchUserStatus")
    public BaseResponse<Object> switchUserStatus(String userId, Integer status) {
        userService.switchUserStatus(userId, status);
        return ResultUtils.success(Code.SUCCESS, null, "修改用户状态成功");
    }

    @PostMapping("delUser")
    public BaseResponse<Object> delUser(String[] userId){
        userService.delUser(userId);
        return ResultUtils.success(Code.SUCCESS, null, "删除用户成功！");
    }

    @PostMapping("/initPwd")
    public BaseResponse<Object> initPwd(String[] userId) {
        userService.initPwd(userId);
        return ResultUtils.success(Code.SUCCESS, null, "初始化用户密码成功！");
    }

    @PostMapping("/switchUserAuth")
    public BaseResponse<Object> switchUserAuth(String userId, Integer[] auth) {
        userService.switchUserAuth(userId, auth);
        return ResultUtils.success(Code.SUCCESS, null, "切换用户权限成功！");
    }
}
