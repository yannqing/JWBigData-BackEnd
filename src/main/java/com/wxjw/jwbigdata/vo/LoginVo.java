package com.yannqing.dockerdesktop.vo;

import com.yannqing.dockerdesktop.domain.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoginVo {

    private Integer user_id;
    private String username;
    private String password;
    private String phone;
    private String email;
    private Integer age;
    private String sex;
    private String avatar;
    private String nick_name;
    private String token;
    private int role;
    private List<String> authList;

    public LoginVo(User user, String token, int role, List<String> authList) {
        this.user_id = user.getUser_id();
        this.username = user.getUsername();
        this.password = user.getPassword();
        this.phone = user.getPhone();
        this.email = user.getEmail();
        this.age = user.getAge();
        this.sex = user.getSex();
        this.avatar = user.getAvatar();
        this.nick_name = user.getNick_name();
        this.token = token;
        this.role = role;
        this.authList = authList;
    }

}
