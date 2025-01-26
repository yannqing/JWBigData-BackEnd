package com.wxjw.jwbigdata.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wxjw.jwbigdata.domain.UserTable;
import com.wxjw.jwbigdata.service.UserTableService;
import com.wxjw.jwbigdata.mapper.UserTableMapper;
import org.springframework.stereotype.Service;

/**
* @author Paul
* @description 针对表【user_table】的数据库操作Service实现
* @createDate 2024-12-17 14:35:06
*/
@Service
public class UserTableServiceImpl extends ServiceImpl<UserTableMapper, UserTable>
    implements UserTableService{

}




