package com.wxjw.jwbigdata.service;

import com.wxjw.jwbigdata.domain.JwRuledetail;
import com.baomidou.mybatisplus.extension.service.IService;
import com.wxjw.jwbigdata.vo.BaseResponse;
import com.wxjw.jwbigdata.vo.RuleVo.RuleDetailVo;

import java.util.List;

/**
* @author Paul
* @description 针对表【jw_ruledetail】的数据库操作Service
* @createDate 2024-06-14 14:07:54
*/
public interface JwRuledetailService extends IService<JwRuledetail> {
    List<RuleDetailVo> getSubrules(String ruleId);

    void addSubrule(String ruleId, String note, String tableId, String fieldName, String matchType, String pattern, String matchValue, String matchTableId, String matchFieldName);

    void delSubrule(String subruleId);
}
