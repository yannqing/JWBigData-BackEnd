package com.wxjw.jwbigdata.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import lombok.Data;

/**
 * 上传表字段参数
 * @TableName uploadcolumn
 */
@TableName(value ="uploadcolumn")
@Data
public class Uploadcolumn implements Serializable {
    /**
     * 
     */
    @TableId(type = IdType.AUTO)
    private Integer id;

    /**
     * 上传表id
     */
    private Integer uploadtableId;

    /**
     * 上传表字段
     */
    private String columnname;

    /**
     * 上传表字段类型
     */
    private String datatype;

    /**
     * 上传表字段注释
     */
    private String comment;

    /**
     * 备注和描述
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
        Uploadcolumn other = (Uploadcolumn) that;
        return (this.getId() == null ? other.getId() == null : this.getId().equals(other.getId()))
            && (this.getUploadtableId() == null ? other.getUploadtableId() == null : this.getUploadtableId().equals(other.getUploadtableId()))
            && (this.getColumnname() == null ? other.getColumnname() == null : this.getColumnname().equals(other.getColumnname()))
            && (this.getDatatype() == null ? other.getDatatype() == null : this.getDatatype().equals(other.getDatatype()))
            && (this.getComment() == null ? other.getComment() == null : this.getComment().equals(other.getComment()))
            && (this.getDescript() == null ? other.getDescript() == null : this.getDescript().equals(other.getDescript()));
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((getId() == null) ? 0 : getId().hashCode());
        result = prime * result + ((getUploadtableId() == null) ? 0 : getUploadtableId().hashCode());
        result = prime * result + ((getColumnname() == null) ? 0 : getColumnname().hashCode());
        result = prime * result + ((getDatatype() == null) ? 0 : getDatatype().hashCode());
        result = prime * result + ((getComment() == null) ? 0 : getComment().hashCode());
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
        sb.append(", uploadtableId=").append(uploadtableId);
        sb.append(", columnname=").append(columnname);
        sb.append(", datatype=").append(datatype);
        sb.append(", comment=").append(comment);
        sb.append(", descript=").append(descript);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}