package com.wxjw.jwbigdata.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 
 * @TableName file_info
 */
@Data
@TableName(value ="file_info")
public class FileInfo implements Serializable {
    /**
     * id
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 父级id
     */
    @TableField(value = "parent_id")
    private Integer parentId;

    /**
     * 名称
     */
    @TableField(value = "file_name")
    private String fileName;

    /**
     * 关联数据库表名
     */
    @TableField(value = "table_name")
    private String tableName;

    /**
     * 是否最末级 0:非最末级；1:最末级
     */
    @TableField(value = "is_end")
    private Integer isEnd;

    /**
     * 创建人
     */
    @TableField(value = "create_by")
    private Integer createBy;

    /**
     * 创建时间
     */
    @TableField(value = "create_time")
    private Date createTime;

    /**
     * 是否可见 0不可见 1可见
     */
    @TableField(value = "status")
    private Integer status;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;

}