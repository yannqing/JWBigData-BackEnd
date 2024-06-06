package com.wxjw.jwbigdata.vo.UserVo;

import com.wxjw.jwbigdata.domain.User;
import lombok.Data;

@Data
public class UserVo {
    private Integer userId;
    private Integer userName;
    private String realName;
    private String dept;
    private String duties;
    private String phone;
    private Integer status;

    public UserVo(User user) {
        this.userId = user.getId();
        this.userName = user.getUserAccount();
        this.realName = user.getUsername();
    }
}
