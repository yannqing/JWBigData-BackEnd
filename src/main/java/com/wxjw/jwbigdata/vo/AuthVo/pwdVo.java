package com.wxjw.jwbigdata.vo.AuthVo;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * @author black
 * @version 1.0.0
 * @className pwdVo
 * @description TODO
 * @date 2024-11-21 15:59
 **/
@Data
@AllArgsConstructor
public class pwdVo {

    private String oldPwd;
    private String newPwd;
    private String confPwd;

}
