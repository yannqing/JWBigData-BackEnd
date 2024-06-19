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
    void addRule(String userId, String ruleName,String ruleComment, String ruleSteps) throws JsonProcessingException;

    void delRule(String[] ruleId);

    void switchRuleStatus(String ruleId, Integer status);

    List<RuleVo> getRuleList();

    void editRule(Integer ruleId, String ruleName, String ruleComment, String ruleSteps);

    JSONObject getRules(String userId);

    RuleVo getRuleInfo(String ruleId);

    void switchRuleOn(String ruleId, Integer isOn);

    JSONArray getRuleResult(String ruleId);

    JSONObject getResultDetail(String ruleId,String id);


}
