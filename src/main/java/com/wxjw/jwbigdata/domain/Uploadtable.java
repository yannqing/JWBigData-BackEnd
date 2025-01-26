package com.wxjw.jwbigdata.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import lombok.Data;

/**
 * 上传表参数
 * @TableName uploadtable
 */
@TableName(value ="uploadtable")
@Data
public class Uploadtable implements Serializable {
    /**
     * 
     */
    @TableId(type = IdType.AUTO)
    private Integer id;

    /**
     * 数据来源
     */
    private String source;

    /**
     * 旧表所在数据库名称
     */
    private String dbname;

    /**
     * 上传表名称
     */
    private String tablename;

    /**
     * 上传表注释
     */
    private String comment;

    /**
     * 该表是否已经处理并且放到cleandb库，登记完表参数，设置为”否1，设置完来源，设置为“否2”
     */
    private String done;

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
        Uploadtable other = (Uploadtable) that;
        return (this.getId() == null ? other.getId() == null : this.getId().equals(other.getId()))
            && (this.getSource() == null ? other.getSource() == null : this.getSource().equals(other.getSource()))
            && (this.getDbname() == null ? other.getDbname() == null : this.getDbname().equals(other.getDbname()))
            && (this.getTablename() == null ? other.getTablename() == null : this.getTablename().equals(other.getTablename()))
            && (this.getComment() == null ? other.getComment() == null : this.getComment().equals(other.getComment()))
            && (this.getDone() == null ? other.getDone() == null : this.getDone().equals(other.getDone()))
            && (this.getDescript() == null ? other.getDescript() == null : this.getDescript().equals(other.getDescript()));
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((getId() == null) ? 0 : getId().hashCode());
        result = prime * result + ((getSource() == null) ? 0 : getSource().hashCode());
        result = prime * result + ((getDbname() == null) ? 0 : getDbname().hashCode());
        result = prime * result + ((getTablename() == null) ? 0 : getTablename().hashCode());
        result = prime * result + ((getComment() == null) ? 0 : getComment().hashCode());
        result = prime * result + ((getDone() == null) ? 0 : getDone().hashCode());
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
        sb.append(", source=").append(source);
        sb.append(", dbname=").append(dbname);
        sb.append(", tablename=").append(tablename);
        sb.append(", comment=").append(comment);
        sb.append(", done=").append(done);
        sb.append(", descript=").append(descript);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}