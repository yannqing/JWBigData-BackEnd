package com.wxjw.jwbigdata.controller;

import com.wxjw.jwbigdata.common.Code;
import com.wxjw.jwbigdata.service.AuthService;
import com.wxjw.jwbigdata.utils.ResultUtils;
import com.wxjw.jwbigdata.vo.AuthVo.pwdInfo;
import com.wxjw.jwbigdata.vo.AuthVo.pwdVo;
import com.wxjw.jwbigdata.vo.BaseResponse;
import com.wxjw.jwbigdata.vo.AuthVo.DeptVo;
import com.wxjw.jwbigdata.vo.AuthVo.UserInfoVo;
import com.wxjw.jwbigdata.vo.UserVo.UserVo;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
public class AuthController {

    @Resource
    private AuthService userService;

    @GetMapping("/getMyInfo")
    public BaseResponse<UserInfoVo> getMyInfo(Integer userId) {
        UserInfoVo myInfo = userService.getMyInfo(userId);

        return ResultUtils.success(Code.SUCCESS, myInfo, "获取个人信息成功");
    }

    @GetMapping("/getDeptList")
    public BaseResponse<List<DeptVo>> getDeptList() {
        List<DeptVo> deptList = userService.getDeptList();
        return ResultUtils.success(Code.SUCCESS, deptList, "查询全部部门列表成功！");
    }

    @PostMapping("/updateMyInfo")
    public BaseResponse<Object> updateMyInfo(@RequestBody UserVo userInfo){
        userService.updateMyInfo(userInfo.getUserId(), userInfo.getDept(), userInfo.getDuties(), userInfo.getPhone());
        return ResultUtils.success();
    }

    @PostMapping("/updateMyPwd")
    public BaseResponse<Object> updateMyPwd(@RequestBody pwdInfo pwdInfo, HttpServletRequest request) {
        pwdVo pwd = pwdInfo.getPwdInfo();
        int userId = pwdInfo.getUserId();
        userService.updateMyPwd(userId, pwd.getOldPwd(), pwd.getNewPwd(), request);
        return ResultUtils.success(Code.SUCCESS, null, "修改密码成功");
    }


}
