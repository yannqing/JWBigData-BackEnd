package com.yannqing.dockerdesktop.common;

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
            "/auth/register",
            "/auth/login",
            "/auth/logout"
    };

    public static List<String> annosList = Arrays.asList(annos);
}
