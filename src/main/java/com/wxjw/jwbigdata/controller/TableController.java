package com.wxjw.jwbigdata.controller;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.wxjw.jwbigdata.common.Code;
import com.wxjw.jwbigdata.service.JwFieldService;
import com.wxjw.jwbigdata.service.JwTableService;
import com.wxjw.jwbigdata.utils.ResultUtils;
import com.wxjw.jwbigdata.vo.BaseResponse;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author panhao
 * @version 1.0.0
 * @className TableController
 * @description TODO
 * @date 2024-06-15 12:10
 **/
@RestController
public class TableController {
    @Resource
    private JwTableService jwTableService;

    @PostMapping("/getFieldsList")
    public BaseResponse<Object> getFieldsList(){
        JSONArray result = jwTableService.getFieldsList();
        return ResultUtils.success(Code.SUCCESS,result,"获取所有字段");
    }
}
