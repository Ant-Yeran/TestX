package com.it.testx.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * User information table
 * @TableName user
 */
@TableName(value ="user")
@Data
public class User implements Serializable {
    /**
     * Primary key ID
     */
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    /**
     * User account
     */
    private String userAccount;

    /**
     * User password (encrypted)
     */
    private String userPassword;

    /**
     * User nickname
     */
    private String userName;

    /**
     * User avatar URL
     */
    private String userAvatar;

    /**
     * User profile/bio
     */
    private String userProfile;

    /**
     * User role: user/admin
     */
    private String userRole;

    /**
     * Last edit time
     */
    private Date editTime;

    /**
     * Creation time
     */
    private Date createTime;

    /**
     * Last update time
     */
    private Date updateTime;

    /**
     * Soft delete flag (0=active, 1=deleted)
     */
    @TableLogic
    private Integer isDelete;

    @Serial
    private static final long serialVersionUID = -4773256413656418500L;
}