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
 * @TableName user
 */
@Data
@TableName(value ="user")
public class User implements Serializable {
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    /**
     * 用户id
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 账户
     */
    @TableField(value = "user_account")
    private String userAccount;

    /**
     * 密码
     */
    @TableField(value = "password")
    private String password;

    /**
     * 姓名
     */
    @TableField(value = "username")
    private String username;

    /**
     * 部门id
     */
    @TableField(value = "department_id")
    private Integer departmentId;

    /**
     * 职务
     */
    @TableField(value = "position")
    private String position;

    /**
     * 角色：1管理员，0普通用户（默认创建为普通用户）
     */
    @TableField(value = "role")
    private Integer role;

    /**
     * 精准画像系统：0无权限，1有权限（默认无权限）
     */
    @TableField(value = "portrait")
    private Integer portrait;

    /**
     * 文件比对系统：0无权限，1有权限（默认无权限）
     */
    @TableField(value = "compare")
    private Integer compare;

    /**
     * 规则模型系统：0无权限，1有权限（默认无权限）
     */
    @TableField(value = "model")
    private Integer model;

    /**
     * 智能检索系统：0无权限，1有权限（默认无权限）
     */
    @TableField(value = "search")
    private Integer search;

    /**
     * 创建者id
     */
    @TableField(value = "created_user")
    private Integer createdUser;

    /**
     * 创建时间
     */
    @TableField(value = "created_time")
    private Date createdTime;

    /**
     * 是否删除：0未删除
     */
    @TableField(value = "is_delete")
    private Integer isDelete;

    /**
     * 电话
     */
    @TableField(value = "phone")
    private String phone;

    /**
     * 状态
     */
    @TableField(value = "status")
    private Integer status;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;

}