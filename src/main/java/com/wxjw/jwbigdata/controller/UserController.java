package com.wxjw.jwbigdata.controller;

import com.wxjw.jwbigdata.service.UserService;
import com.wxjw.jwbigdata.vo.BaseResponse;
import com.wxjw.jwbigdata.vo.UserVo.UserVo;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

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
        userService.getUserList();
        return null;
    }
}
