package com.wxjw.jwbigdata.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * 
 * @TableName jw_ruledetail
 */
@TableName(value ="jw_ruledetail")
@Data
public class JwRuledetail implements Serializable {
    /**
     * 主键
     */
    @TableId(value = "ruledetail_id", type = IdType.AUTO)
    private Integer ruledetailId;

    /**
     * 左表字段id
     */
    @TableField(value = "field_id")
    private Integer fieldId;


    /**
     * 规则类型
     */
    @TableField(value = "match_type")
    private String matchType;

    /**
     * 运算符
     */
    @TableField(value = "pattern")
    private String pattern;

    /**
     * 匹配常量
     */
    @TableField(value = "match_value")
    private String matchValue;

    /**
     * 右表字段id
     */
    @TableField(value = "matchfield_id")
    private Integer matchfieldId;


    /**
     * 备注
     */
    @TableField(value = "note")
    private String note;

    /**
     * 创建时间
     */
    @TableField(value = "create_time")
    private Date createTime;

    /**
     * 外键
     */
    @TableField(value = "rule_id")
    private Integer ruleId;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;

    @Override
    public boolean equals(Object that) {
        if (this == that) {
            return true;
        }
        if (that == null) {
            return false;
        }
        if (getClass() != that.getClass()) {
            return false;
        }
        JwRuledetail other = (JwRuledetail) that;
        return (this.getRuledetailId() == null ? other.getRuledetailId() == null : this.getRuledetailId().equals(other.getRuledetailId()))
            && (this.getFieldId() == null ? other.getFieldId() == null : this.getFieldId().equals(other.getFieldId()))
            && (this.getMatchType() == null ? other.getMatchType() == null : this.getMatchType().equals(other.getMatchType()))
            && (this.getPattern() == null ? other.getPattern() == null : this.getPattern().equals(other.getPattern()))
            && (this.getMatchValue() == null ? other.getMatchValue() == null : this.getMatchValue().equals(other.getMatchValue()))
            && (this.getMatchfieldId() == null ? other.getMatchfieldId() == null : this.getMatchfieldId().equals(other.getMatchfieldId()))
            && (this.getNote() == null ? other.getNote() == null : this.getNote().equals(other.getNote()))
            && (this.getCreateTime() == null ? other.getCreateTime() == null : this.getCreateTime().equals(other.getCreateTime()))
            && (this.getRuleId() == null ? other.getRuleId() == null : this.getRuleId().equals(other.getRuleId()));
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((getRuledetailId() == null) ? 0 : getRuledetailId().hashCode());
        result = prime * result + ((getFieldId() == null) ? 0 : getFieldId().hashCode());
        result = prime * result + ((getMatchType() == null) ? 0 : getMatchType().hashCode());
        result = prime * result + ((getPattern() == null) ? 0 : getPattern().hashCode());
        result = prime * result + ((getMatchValue() == null) ? 0 : getMatchValue().hashCode());
        result = prime * result + ((getMatchfieldId() == null) ? 0 : getMatchfieldId().hashCode());
        result = prime * result + ((getNote() == null) ? 0 : getNote().hashCode());
        result = prime * result + ((getCreateTime() == null) ? 0 : getCreateTime().hashCode());
        result = prime * result + ((getRuleId() == null) ? 0 : getRuleId().hashCode());
        return result;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", ruledetail_id=").append(ruledetailId);
        sb.append(", field_id=").append(fieldId);
        sb.append(", match_type=").append(matchType);
        sb.append(", pattern=").append(pattern);
        sb.append(", match_value=").append(matchValue);
        sb.append(", matchfield_id=").append(matchfieldId);
        sb.append(", note=").append(note);
        sb.append(", create_time=").append(createTime);
        sb.append(", rule_id=").append(ruleId);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}