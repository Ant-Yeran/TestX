package com.it.testx.constant;

/**
 * 用户模块常量
 */
public interface UserConstant {

    /**
     * 用户登录态键
     */
    String USER_LOGIN_STATE = "user_login";

    /**
     * 用户登录 Token 前缀（格式：login:token:{token}）
     */
    String LOGIN_TOKEN_PREFIX = "login:token:";

    /**
     * 登录 Token 默认过期时间（分钟）
     */
    long LOGIN_TOKEN_EXPIRE = 30L;


    //  region 权限

    /**
     * 默认角色
     */
    String DEFAULT_ROLE = "user";

    /**
     * 管理员角色
     */
    String ADMIN_ROLE = "admin";
    
    // endregion
}
