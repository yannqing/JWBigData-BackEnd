package com.yannqing.dockerdesktop.vo;

import com.yannqing.dockerdesktop.domain.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserInfoVo {

    private Integer user_id;
    private String username;
    private String phone;
    private String email;
    private Integer age;
    private String sex;
    private String avatar;
    private String nick_name;
    private int role;
    private int internet;
    private int disk_size;

    public UserInfoVo(User user, int role, List<String> authList) {
        this.user_id = user.getUser_id();
        this.username = user.getUsername();
        this.phone = user.getPhone();
        this.email = user.getEmail();
        this.age = user.getAge();
        this.sex = user.getSex();
        this.avatar = user.getAvatar();
        this.nick_name = user.getNick_name();
        this.role = role;
        this.internet = user.getInternet();
        this.disk_size = user.getDisk_size();
    }

}
