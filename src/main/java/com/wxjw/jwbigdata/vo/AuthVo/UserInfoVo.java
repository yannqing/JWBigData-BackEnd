package com.wxjw.jwbigdata.vo.AuthVo;

import com.wxjw.jwbigdata.domain.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserInfoVo {

    private String userName;
    private String realName;
    private String dept;
    private String duties;
    private String phone;

    public UserInfoVo(User user, String dept) {
        this.userName = String.valueOf(user.getUserAccount());
        this.realName = user.getUsername();
        this.dept = dept;
        this.duties = user.getPosition();
        this.phone = user.getPhone();
    }

}
