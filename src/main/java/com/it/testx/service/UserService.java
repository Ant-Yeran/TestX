package com.it.testx.service;

import com.it.testx.model.entity.User;
import com.baomidou.mybatisplus.extension.service.IService;

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

}
