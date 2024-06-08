package com.wxjw.jwbigdata;

import com.wxjw.jwbigdata.domain.User;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@SpringBootTest
class JwBigDataApplicationTests {

    /**
     * java8 流测试
     */
    @Test
    void contextLoads() {
//        List<String> list = new ArrayList<>();
//        list.add("测试1");
//        list.add("测试2");
//        list.add("测试3");
//        list.add("测试4");
//        list.add("测试5");
//
//        list.stream().forEach(test -> {
//            test += " test";
//            System.out.println(test);
//        });
//        List<String> collect = list.stream().map(test -> test += " qwer").collect(Collectors.toList());
//        System.out.println(collect);
//        list.forEach(System.out::println);

        List<User> users = new ArrayList<>();
        User user1 = new User();
        user1.setUsername("111");
        User user2 = new User();
        user2.setUsername("222");
        User user3 = new User();
        user3.setUsername("333");

        users = Arrays.asList(user1, user2, user3);
//        users.stream().forEach(o->o.setUsername("444"));
//        users.forEach(o-> System.out.println(o.getUsername()));

        users.stream().map(o -> {
            o.setUsername("xxx");
            return null;
        });
        users.forEach(o-> System.out.println(o.getUsername()));

    }

}
