package com.wxjw.jwbigdata.vo.RuleVo;

import com.wxjw.jwbigdata.domain.JwRule;
import com.wxjw.jwbigdata.domain.User;
import lombok.Data;

import java.util.Date;

/**
 * @author panhao
 * @version 1.0.0
 * @className RuleVo
 * @description TODO
 * @date 2024-06-14 15:51  ruleId:String, ruleName:String, ruleComment:String, ruleType:String, creator:String,createTime:dataTime
 **/
@Data
public class RuleVo {
    private Integer ruleId;

    private String ruleName;

    private String ruleComment;

    private Integer ruleType;

    private String creator;

    private Date createTime;

    public RuleVo(JwRule rule, User user){
        this.ruleId = rule.getRuleId();
        this.ruleName = rule.getRuleName();
        this.ruleComment = rule.getDescription();
        this.ruleType = rule.getStatus();
        this.creator = user.getUsername();
        this.createTime = rule.getCreateTime();
    }
}
