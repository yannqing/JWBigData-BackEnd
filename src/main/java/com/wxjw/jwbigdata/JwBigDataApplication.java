package com.wxjw.jwbigdata;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
//@EnableScheduling
@MapperScan("com.wxjw.jwbigdata.mapper")
public class JwBigDataApplication {

    public static void main(String[] args) {
        SpringApplication.run(JwBigDataApplication.class, args);
    }

}
