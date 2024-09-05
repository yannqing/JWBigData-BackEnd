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
 * @TableName modeltask
 */
@TableName(value ="modeltask")
@Data
public class Modeltask implements Serializable {
    /**
     * 
     */
    @TableId(type = IdType.AUTO)
    private Integer id;

    /**
     * 
     */
    private String task;

    /**
     * 
     */
    private String resulttable;

    /**
     * 
     */
    private String resultcomment;

    /**
     * 
     */
    private Date timestamp;

    /**
     * 
     */
    private Integer modelid;

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
        Modeltask other = (Modeltask) that;
        return (this.getId() == null ? other.getId() == null : this.getId().equals(other.getId()))
            && (this.getTask() == null ? other.getTask() == null : this.getTask().equals(other.getTask()))
            && (this.getResulttable() == null ? other.getResulttable() == null : this.getResulttable().equals(other.getResulttable()))
            && (this.getResultcomment() == null ? other.getResultcomment() == null : this.getResultcomment().equals(other.getResultcomment()))
            && (this.getTimestamp() == null ? other.getTimestamp() == null : this.getTimestamp().equals(other.getTimestamp()))
            && (this.getModelid() == null ? other.getModelid() == null : this.getModelid().equals(other.getModelid()));
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((getId() == null) ? 0 : getId().hashCode());
        result = prime * result + ((getTask() == null) ? 0 : getTask().hashCode());
        result = prime * result + ((getResulttable() == null) ? 0 : getResulttable().hashCode());
        result = prime * result + ((getResultcomment() == null) ? 0 : getResultcomment().hashCode());
        result = prime * result + ((getTimestamp() == null) ? 0 : getTimestamp().hashCode());
        result = prime * result + ((getModelid() == null) ? 0 : getModelid().hashCode());
        return result;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", id=").append(id);
        sb.append(", task=").append(task);
        sb.append(", resulttable=").append(resulttable);
        sb.append(", resultcomment=").append(resultcomment);
        sb.append(", timestamp=").append(timestamp);
        sb.append(", modelid=").append(modelid);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}