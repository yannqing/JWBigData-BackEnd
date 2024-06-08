package com.wxjw.jwbigdata.vo.UserVo;

import com.wxjw.jwbigdata.domain.User;
import lombok.Data;

@Data
public class UserVo {
    private Integer userId;
    private String userName;
    private String realName;
    private String dept;
    private String duties;
    private String phone;
    private Integer status;

    public UserVo(User user, String dept) {
        this.userId = user.getId();
        this.userName = user.getUserAccount();
        this.realName = user.getUsername();
        this.dept = dept;
        this.duties = user.getPosition();
        this.phone = user.getPhone();
        this.status = user.getStatus();
    }
}
