package com.wxjw.jwbigdata.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.wxjw.jwbigdata.common.Code;
import com.wxjw.jwbigdata.domain.User;
import com.wxjw.jwbigdata.service.UserService;
import com.wxjw.jwbigdata.utils.ResultUtils;
import com.wxjw.jwbigdata.vo.AuthVo.UserInfoVo;
import com.wxjw.jwbigdata.vo.AuthVo.userAuth;
import com.wxjw.jwbigdata.vo.BaseResponse;
import com.wxjw.jwbigdata.vo.UserVo.DeptVo;
import com.wxjw.jwbigdata.vo.UserVo.UserVo;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.*;

import javax.xml.transform.Result;
import java.util.List;
import java.util.Map;

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
     * @return
     */
    @PostMapping("/addUser")
    public BaseResponse<Object> addUser(@RequestBody Map<String,UserInfoVo> data, HttpServletRequest request) throws JsonProcessingException {
        UserInfoVo userInfo = data.get("userInfo");
        userService.addUser(userInfo.getUserName(), userInfo.getRealName(), userInfo.getDept(), userInfo.getDuties(), userInfo.getPhone(), request);
        return ResultUtils.success(Code.SUCCESS, null, "新增用户成功！");
    }
    @PostMapping("/statusChange")
    public BaseResponse<Object> switchUserStatus(@RequestBody Map<String,Object> data) {
        userService.switchUserStatus((Integer)data.get("id"), (Boolean)data.get("status")==true?0:1);
        return ResultUtils.success(Code.SUCCESS, null, "修改用户状态成功");
    }

    @PostMapping("/deptStatusChange")
    public BaseResponse<Object> switchDeptStatus(@RequestBody Map<String,Object> data) {
        userService.switchDeptStatus((Integer)data.get("id"), (Boolean)data.get("status")==true?0:1);
        return ResultUtils.success(Code.SUCCESS, null, "修改用户状态成功");
    }


    //
    @PostMapping("delUser")
    public BaseResponse<Object> delUser(@RequestBody Map<String,int[]> data){
        userService.delUser(data.get("userId"));
        return ResultUtils.success(Code.SUCCESS, null, "删除用户成功！");
    }

    @PostMapping("/initPwd")
    public BaseResponse<Object> initPwd(@RequestBody Map<String,int[]> data) {
        int[] userId = data.get("userId");
        userService.initPwd(userId);
        return ResultUtils.success(Code.SUCCESS, null, "初始化用户密码成功！");
    }

    @PostMapping("/switchUserAuth")
    public BaseResponse<Object> switchUserAuth(@RequestBody userAuth userAuth) {
        userService.switchUserAuth(userAuth.getUserId(), userAuth.getStatus());
        return ResultUtils.success(Code.SUCCESS, null, "切换用户权限成功！");
    }

    @PostMapping("/addDept")
    public BaseResponse<Object> addDept(@RequestBody Map<String,DeptVo> data, HttpServletRequest request) throws JsonProcessingException {
        userService.addDept(data.get("deptInfo").getDeptName(), request);
        return ResultUtils.success(Code.SUCCESS, null, "新增部门成功！");
    }

    @PostMapping("delDept")
    public BaseResponse<Object> delDept(@RequestBody Map<String,int[]> data){
        userService.delDept(data.get("deptId"));
        return ResultUtils.success(Code.SUCCESS, null, "删除部门成功！");
    }

    @PostMapping("updateDeptInfo")
    public BaseResponse<Object> updataDeptInfo(@RequestBody Map<String,DeptVo> data){
        DeptVo deptInfo = data.get("deptInfo");
        userService.updataDeptInfo(deptInfo.getDeptId(),deptInfo.getDeptName());
        return ResultUtils.success(Code.SUCCESS, null, "修改部门成功！");
    }
}
