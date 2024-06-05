package com.yannqing.dockerdesktop.security.handler;

import cn.hutool.json.JSONUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.yannqing.dockerdesktop.common.Code;
import com.yannqing.dockerdesktop.domain.User;
import com.yannqing.dockerdesktop.utils.JwtUtils;
import com.yannqing.dockerdesktop.utils.RedisCache;
import com.yannqing.dockerdesktop.utils.ResultUtils;
import com.yannqing.dockerdesktop.vo.LoginVo;
import com.yannqing.dockerdesktop.vo.SecurityUser;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Slf4j
public class MyLoginSuccessHandler implements AuthenticationSuccessHandler {


    RedisCache redisCache;


    public MyLoginSuccessHandler(RedisCache redisCache) {
        this.redisCache = redisCache;
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

        List<SimpleGrantedAuthority> authorities = (List<SimpleGrantedAuthority>) securityUser.getAuthorities();
        List<String> authList = authorities.stream().map(SimpleGrantedAuthority::getAuthority).collect(Collectors.toList());
        //生成token
        String token = JwtUtils.token(userInfo, authList);
        //将token存入redis中
        redisCache.setCacheObject("token:"+token,String.valueOf(authentication),60*60*3, TimeUnit.SECONDS);

        LoginVo userInfoVo = new LoginVo(user,token,securityUser.getRole(), authList);

//        Map<String,Object> map = new HashMap<>();
//        map.put("token", token);
//        map.put("authList", authList);
//        map.put("role", securityUser.getRole());
//        map.put("userInfo",userInfoVo);

        response.getWriter().write(JSONUtil.toJsonStr(ResultUtils.success(Code.LOGIN_SUCCESS, userInfoVo,"登录成功")));
        log.info("登录成功！");
    }
}
