package com.wxjw.jwbigdata.security.filter;

import cn.hutool.json.JSONUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.wxjw.jwbigdata.common.Code;
import com.wxjw.jwbigdata.common.Constant;
import com.wxjw.jwbigdata.domain.User;
import com.wxjw.jwbigdata.utils.JwtUtils;
import com.wxjw.jwbigdata.utils.RedisCache;
import com.wxjw.jwbigdata.utils.ResultUtils;
import com.wxjw.jwbigdata.utils.YannqingTools;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
public class JwtAuthenticationTokenFilter extends OncePerRequestFilter {


    private RedisCache redisCache;

    public JwtAuthenticationTokenFilter(RedisCache redisCache) {
        this.redisCache = redisCache;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        //匿名地址，直接放行
        String requestURI = request.getRequestURI();
        if (YannqingTools.contains(requestURI, Constant.annos)) {
            filterChain.doFilter(request,response);
            return;
        }

        String token = request.getHeader("token");
        //验证token的合法性，不报错即合法

        String redisToken = redisCache.getCacheObject("token:" + token);
        log.info(token);
        if (redisToken==null) {
            response.setStatus(200);
            response.setContentType("application/json;charset=utf-8");
            response.getWriter().write(JSONUtil.toJsonStr(ResultUtils.failure(Code.TOKEN_EXPIRE,null,"您已退出，请重新登录")));
            log.info("您已退出，请重新登录");
            return;
        }

        if (token!=null){
            try {
                //验证token的合法性，不抛异常则合法
                JwtUtils.tokenVerify(token);
                //从token中获取到用户的信息，以及对应用户的权限信息
                String userInfo = JwtUtils.getUserInfoFromToken(token);
                ObjectMapper objectMapper = new ObjectMapper();
                User user = objectMapper.readValue(userInfo, User.class);
                List<String> userAuthorization = JwtUtils.getUserAuthorizationFromToken(token);
                List<SimpleGrantedAuthority> authorities = userAuthorization.stream().map(SimpleGrantedAuthority::new).collect(Collectors.toList());
                //放行后面的用户名密码过滤器
                UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(user,null,authorities);
                SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
            }catch (Exception e){
                response.setStatus(200);
                response.setContentType("application/json;charset=UTF-8");
                response.getWriter().write(JSONUtil.toJsonStr(ResultUtils.failure(Code.TOKEN_AUTHENTICATE_FAILURE,null,"非法token")));
                log.error("非法token");
                return;
            }
        }
        filterChain.doFilter(request,response);
    }
}
