package com.wxjw.jwbigdata.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.wxjw.jwbigdata.common.Code;
import com.wxjw.jwbigdata.service.OperlogService;
import com.wxjw.jwbigdata.utils.ResultUtils;
import com.wxjw.jwbigdata.vo.BaseResponse;
import com.wxjw.jwbigdata.vo.LogVo.LogVo;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author black
 * @version 1.0.0
 * @className LogController
 * @description TODO
 * @date 2024-12-23 21:06
 **/
@RestController
public class LogController {
    @Resource
    private OperlogService operlogService;
    /**
     * 获取所有用户的列表
     * @return
     */
    @GetMapping("/getLogList")
    public BaseResponse<List<LogVo>> getLogList(HttpServletRequest request) throws JsonProcessingException {
        List<LogVo> LogList = operlogService.getLogList(request);
        return ResultUtils.success(Code.SUCCESS, LogList, "获取所有日志");
    }
}
