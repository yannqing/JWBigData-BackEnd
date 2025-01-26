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
import jakarta.annotation.Resource;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;

import java.io.IOException;
import java.util.Date;

@Slf4j
public class MyLogoutSuccessHandler implements LogoutSuccessHandler {

    private RedisCache redisCache;

    OperlogService operlogService;

    public MyLogoutSuccessHandler(RedisCache redisCache,OperlogService operlogService) {
        this.redisCache = redisCache;
        this.operlogService = operlogService;
    }

    @Resource
    private ObjectMapper objectMapper;

    @Resource
    private OperlogMapper operlogMapper;
    /**
     * 退出成功处理器：删除redis中的token即可
     * @param request
     * @param response
     * @param authentication
     * @throws IOException
     * @throws ServletException
     */
    @Override
    public void onLogoutSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        String token = request.getHeader("token");
        redisCache.deleteObject("token:"+token);

        response.setStatus(200);
        response.setContentType("application/json;charset=UTF-8");
        response.getWriter().write(JSONUtil.toJsonStr(ResultUtils.success(Code.LOGOUT_SUCCESS,null,"退出成功！")));
        //从token得到创建者的userId
        String userInfo = JwtUtils.getUserInfoFromToken(token);
        User loginUser = objectMapper.readValue(userInfo, User.class);

        Operlog operlog = new Operlog();
        operlog.setUserId(loginUser.getId());
        operlog.setOperType(OperType.loginOut);
        operlog.setOperData("注销成功");
        operlog.setOperTime(new Date());
        operlogMapper.insert(operlog);
        log.info("退出成功!");
    }
}
