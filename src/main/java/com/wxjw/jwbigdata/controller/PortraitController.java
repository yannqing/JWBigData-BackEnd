package com.wxjw.jwbigdata.controller;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.wxjw.jwbigdata.common.Code;
import com.wxjw.jwbigdata.domain.RelationOfNewtable;
import com.wxjw.jwbigdata.service.NewTableService;
import com.wxjw.jwbigdata.service.RelationOfNewtableService;
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
    private NewTableService newTableService;


    /**
     *
     * @param type:"1"是人物，“2”是单位
     * @param keyWord:身份证号、姓名、公司名、公司id
     * @return
     */
    @PostMapping("/getPortraitList")
    public BaseResponse<JSONObject> getPortraitList(String type, String keyWord){
        JSONArray result;
        if(type.equals("1"))
        {
            // 人物画像
            String regex = "(^\\d{15}$)|(^\\d{18}$)|(^\\d{17}(\\d|X|x)$)";
            Boolean isId = Pattern.matches(regex, keyWord);
            if(isId){
                try{
                    // 关键字是身份证
                    result = newTableService.humanListByid(keyWord);
                }
                catch (Exception ex){
                    return ResultUtils.failure(Code.FAILURE, null, ex.getMessage());
                }

            }
            else{
                try{
                    // 关键字是姓名
                    result = newTableService.humanListByName(keyWord);
                }
                catch (Exception ex){
                    return ResultUtils.failure(Code.FAILURE, null, ex.getMessage());
                }
            }
        }
        else if(type.equals("2"))
        {
            try{
                // 单位画像
                result = newTableService.companyList(keyWord);
            }
            catch (Exception ex){
                return ResultUtils.failure(Code.FAILURE, null, ex.getMessage());
            }
        }
        else return ResultUtils.failure(Code.FAILURE, null, "画像类型错误！");
//        JSONArray result = jwRuleService.getRuleResult(ruleId);
        return ResultUtils.success(Code.SUCCESS, result, "获取人物画像结果");
    }

    /**
     *
     * @param type:"1"是人物，“2”是单位
     * @param keyInfo：String  //对于个人，是id；对于企业，可能name或idNum（企业可能没有）
     * @return
     */
    @PostMapping("/getResultDetail")
    public BaseResponse<JSONObject> portrait(String type, String keyInfo) {
        JSONObject result = new JSONObject();
        if(type.equals("1"))
        {
            try{
                // 人物画像
                // 关键字是id主键
                result = newTableService.humanIdPortrait(keyInfo);
            }
            catch (Exception ex){
                return ResultUtils.failure(Code.FAILURE, null, ex.getMessage());
            }

        }
        else if(type.equals("2"))
        {
            try{
                // 单位画像
                result = newTableService.companyPortrait(keyInfo);
            }
            catch (Exception ex){
                return ResultUtils.failure(Code.FAILURE, null, ex.getMessage());
            }
        }
        else
            return ResultUtils.failure(Code.FAILURE, result, "画像类型错误！");
//        JSONArray result = jwRuleService.getRuleResult(ruleId);
        return ResultUtils.success(Code.SUCCESS, result, "获取人物画像结果");
    }
}
