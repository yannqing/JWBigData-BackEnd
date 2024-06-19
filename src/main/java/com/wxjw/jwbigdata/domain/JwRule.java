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
 * @TableName jw_rule
 */
@TableName(value ="jw_rule")
@Data
public class JwRule implements Serializable {
    /**
     * 主键
     */
    @TableId(value = "rule_id", type = IdType.AUTO)
    private Integer ruleId;

    /**
     * 规则名称
     */
    @TableField(value = "rule_name")
    private String ruleName;

    /**
     * 描述
     */
    @TableField(value = "description")
    private String description;

    /**
     * SQL语句
     */
    @TableField(value = "sql_statement")
    private String sqlStatement;

    /**
     * 备注
     */
    @TableField(value = "note")
    private String note;

    /**
     * 结果表名称
     */
    @TableField(value = "result_table")
    private String resultTable;

    /**
     * 创建者
     */
    @TableField(value = "create_by")
    private Integer createBy;

    /**
     * 创建时间
     */
    @TableField(value = "create_time")
    private Date createTime;

    /**
     * 0未启用 1已启用
     */
    @TableField(value = "is_on")
    private Integer isOn;

    /**
     * 0私有 1公有
     */
    @TableField(value = "status")
    private Integer status;

    /**
     * 0人相关 1单位相关
     */
    @TableField(value = "type")
    private Integer type;

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
        JwRule other = (JwRule) that;
        return (this.getRuleId() == null ? other.getRuleId() == null : this.getRuleId().equals(other.getRuleId()))
            && (this.getRuleName() == null ? other.getRuleName() == null : this.getRuleName().equals(other.getRuleName()))
            && (this.getDescription() == null ? other.getDescription() == null : this.getDescription().equals(other.getDescription()))
            && (this.getSqlStatement() == null ? other.getSqlStatement() == null : this.getSqlStatement().equals(other.getSqlStatement()))
            && (this.getNote() == null ? other.getNote() == null : this.getNote().equals(other.getNote()))
            && (this.getResultTable() == null ? other.getResultTable() == null : this.getResultTable().equals(other.getResultTable()))
            && (this.getCreateBy() == null ? other.getCreateBy() == null : this.getCreateBy().equals(other.getCreateBy()))
            && (this.getCreateTime() == null ? other.getCreateTime() == null : this.getCreateTime().equals(other.getCreateTime()))
            && (this.getStatus() == null ? other.getStatus() == null : this.getStatus().equals(other.getStatus()))
            && (this.getIsOn() == null ? other.getIsOn() == null : this.getIsOn().equals(other.getIsOn()))
            && (this.getType() == null ? other.getType() == null : this.getType().equals(other.getType()));
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((getRuleId() == null) ? 0 : getRuleId().hashCode());
        result = prime * result + ((getRuleName() == null) ? 0 : getRuleName().hashCode());
        result = prime * result + ((getDescription() == null) ? 0 : getDescription().hashCode());
        result = prime * result + ((getSqlStatement() == null) ? 0 : getSqlStatement().hashCode());
        result = prime * result + ((getNote() == null) ? 0 : getNote().hashCode());
        result = prime * result + ((getResultTable() == null) ? 0 : getResultTable().hashCode());
        result = prime * result + ((getCreateBy() == null) ? 0 : getCreateBy().hashCode());
        result = prime * result + ((getCreateTime() == null) ? 0 : getCreateTime().hashCode());
        result = prime * result + ((getStatus() == null) ? 0 : getStatus().hashCode());
        result = prime * result + ((getIsOn() == null) ? 0 : getIsOn().hashCode());
        result = prime * result + ((getType() == null) ? 0 : getType().hashCode());
        return result;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", rule_id=").append(ruleId);
        sb.append(", rule_name=").append(ruleName);
        sb.append(", description=").append(description);
        sb.append(", sql_statement=").append(sqlStatement);
        sb.append(", note=").append(note);
        sb.append(", result_table=").append(resultTable);
        sb.append(", create_by=").append(createBy);
        sb.append(", create_time=").append(createTime);
        sb.append(", status=").append(status);
        sb.append(", is_on=").append(isOn);
        sb.append(", type=").append(type);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}