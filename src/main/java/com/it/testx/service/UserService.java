package com.it.testx.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.it.testx.model.dto.admin.UserQueryRequest;
import com.it.testx.model.entity.User;
import com.baomidou.mybatisplus.extension.service.IService;
import com.it.testx.model.vo.admin.UserVO;
import com.it.testx.model.vo.user.LoginUserVO;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

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
     * @param token token
     * @return  Current login user
     */
    User getLoginUser(String token);

    /**
     * User logout
     *
     * @param token token
     * @return  success
     */
    boolean logout(String token);

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

    /**
     * 【管理员】获取脱敏后的用户信息
     * @param user  用户信息
     * @return  脱敏后的用户信息
     */
    UserVO getUserVO(User user);

    /**
     * 【管理员】获取脱敏后的用户信息列表
     * @param userList  用户信息列表
     * @return  脱敏后的用户信息列表
     */
    List<UserVO> getUserVOList(List<User> userList);

    /**
     * 【管理员】用户查询
     * @param userQueryRequest  用户查询请求
     * @return  用户
     */
    QueryWrapper<User> getQueryWrapper(UserQueryRequest userQueryRequest);
}
