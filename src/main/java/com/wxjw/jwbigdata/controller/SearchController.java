package com.wxjw.jwbigdata.controller;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.wxjw.jwbigdata.common.Code;
import com.wxjw.jwbigdata.common.OperType;
import com.wxjw.jwbigdata.domain.NewTable;
import com.wxjw.jwbigdata.domain.Operlog;
import com.wxjw.jwbigdata.domain.User;
import com.wxjw.jwbigdata.mapper.NewTableMapper;
import com.wxjw.jwbigdata.mapper.OperlogMapper;
import com.wxjw.jwbigdata.service.NewTableService;
import com.wxjw.jwbigdata.utils.JwtUtils;
import com.wxjw.jwbigdata.utils.ResultUtils;
import com.wxjw.jwbigdata.vo.BaseResponse;
import com.wxjw.jwbigdata.vo.TableVo.AuthTable;
import com.wxjw.jwbigdata.vo.TableVo.SearchParams;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @author black
 * @version 1.0.0
 * @className SearchController
 * @description TODO
 * @date 2024-12-30 22:29
 **/
@RestController
public class SearchController {

    @Resource
    private NewTableService newTableService;
    @Resource
    private ObjectMapper objectMapper;
    @Resource
    private OperlogMapper operlogMapper;
    @Resource
    private NewTableMapper newTableMapper;

    @PostMapping("/getResultList")
    public BaseResponse<JSONObject> getSearchList(@RequestBody SearchParams data, HttpServletRequest request) throws JsonProcessingException {
        JSONObject result;
        String keyWord = data.getKeyWord();
        List<Integer> tables = data.getTabId();
        try {
            result = newTableService.searchListByKeyWord(tables,keyWord);
        } catch (Exception ex) {
            return ResultUtils.failure(Code.FAILURE, null, ex.getMessage());
        }

        NewTable newTable = newTableMapper.selectById(1);
        String tableName = newTable==null?"":newTable.getComment();
        //从token得到创建者的userId
        String token = request.getHeader("token");
        String userInfo = JwtUtils.getUserInfoFromToken(token);
        User loginUser = objectMapper.readValue(userInfo, User.class);
        Operlog operlog = new Operlog();
        operlog.setUserId(loginUser.getId());
        operlog.setOperType(OperType.getSearchList);
        operlog.setOperData("数据源："+tableName+";关键字："+keyWord);
        operlog.setOperTime(new Date());
        operlogMapper.insert(operlog);
        return ResultUtils.success(Code.SUCCESS, result, "获取检索结果");
    }
}
