package com.wxjw.jwbigdata.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.wxjw.jwbigdata.common.OperType;
import com.wxjw.jwbigdata.domain.Operlog;
import com.wxjw.jwbigdata.domain.User;
import com.wxjw.jwbigdata.mapper.UserMapper;
import com.wxjw.jwbigdata.service.OperlogService;
import com.wxjw.jwbigdata.mapper.OperlogMapper;
import com.wxjw.jwbigdata.utils.JwtUtils;
import com.wxjw.jwbigdata.vo.LogVo.LogVo;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
* @author Paul
* @description 针对表【operlog】的数据库操作Service实现
* @createDate 2024-12-13 14:14:02
*/
@Service
@Slf4j
public class OperlogServiceImpl extends ServiceImpl<OperlogMapper, Operlog>
    implements OperlogService{
    @Resource
    private OperlogMapper operlogMapper;
    @Resource
    private UserMapper userMapper;
    @Resource
    private ObjectMapper objectMapper;
    @Override
    public void InsertLog(Operlog operlog) {
        operlogMapper.insert(operlog);
    }

    @Override
    public List<LogVo> getLogList(HttpServletRequest request) throws JsonProcessingException {
        //从token得到创建者的userId
        String token = request.getHeader("token");
        String userInfo = JwtUtils.getUserInfoFromToken(token);
        User loginUser = objectMapper.readValue(userInfo, User.class);

        List<Operlog> operlogs = operlogMapper.selectList(new QueryWrapper<Operlog>().orderByDesc("oper_time"));
        List<LogVo> logList = new ArrayList<>();
        for (Operlog operlog : operlogs) {
            User operUser = userMapper.selectById(operlog.getUserId());
            LogVo logVo = new LogVo(operlog, operUser);
            logList.add(logVo);
        }
        Operlog operlog = new Operlog();
        operlog.setUserId(loginUser.getId());
        operlog.setOperType(OperType.getLogList);
        operlog.setOperData("查询成功");
        operlog.setOperTime(new Date());
        operlogMapper.insert(operlog);
        return logList;
    }
}




