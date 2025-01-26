package com.wxjw.jwbigdata.service;


import com.baomidou.mybatisplus.extension.service.IService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.wxjw.jwbigdata.domain.User;
import com.wxjw.jwbigdata.vo.UserVo.UserVo;
import jakarta.servlet.http.HttpServletRequest;

import java.util.List;

/**
* @author yannqing
* @description 针对表【user】的数据库操作Service
* @createDate 2024-06-03 18:53:58
*/
public interface UserService extends IService<User> {

    List<UserVo> getUserList();

    void addUser(String userName, String realName, String dept, String duties, String phone,List<Integer> selectedTables, HttpServletRequest request) throws JsonProcessingException;

    void updateUserInfo(int userId,String dept, String duties, String phone,List<Integer> selectedTables, HttpServletRequest request) throws JsonProcessingException;

    void switchUserStatus(int userId, int status, HttpServletRequest request) throws JsonProcessingException;

    void switchDeptStatus(int deptId, int status, HttpServletRequest request) throws JsonProcessingException;

    void delUser(int[] userId, HttpServletRequest request) throws JsonProcessingException;

    void delDept(int[] deptId, HttpServletRequest request) throws JsonProcessingException;

    void initPwd(int[] userId, HttpServletRequest request) throws JsonProcessingException;

    void switchUserAuth(Integer userId, Boolean[] auth, HttpServletRequest request) throws JsonProcessingException;

    void addDept(String deptName,List<Integer> selectedTables, HttpServletRequest request) throws JsonProcessingException;

    void updataDeptInfo(int deptId, String deptName,List<Integer> selectedTables, HttpServletRequest request) throws JsonProcessingException;
}
