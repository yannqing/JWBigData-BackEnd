package com.wxjw.jwbigdata.service;


import com.baomidou.mybatisplus.extension.service.IService;
import com.wxjw.jwbigdata.domain.User;
import com.wxjw.jwbigdata.vo.UserVo.UserVo;

import java.util.List;

/**
* @author yannqing
* @description 针对表【user】的数据库操作Service
* @createDate 2024-06-03 18:53:58
*/
public interface UserService extends IService<User> {

    List<UserVo> getUserList();
}
