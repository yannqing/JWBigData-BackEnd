package com.wxjw.jwbigdata.UserController;
import java.util.Date;

import com.wxjw.jwbigdata.domain.User;
import com.wxjw.jwbigdata.mapper.UserMapper;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;

@SpringBootTest
public class UserAuthenticationTests {

    @Resource
    private UserMapper userMapper;

    @Resource
    private PasswordEncoder passwordEncoder;

    /**
     * 手动新增用户
     */
    @Test
    void addUser() {
        User user = new User();
        user.setUserAccount("1002");
        user.setPassword(passwordEncoder.encode("123456"));
        user.setUsername("test2");
        user.setDepartmentId(0);
        user.setPosition("测试");
        user.setRole(0);
        user.setPortrait(0);
        user.setCompare(0);
        user.setModel(0);
        user.setCreatedUser(0);
        user.setCreatedTime(new Date());
        user.setIsDelete(0);

        userMapper.insert(user);
    }

    /**
     * 测试加密密码
     */
    @Test
    void passwordEncoder() {
        User user = userMapper.selectById(2);
        boolean matches = passwordEncoder.matches("123456", user.getPassword());
        if (matches) {
            System.out.println("yes");
        }else {
            System.out.println("no");
        }
    }
}
