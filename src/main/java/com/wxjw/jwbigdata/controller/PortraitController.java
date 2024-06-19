package com.wxjw.jwbigdata.controller;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.wxjw.jwbigdata.common.Code;
import com.wxjw.jwbigdata.service.JwTableService;
import com.wxjw.jwbigdata.utils.ResultUtils;
import com.wxjw.jwbigdata.vo.BaseResponse;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.regex.Pattern;

/**
 * @author panhao
 * @version 1.0.0
 * @className Portrait
 * @description TODO
 * @date 2024-06-16 22:04
 **/
@RestController
public class PortraitController {
    @Resource
    private JwTableService jwTableService;

    @PostMapping("/portrait")
    public BaseResponse<JSONObject> portrait(String type, String keyWord) {
        if(type.equals("1"))
        {
            // 人物画像
            String regex = "(^\\d{15}$)|(^\\d{18}$)|(^\\d{17}(\\d|X|x)$)";
            Boolean isId = Pattern.matches(regex, keyWord);
            if(isId){
                // 关键字是身份证号
                JSONArray result = jwTableService.humanIdPortrait(keyWord);
            }
            else{
                // 关键字是姓名
                JSONArray result = jwTableService.humanNamePortrait(keyWord);
            }
        }
        else if(type.equals("2"))
        {
            // 单位画像
            JSONArray result = jwTableService.companyNamePortrait(keyWord);
        }
        else return ResultUtils.failure(Code.FAILURE, null, "画像类型错误！");
//        JSONArray result = jwRuleService.getRuleResult(ruleId);
        return ResultUtils.success(Code.SUCCESS, null, "获取人物画像结果");
    }
}
