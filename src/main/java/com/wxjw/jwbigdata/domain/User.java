package com.yannqing.dockerdesktop.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 
 * @TableName yan_user
 */
@TableName(value ="yan_user")
@Data
public class User implements Serializable {
    /**
     * 用户id
     */
    @TableId(value = "user_id", type = IdType.AUTO)
    private Integer user_id;

    /**
     * 用户名
     */
    @TableField(value = "username")
    private String username;

    /**
     * 密码
     */
    @TableField(value = "password")
    private String password;

    /**
     * 地址
     */
    @TableField(value = "address")
    private String address;

    /**
     * 电话
     */
    @TableField(value = "phone")
    private String phone;

    /**
     * 邮箱
     */
    @TableField(value = "email")
    private String email;

    /**
     * 年龄
     */
    @TableField(value = "age")
    private Integer age;

    /**
     * 签名
     */
    @TableField(value = "signature")
    private String signature;

    /**
     * 性别
     */
    @TableField(value = "sex")
    private String sex;

    /**
     * 头像
     */
    @TableField(value = "avatar")
    private String avatar;

    /**
     * 
     */
    @TableField(value = "disk_size")
    private Integer disk_size;

    /**
     * 
     */
    @TableField(value = "internet")
    private Integer internet;

    /**
     * 昵称
     */
    @TableField(value = "nick_name")
    private String nick_name;

    /**
     * 账户是否可用
     */
    @TableField(value = "enabled")
    private Integer enabled;

    /**
     * 账户是否过期
     */
    @TableField(value = "account_no_expired")
    private Integer account_no_expired;

    /**
     * 密码是否过期
     */
    @TableField(value = "credentials_no_expired")
    private Integer credentials_no_expired;

    /**
     * 是否被锁定
     */
    @TableField(value = "account_no_locked")
    private Integer account_no_locked;

    /**
     * 创建时间
     */
    @TableField(value = "create_time")
    private Date create_time;

    /**
     * 备注
     */
    @TableField(value = "description")
    private String description;

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
        User other = (User) that;
        return (this.getUser_id() == null ? other.getUser_id() == null : this.getUser_id().equals(other.getUser_id()))
            && (this.getUsername() == null ? other.getUsername() == null : this.getUsername().equals(other.getUsername()))
            && (this.getPassword() == null ? other.getPassword() == null : this.getPassword().equals(other.getPassword()))
            && (this.getAddress() == null ? other.getAddress() == null : this.getAddress().equals(other.getAddress()))
            && (this.getPhone() == null ? other.getPhone() == null : this.getPhone().equals(other.getPhone()))
            && (this.getEmail() == null ? other.getEmail() == null : this.getEmail().equals(other.getEmail()))
            && (this.getAge() == null ? other.getAge() == null : this.getAge().equals(other.getAge()))
            && (this.getSignature() == null ? other.getSignature() == null : this.getSignature().equals(other.getSignature()))
            && (this.getSex() == null ? other.getSex() == null : this.getSex().equals(other.getSex()))
            && (this.getAvatar() == null ? other.getAvatar() == null : this.getAvatar().equals(other.getAvatar()))
            && (this.getDisk_size() == null ? other.getDisk_size() == null : this.getDisk_size().equals(other.getDisk_size()))
            && (this.getInternet() == null ? other.getInternet() == null : this.getInternet().equals(other.getInternet()))
            && (this.getNick_name() == null ? other.getNick_name() == null : this.getNick_name().equals(other.getNick_name()))
            && (this.getEnabled() == null ? other.getEnabled() == null : this.getEnabled().equals(other.getEnabled()))
            && (this.getAccount_no_expired() == null ? other.getAccount_no_expired() == null : this.getAccount_no_expired().equals(other.getAccount_no_expired()))
            && (this.getCredentials_no_expired() == null ? other.getCredentials_no_expired() == null : this.getCredentials_no_expired().equals(other.getCredentials_no_expired()))
            && (this.getAccount_no_locked() == null ? other.getAccount_no_locked() == null : this.getAccount_no_locked().equals(other.getAccount_no_locked()))
            && (this.getCreate_time() == null ? other.getCreate_time() == null : this.getCreate_time().equals(other.getCreate_time()))
            && (this.getDescription() == null ? other.getDescription() == null : this.getDescription().equals(other.getDescription()));
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((getUser_id() == null) ? 0 : getUser_id().hashCode());
        result = prime * result + ((getUsername() == null) ? 0 : getUsername().hashCode());
        result = prime * result + ((getPassword() == null) ? 0 : getPassword().hashCode());
        result = prime * result + ((getAddress() == null) ? 0 : getAddress().hashCode());
        result = prime * result + ((getPhone() == null) ? 0 : getPhone().hashCode());
        result = prime * result + ((getEmail() == null) ? 0 : getEmail().hashCode());
        result = prime * result + ((getAge() == null) ? 0 : getAge().hashCode());
        result = prime * result + ((getSignature() == null) ? 0 : getSignature().hashCode());
        result = prime * result + ((getSex() == null) ? 0 : getSex().hashCode());
        result = prime * result + ((getAvatar() == null) ? 0 : getAvatar().hashCode());
        result = prime * result + ((getDisk_size() == null) ? 0 : getDisk_size().hashCode());
        result = prime * result + ((getInternet() == null) ? 0 : getInternet().hashCode());
        result = prime * result + ((getNick_name() == null) ? 0 : getNick_name().hashCode());
        result = prime * result + ((getEnabled() == null) ? 0 : getEnabled().hashCode());
        result = prime * result + ((getAccount_no_expired() == null) ? 0 : getAccount_no_expired().hashCode());
        result = prime * result + ((getCredentials_no_expired() == null) ? 0 : getCredentials_no_expired().hashCode());
        result = prime * result + ((getAccount_no_locked() == null) ? 0 : getAccount_no_locked().hashCode());
        result = prime * result + ((getCreate_time() == null) ? 0 : getCreate_time().hashCode());
        result = prime * result + ((getDescription() == null) ? 0 : getDescription().hashCode());
        return result;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", user_id=").append(user_id);
        sb.append(", username=").append(username);
        sb.append(", password=").append(password);
        sb.append(", address=").append(address);
        sb.append(", phone=").append(phone);
        sb.append(", email=").append(email);
        sb.append(", age=").append(age);
        sb.append(", signature=").append(signature);
        sb.append(", sex=").append(sex);
        sb.append(", avatar=").append(avatar);
        sb.append(", disk_size=").append(disk_size);
        sb.append(", internet=").append(internet);
        sb.append(", nick_name=").append(nick_name);
        sb.append(", enabled=").append(enabled);
        sb.append(", account_no_expired=").append(account_no_expired);
        sb.append(", credentials_no_expired=").append(credentials_no_expired);
        sb.append(", account_no_locked=").append(account_no_locked);
        sb.append(", create_time=").append(create_time);
        sb.append(", description=").append(description);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}