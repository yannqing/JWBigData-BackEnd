package com.wxjw.jwbigdata.common;

import lombok.Data;
import org.springframework.context.annotation.Configuration;

import java.util.Arrays;
import java.util.List;

/**
 * 常量类
 */
@Data
@Configuration
public class Constant {

    public static String[] annos = {
            "/register",
            "/login",
            "/logout"
    };

    public static List<String> annosList = Arrays.asList(annos);
}
