package com.wxjw.jwbigdata.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import lombok.Data;

/**
 * 
 * @TableName newtable
 */
@TableName(value ="newtable")
@Data
public class NewTable implements Serializable {
    /**
     * 
     */
    @TableId(type = IdType.AUTO)
    private Integer id;

    /**
     * 
     */
    private Integer oldtableId;

    /**
     * 
     */
    private String dbname;

    /**
     * 
     */
    private String tablename;

    /**
     * 
     */
    private String comment;

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
        NewTable other = (NewTable) that;
        return (this.getId() == null ? other.getId() == null : this.getId().equals(other.getId()))
            && (this.getOldtableId() == null ? other.getOldtableId() == null : this.getOldtableId().equals(other.getOldtableId()))
            && (this.getDbname() == null ? other.getDbname() == null : this.getDbname().equals(other.getDbname()))
            && (this.getTablename() == null ? other.getTablename() == null : this.getTablename().equals(other.getTablename()))
            && (this.getComment() == null ? other.getComment() == null : this.getComment().equals(other.getComment()));
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((getId() == null) ? 0 : getId().hashCode());
        result = prime * result + ((getOldtableId() == null) ? 0 : getOldtableId().hashCode());
        result = prime * result + ((getDbname() == null) ? 0 : getDbname().hashCode());
        result = prime * result + ((getTablename() == null) ? 0 : getTablename().hashCode());
        result = prime * result + ((getComment() == null) ? 0 : getComment().hashCode());
        return result;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", id=").append(id);
        sb.append(", oldtableId=").append(oldtableId);
        sb.append(", dbname=").append(dbname);
        sb.append(", tablename=").append(tablename);
        sb.append(", comment=").append(comment);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}