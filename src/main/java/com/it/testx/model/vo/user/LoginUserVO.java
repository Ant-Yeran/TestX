package com.it.testx.model.vo.user;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;

/**
 * 登录用户视图类
 */
@Data
public class LoginUserVO implements Serializable {
    /**
     * Primary key ID
     */
    private Long id;

    /**
     * User account
     */
    private String userAccount;

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

    @Serial
    private static final long serialVersionUID = -4773256413656418500L;
}