package com.wxjw.jwbigdata.vo.RuleVo;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.wxjw.jwbigdata.domain.JwRule;
import com.wxjw.jwbigdata.domain.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author panhao
 * @version 1.0.0
 * @className RuleVo
 * @description TODO
 * @date 2024-06-14 15:51  ruleId:String, ruleName:String, ruleComment:String, ruleType:String, creator:String,createTime:dataTime
 **/
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RuleVo {
    private Integer ruleId;

    public Integer getRuleId() {
        return ruleId;
    }

    public void setRuleId(Integer ruleId) {
        this.ruleId = ruleId;
    }

    public String getRuleName() {
        return ruleName;
    }

    public void setRuleName(String ruleName) {
        this.ruleName = ruleName;
    }

    public String getRuleComment() {
        return ruleComment;
    }

    public void setRuleComment(String ruleComment) {
        this.ruleComment = ruleComment;
    }

    public String getRuleSteps() {
        return ruleSteps;
    }

    public void setRuleSteps(String ruleSteps) {
        this.ruleSteps = ruleSteps;
    }

    public String getRuleType() {
        return ruleType;
    }

    public void setRuleType(String ruleType) {
        this.ruleType = ruleType;
    }

    public String getCreator() {
        return creator;
    }

    public void setCreator(String creator) {
        this.creator = creator;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public boolean getIsPublic() {
        return isPublic;
    }

    public void setIsPublic(boolean IsPublic) {
        isPublic = IsPublic;
    }

    private String ruleName;

    private String ruleComment;

    private String ruleSteps;

    private String ruleType;

    private String creator;

    private String createTime;

    private boolean isPublic;

    public RuleVo(JwRule rule, User user){
        this.ruleId = rule.getRuleId();
        this.ruleName = rule.getRuleName();
        this.ruleComment = rule.getDescription();
        this.ruleSteps = rule.getNote();
        if(rule.getIsOn() == null || rule.getIsOn() == 0){
            this.ruleType ="未启用";
        }
        else
            this.ruleType = "已启用";
        this.creator = user.getUsername();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        if(rule.getCreateTime() != null){
            this.createTime = formatter.format(rule.getCreateTime());
        }
        else this.createTime = "";
        if(rule.getStatus() == null || rule.getStatus() == 0){
            this.isPublic = false;
        }
        else this.isPublic = true;
    }
}
