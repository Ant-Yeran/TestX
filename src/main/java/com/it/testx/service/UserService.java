package com.it.testx.service;

import com.it.testx.model.entity.User;
import com.baomidou.mybatisplus.extension.service.IService;
import com.it.testx.model.vo.user.LoginUserVO;

import javax.servlet.http.HttpServletRequest;

/**
* User Service
*/
public interface UserService extends IService<User> {

    /**
     * User register
     *
     * @param account   User account
     * @param password  User password
     * @param checkPassword Check password
     * @return  User ID
     */
    long register(String account, String password, String checkPassword);

    /**
     * User login
     *
     * @param account  User account
     * @param password User password
     * @return  脱敏后的用户信息
     */
    LoginUserVO login(String account, String password);

    /**
     * Get current login user
     *
     * @param request   request
     * @return  Current login user
     */
    User getLoginUser(HttpServletRequest request);

    /**
     * User logout
     *
     * @param request   request
     * @return  success
     */
    boolean logout(HttpServletRequest request);

    /**
     *
     * @param password  User Password
     * @return  EncryptPassword
     */
    String getEncryptPassword(String password);

    /**
     * User Login
     *
     * @param account  User account
     * @param password User password
     * @param request   http request
     * @return 脱敏后的用户信息
     */
    LoginUserVO userLogin(String account, String password, HttpServletRequest request);

    /**
     * 获取脱敏后的用户信息
     * @param user  用户信息
     * @param token token
     * @return  脱敏后的用户信息
     */
    LoginUserVO getLoginUserVO(User user, String token);

    /**
     * 获取脱敏后的用户信息
     * @param user  用户信息
     * @return  脱敏后的用户信息
     */
    LoginUserVO getLoginUserVO(User user);
}
