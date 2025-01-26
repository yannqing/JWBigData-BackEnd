package com.wxjw.jwbigdata.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.wxjw.jwbigdata.domain.Operlog;
import com.baomidou.mybatisplus.extension.service.IService;
import com.wxjw.jwbigdata.vo.LogVo.LogVo;
import jakarta.servlet.http.HttpServletRequest;

import java.util.List;

/**
* @author Paul
* @description 针对表【operlog】的数据库操作Service
* @createDate 2024-12-13 14:14:02
*/
public interface OperlogService extends IService<Operlog> {
    void InsertLog(Operlog operlog);

    List<LogVo> getLogList(HttpServletRequest request) throws JsonProcessingException;
}
