package com.wxjw.jwbigdata.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import lombok.Data;

/**
 * 
 * @TableName jw_table
 */
@TableName(value ="jw_table")
@Data
public class JwTable implements Serializable {
    /**
     * 主键
     */
    @TableId(value = "table_id", type = IdType.AUTO)
    private Integer tableId;

    /**
     * 表名
     */
    @TableField(value = "table_name")
    private String tableName;

    /**
     * 标签
     */
    @TableField(value = "tag")
    private String tag;

    /**
     * 详细描述
     */
    @TableField(value = "description")
    private String description;

    /**
     * 前置表ID,用逗号分隔
     */
    @TableField(value = "forward_table")
    private String forwardTable;

    /**
     * 类型：0 人物；1 单位；2 事件
     */
    @TableField(value = "type")
    private Integer type;

    /**
     * 标记：1 入口；1 非入口
     */
    @TableField(value = "mark")
    private Integer mark;

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
        JwTable other = (JwTable) that;
        return (this.getTableId() == null ? other.getTableId() == null : this.getTableId().equals(other.getTableId()))
            && (this.getTableName() == null ? other.getTableName() == null : this.getTableName().equals(other.getTableName()))
            && (this.getTag() == null ? other.getTag() == null : this.getTag().equals(other.getTag()))
            && (this.getDescription() == null ? other.getDescription() == null : this.getDescription().equals(other.getDescription()))
            && (this.getForwardTable() == null ? other.getForwardTable() == null : this.getForwardTable().equals(other.getForwardTable()))
            && (this.getType() == null ? other.getType() == null : this.getType().equals(other.getType()))
            && (this.getMark() == null ? other.getMark() == null : this.getMark().equals(other.getMark()));
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((getTableId() == null) ? 0 : getTableId().hashCode());
        result = prime * result + ((getTableName() == null) ? 0 : getTableName().hashCode());
        result = prime * result + ((getTag() == null) ? 0 : getTag().hashCode());
        result = prime * result + ((getDescription() == null) ? 0 : getDescription().hashCode());
        result = prime * result + ((getForwardTable() == null) ? 0 : getForwardTable().hashCode());
        result = prime * result + ((getType() == null) ? 0 : getType().hashCode());
        result = prime * result + ((getMark() == null) ? 0 : getMark().hashCode());
        return result;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", table_id=").append(tableId);
        sb.append(", table_name=").append(tableName);
        sb.append(", tag=").append(tag);
        sb.append(", description=").append(description);
        sb.append(", forward_table=").append(forwardTable);
        sb.append(", type=").append(type);
        sb.append(", mark=").append(mark);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}