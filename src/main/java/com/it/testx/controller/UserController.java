package com.it.testx.controller;

import com.it.testx.common.BaseResponse;
import com.it.testx.common.ResultUtils;
import com.it.testx.exception.ErrorCode;
import com.it.testx.exception.ThrowUtils;
import com.it.testx.model.dto.user.UserRegisterRequest;
import com.it.testx.service.UserService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@RestController
@RequestMapping("/user")
public class UserController {

    @Resource
    private UserService userService;

    /**
     * 用户注册
     */
    @PostMapping("/register")
    public BaseResponse<Long> register(@RequestBody UserRegisterRequest userRegisterRequest) {
        ThrowUtils.throwIf(userRegisterRequest == null, ErrorCode.PARAMS_ERROR);
        String userAccount = userRegisterRequest.getUserAccount();
        String userPassword = userRegisterRequest.getUserPassword();
        String checkPassword = userRegisterRequest.getCheckPassword();
        long result = userService.register(userAccount, userPassword, checkPassword);
        return ResultUtils.success(result);
    }
}

