package com.wxjw.jwbigdata.vo.AuthVo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.beans.IntrospectionException;

/**
 * @author black
 * @version 1.0.0
 * @className userAuth
 * @description TODO
 * @date 2024-11-22 22:37
 **/
@Data
@AllArgsConstructor
@NoArgsConstructor
public class userAuth {
    private Integer userId;
    private Boolean[] status;
}
