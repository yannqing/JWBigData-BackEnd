package com.wxjw.jwbigdata.vo.RuleVo;

import com.wxjw.jwbigdata.domain.JwField;
import com.wxjw.jwbigdata.domain.JwRuledetail;
import com.wxjw.jwbigdata.domain.JwTable;
import lombok.Data;

/**
 * @author panhao
 * @version 1.0.0
 * @className RuleDetailVo
 * @description TODO
 * @date 2024-06-15 11:32
 **/
@Data
public class RuleDetailVo {
    private Integer subruleId;

    private String note;

    private String tableComment;

    private String fieldComment;

    private String matchType;

    private String pattern;

    private String matchtableComment;

    private String matchfieldComment;

    private String matchValue;

    public RuleDetailVo(JwRuledetail ruledetail, JwTable table1, JwField field1, JwTable table2,JwField field2){
        this.subruleId = ruledetail.getRuledetailId();
        this.note = ruledetail.getNote();
        if(table1 != null){
            this.tableComment = table1.getDescription();
        }
        else this.tableComment = "";
        if(field1 != null){
            this.fieldComment = field1.getDescription();
        }
        else
            this.fieldComment = "";
        this.matchType = ruledetail.getMatchType();
        this.pattern = ruledetail.getPattern();
        if(table2 != null){
            this.matchtableComment = table2.getDescription();
        }
        else
            this.matchtableComment = "";
        if(field2 != null){
            this.matchfieldComment = field2.getDescription();
        }
        else
            this.matchfieldComment = "";
        this.matchValue = ruledetail.getMatchValue();
    }

}
