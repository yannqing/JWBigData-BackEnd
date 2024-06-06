package com.wxjw.jwbigdata.service;


import com.baomidou.mybatisplus.extension.service.IService;
import com.wxjw.jwbigdata.domain.User;
import com.wxjw.jwbigdata.vo.AuthVo.DeptVo;
import com.wxjw.jwbigdata.vo.AuthVo.UserInfoVo;
import jakarta.servlet.http.HttpServletRequest;

import java.util.List;

/**
* @author yannqing
* @description 针对登录和个人信息管理的 Service
* @createDate 2024-06-03 18:53:58
*/
public interface AuthService extends IService<User> {

    UserInfoVo getMyInfo(Integer userId);
    List<DeptVo> getDeptList();

    void updateMyInfo(Integer userId, String dept, String duties, String phone);

    void updateMyPwd(Integer userId, String oldPwd, String newPwd, HttpServletRequest request);
}
