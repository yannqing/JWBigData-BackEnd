package com.wxjw.jwbigdata.security.handler;

import cn.hutool.json.JSONUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.wxjw.jwbigdata.common.Code;
import com.wxjw.jwbigdata.common.OperType;
import com.wxjw.jwbigdata.domain.Operlog;
import com.wxjw.jwbigdata.domain.User;
import com.wxjw.jwbigdata.mapper.OperlogMapper;
import com.wxjw.jwbigdata.service.OperlogService;
import com.wxjw.jwbigdata.utils.JwtUtils;
import com.wxjw.jwbigdata.utils.RedisCache;
import com.wxjw.jwbigdata.utils.ResultUtils;
import com.wxjw.jwbigdata.vo.AuthVo.LoginVo;
import com.wxjw.jwbigdata.vo.SecurityUser;
import jakarta.annotation.Resource;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Slf4j
public class MyLoginSuccessHandler implements AuthenticationSuccessHandler {


    private RedisCache redisCache;

    private OperlogService operlogService;


    public MyLoginSuccessHandler(RedisCache redisCache,OperlogService operlogService) {
        this.redisCache = redisCache;
        this.operlogService = operlogService;
    }

    /**
     * 登录成功处理器：返回用户信息，对应用户的权限信息，登录生成token
     * @param request
     * @param response
     * @param authentication
     * @throws IOException
     * @throws ServletException
     */
    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        response.setContentType("application/json;charset=UTF-8");

        SecurityUser securityUser = (SecurityUser) authentication.getPrincipal();
        User user = securityUser.getUser();
        ObjectMapper objectMapper = new ObjectMapper();
        String userInfo = objectMapper.writeValueAsString(user);

        List<Integer> authList = new ArrayList<>();
        authList.add(user.getRole());
        authList.add(user.getPortrait());
        authList.add(user.getSearch());
        authList.add(user.getCompare());
        authList.add(user.getModel());
       //生成token
        String token = JwtUtils.token(userInfo, authList);
        //将token存入redis中，并设置token过期时间：3小时
        redisCache.setCacheObject("token:"+token,String.valueOf(authentication),60*60*3, TimeUnit.SECONDS);

        LoginVo userInfoVo = new LoginVo(user, token, authList);

        response.getWriter().write(JSONUtil.toJsonStr(ResultUtils.success(Code.LOGIN_SUCCESS, userInfoVo,"登录成功")));
        Operlog operlog = new Operlog();
        operlog.setUserId(user.getId());
        operlog.setOperType(OperType.loginIn);
        operlog.setOperData("登录成功");
        operlog.setOperTime(new Date());
        operlogService.InsertLog(operlog);
        log.info("登录成功！");
    }
}
