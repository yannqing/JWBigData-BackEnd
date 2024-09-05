package com.wxjw.jwbigdata.vo.RuleVo;

import com.wxjw.jwbigdata.domain.*;
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

    public RuleDetailVo(JwRuledetail ruledetail, NewTable table1, NewColumn field1, NewTable table2, NewColumn field2){
        this.subruleId = ruledetail.getRuledetailId();
        this.note = ruledetail.getNote();
        if(table1 != null){
            this.tableComment = table1.getComment();
        }
        else this.tableComment = "";
        if(field1 != null){
            this.fieldComment = field1.getComment();
        }
        else
            this.fieldComment = "";
        this.matchType = ruledetail.getMatchType();
        this.pattern = ruledetail.getPattern();
        if(table2 != null){
            this.matchtableComment = table2.getComment();
        }
        else
            this.matchtableComment = "";
        if(field2 != null){
            this.matchfieldComment = field2.getComment();
        }
        else
            this.matchfieldComment = "";
        this.matchValue = ruledetail.getMatchValue();
    }

}
