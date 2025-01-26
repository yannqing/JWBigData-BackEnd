package com.wxjw.jwbigdata.service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.wxjw.jwbigdata.domain.JwRule;
import com.baomidou.mybatisplus.extension.service.IService;
import com.wxjw.jwbigdata.vo.RuleVo.RuleDetailVo;
import com.wxjw.jwbigdata.vo.RuleVo.RuleVo;
import com.wxjw.jwbigdata.vo.UserVo.UserVo;
import jakarta.servlet.http.HttpServletRequest;

import java.util.List;

/**
* @author Paul
* @description 针对表【jw_rule】的数据库操作Service
* @createDate 2024-06-14 14:06:48
*/
public interface JwRuleService extends IService<JwRule> {
    void addRule(Integer userId, String ruleName,String ruleComment, String ruleSteps);

    void delRule(String[] ruleId,HttpServletRequest request) throws JsonProcessingException;

    void switchRuleStatus(Integer ruleId, Integer status,HttpServletRequest request) throws JsonProcessingException;

    List<RuleVo> getRuleList();

    void editRule(Integer ruleId, String ruleName, String ruleComment, String ruleSteps, HttpServletRequest request) throws JsonProcessingException;

    JSONObject getRules(String userId);

    RuleVo getRuleInfo(String ruleId);

    void switchRuleOn(String ruleId, Integer isOn, HttpServletRequest request) throws JsonProcessingException;

    JSONObject getRuleResult(String ruleId, HttpServletRequest request) throws JsonProcessingException;

    JSONArray getResultDetail(String ruleId,String id);

    JwRule getRuleById(String ruleId);


}
