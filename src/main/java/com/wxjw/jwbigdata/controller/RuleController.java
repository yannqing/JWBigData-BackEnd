package com.wxjw.jwbigdata.controller;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.wxjw.jwbigdata.common.Code;
import com.wxjw.jwbigdata.service.JwRuleService;
import com.wxjw.jwbigdata.service.JwRuledetailService;
import com.wxjw.jwbigdata.utils.ResultUtils;
import com.wxjw.jwbigdata.vo.BaseResponse;
import com.wxjw.jwbigdata.vo.RuleVo.RuleDetailVo;
import com.wxjw.jwbigdata.vo.RuleVo.RuleVo;
import com.wxjw.jwbigdata.vo.UserVo.UserVo;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.LinkedHashMap;
import java.util.List;

/**
 * @author panhao
 * @version 1.0.0
 * @className RoleController
 * @description 规则模块控制器
 * @date 2024-06-14 13:46
 **/
@RestController
public class RuleController {

    @Resource
    private JwRuleService jwRuleService;

    @Resource
    private JwRuledetailService jwRuledetailService;

    @PostMapping("/addRule")
    public BaseResponse<Object> addRule(String userId, String ruleName,String ruleComment, String ruleSteps) throws JsonProcessingException {
        jwRuleService.addRule(userId,ruleName,ruleComment,ruleSteps);
//        userService.addUser(userName, realName, dept, duties, phone, request);
        return ResultUtils.success(Code.SUCCESS, null, "新增规则成功！");
    }

    @PostMapping("delRule")
    public BaseResponse<Object> delRule(@RequestBody LinkedHashMap<String, List<String>> ruleId){
        List<String> ruleIds = ruleId.get("ruleId");
        jwRuleService.delRule(ruleIds.toArray(new String[ruleIds.size()]));
        return ResultUtils.success(Code.SUCCESS, null, "删除规则成功！");
    }

    @PostMapping("/switchRuleStatus")
    public BaseResponse<Object> switchRuleStatus(String ruleId, Integer status) {
        jwRuleService.switchRuleStatus(ruleId, status);
        return ResultUtils.success(Code.SUCCESS, null, "修改规则状态成功");
    }

    @GetMapping("/getRuleList")
    public BaseResponse<List<RuleVo>> getRuleList() {
        List<RuleVo> ruleList = jwRuleService.getRuleList();
        return ResultUtils.success(Code.SUCCESS, ruleList, "获取所有规则");
    }

    @PostMapping("/editRule")
    public BaseResponse<Object> editRule(Integer ruleId, String ruleName, String ruleComment, String ruleSteps){
        jwRuleService.editRule(ruleId, ruleName, ruleComment, ruleSteps);
        return ResultUtils.success(Code.SUCCESS, null, "修改规则成功");
    }

    @GetMapping("/getRules")
    public BaseResponse<Object> getRules(String userId) {
        JSONObject result = jwRuleService.getRules(userId);
        return ResultUtils.success(Code.SUCCESS, result, "获取所有规则");
    }

    @GetMapping("/getRuleInfo")
    public BaseResponse<Object> getRuleInfo(String ruleId) {
        RuleVo jwRule = jwRuleService.getRuleInfo(ruleId);
        return ResultUtils.success(Code.SUCCESS, jwRule, "获取规则");
    }

    @GetMapping("/getSubrules")
    public BaseResponse<List<RuleDetailVo>> getSubrules(String ruleId) {
        List<RuleDetailVo> RuleDetailVoList = jwRuledetailService.getSubrules(ruleId);
        return ResultUtils.success(Code.SUCCESS, RuleDetailVoList, "获取规则明细");
    }

    @PostMapping("/addSubrule")
    public BaseResponse<Object> addSubrule(String ruleId,String note,String tableId,String fieldName,String matchType,String pattern,String matchValue,String matchTableId,String matchFieldName) {
        jwRuledetailService.addSubrule(ruleId,note,tableId,fieldName,matchType,pattern,matchValue,matchTableId,matchFieldName);
        return ResultUtils.success(Code.SUCCESS, null, "添加规则明细成功");
    }

    @PostMapping("/delSubrule")
    public BaseResponse<Object> delSubrule(String subruleId){
        jwRuledetailService.delSubrule(subruleId);
        return ResultUtils.success(Code.SUCCESS, null, "删除规则明细成功！");
    }

    @PostMapping("/switchRuleOn")
    public BaseResponse<Object> switchRuleOn(String ruleId, Integer isOn) {
        jwRuleService.switchRuleOn(ruleId, isOn);
        return ResultUtils.success(Code.SUCCESS, null, "修改规则启用状态成功");
    }

//    @PostMapping("/getRuleResult")
//    public BaseResponse<JSONArray> getRuleResult(String ruleId) {
//        JSONArray result = jwRuleService.getRuleResult(ruleId);
//        return ResultUtils.success(Code.SUCCESS, result, "获取模型分析结果");
//    }

    @PostMapping("/getRuleResult")
    public BaseResponse<JSONObject> getRuleResult(String ruleId,String userid) {
        JSONObject result = jwRuleService.getResultDetail(ruleId,userid);
        return ResultUtils.success(Code.SUCCESS, result, "获取模型分析详情结果");
    }
}
