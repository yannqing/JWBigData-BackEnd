package com.wxjw.jwbigdata.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import lombok.Data;

/**
 * 
 * @TableName relationofnewtable
 */
@TableName(value ="relationofnewtable")
@Data
public class RelationOfNewtable implements Serializable {
    /**
     * 
     */
    @TableId(type = IdType.AUTO)
    private Integer id;

    /**
     * 
     */
    private Integer newtableId;

    /**
     * 
     */
    private String label;

    /**
     * 
     */
    private String sunlable;

    /**
     * 
     */
    private String preSerial;

    /**
     * 
     */
    private String descript;

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
        RelationOfNewtable other = (RelationOfNewtable) that;
        return (this.getId() == null ? other.getId() == null : this.getId().equals(other.getId()))
            && (this.getNewtableId() == null ? other.getNewtableId() == null : this.getNewtableId().equals(other.getNewtableId()))
            && (this.getLabel() == null ? other.getLabel() == null : this.getLabel().equals(other.getLabel()))
            && (this.getSunlable() == null ? other.getSunlable() == null : this.getSunlable().equals(other.getSunlable()))
            && (this.getPreSerial() == null ? other.getPreSerial() == null : this.getPreSerial().equals(other.getPreSerial()))
            && (this.getDescript() == null ? other.getDescript() == null : this.getDescript().equals(other.getDescript()));
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((getId() == null) ? 0 : getId().hashCode());
        result = prime * result + ((getNewtableId() == null) ? 0 : getNewtableId().hashCode());
        result = prime * result + ((getLabel() == null) ? 0 : getLabel().hashCode());
        result = prime * result + ((getSunlable() == null) ? 0 : getSunlable().hashCode());
        result = prime * result + ((getPreSerial() == null) ? 0 : getPreSerial().hashCode());
        result = prime * result + ((getDescript() == null) ? 0 : getDescript().hashCode());
        return result;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", id=").append(id);
        sb.append(", newtableId=").append(newtableId);
        sb.append(", label=").append(label);
        sb.append(", sunlable=").append(sunlable);
        sb.append(", preSerial=").append(preSerial);
        sb.append(", descript=").append(descript);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}