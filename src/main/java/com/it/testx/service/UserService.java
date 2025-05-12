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
     * @param account   User account
     * @param password  User password
     * @param checkPassword Check password
     * @return  User ID
     */
    long register(String account, String password, String checkPassword);


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
     * @return  脱敏后的用户信息
     */
    LoginUserVO getLoginUserVO(User user);
}
