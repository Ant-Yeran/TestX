package com.it.testx.controller;

import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.it.testx.annotation.AuthCheck;
import com.it.testx.common.BaseResponse;
import com.it.testx.common.DeleteRequest;
import com.it.testx.common.ResultUtils;
import com.it.testx.constant.UserConstant;
import com.it.testx.exception.ErrorCode;
import com.it.testx.exception.ThrowUtils;
import com.it.testx.model.dto.admin.UserQueryRequest;
import com.it.testx.model.dto.user.UserLoginRequest;
import com.it.testx.model.dto.user.UserRegisterRequest;
import com.it.testx.model.entity.User;
import com.it.testx.model.vo.admin.UserVO;
import com.it.testx.model.vo.user.LoginUserVO;
import com.it.testx.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@RequestMapping("/user")
public class UserController {

    @Resource
    private UserService userService;

    /**
     * 用户注册
     */
    @PostMapping("/register")
    public BaseResponse<Long> userRegister(@RequestBody UserRegisterRequest userRegisterRequest) {
        ThrowUtils.throwIf(userRegisterRequest == null, ErrorCode.PARAMS_ERROR);
        String userAccount = userRegisterRequest.getUserAccount();
        String userPassword = userRegisterRequest.getUserPassword();
        String checkPassword = userRegisterRequest.getCheckPassword();
        long result = userService.register(userAccount, userPassword, checkPassword);
        return ResultUtils.success(result);
    }

    /**
     * 用户登录
     */
    @PostMapping("/login")
    public BaseResponse<LoginUserVO> userLogin(@RequestBody UserLoginRequest userLoginRequest, HttpServletRequest request) {
        ThrowUtils.throwIf(userLoginRequest == null, ErrorCode.PARAMS_ERROR);
        String userAccount = userLoginRequest.getUserAccount();
        String userPassword = userLoginRequest.getUserPassword();
        LoginUserVO loginUserVO = userService.login(userAccount, userPassword);
        return ResultUtils.success(loginUserVO);
    }

    /**
     * 获取当前登录用户
     */
    @GetMapping("/get/cur")
    public BaseResponse<LoginUserVO> getLoginUser(@RequestHeader("Authorization") String token) {
        ThrowUtils.throwIf(StringUtils.isBlank(token), ErrorCode.NOT_LOGIN_ERROR);
        User user = userService.getLoginUser(token);
        return ResultUtils.success(userService.getLoginUserVO(user, token));
    }
//    @GetMapping("/get/cur")
//    public BaseResponse<LoginUserVO> getLoginUser(HttpServletRequest request) {
//        User user = userService.getLoginUser(request);
//        String token = request.getHeader("Authorization");
//        return ResultUtils.success(userService.getLoginUserVO(user, token));
//    }


    /**
     * 用户登出
     */
    @PostMapping("/logout")
    public BaseResponse<Boolean> userLogout(@RequestHeader("Authorization") String token) {
        ThrowUtils.throwIf(StringUtils.isBlank(token), ErrorCode.NOT_LOGIN_ERROR);
        boolean result = userService.logout(token);
        return ResultUtils.success(result);
    }


    // Admin

    // Query
    /**
     * 【管理员】根据 id 获取用户
     */
    @GetMapping("/get")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<User> getUserById(long id) {
        ThrowUtils.throwIf(id <= 0, ErrorCode.PARAMS_ERROR);
        User user = userService.getById(id);
        ThrowUtils.throwIf(user == null, ErrorCode.NOT_FOUND_ERROR);
        return ResultUtils.success(user);
    }

    /**
     *【管理员】根据 id 获取包装类
     */
    @GetMapping("/get/vo")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<UserVO> getUserVOById(@RequestHeader("Authorization") String token, long id) {
        BaseResponse<User> response = getUserById(id);
        User user = response.getData();
        return ResultUtils.success(userService.getUserVO(user));
    }

    @PostMapping("/list/page/vo")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Page<UserVO>> getUserList(@RequestBody UserQueryRequest userQueryRequest) {
        // 1. 参数校验
        ThrowUtils.throwIf(userQueryRequest == null, ErrorCode.PARAMS_ERROR);
        // 2. 查询参数获取
        long current = userQueryRequest.getCurrent();
        long pageSize = userQueryRequest.getPageSize();
        Page<User> userPage = userService.page(new Page<>(current, pageSize),
                userService.getQueryWrapper(userQueryRequest));
        Page<UserVO> userVOPage = new Page<>(current, pageSize, userPage.getTotal());
        List<UserVO> userVOList = userService.getUserVOList(userPage.getRecords());
        userVOPage.setRecords(userVOList);
        return ResultUtils.success(userVOPage);
    }

    // Delete
    /**
     * 删除用户
     */
    @PostMapping("/delete")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Boolean> deleteUser(@RequestBody DeleteRequest deleteRequest) {
        ThrowUtils.throwIf(deleteRequest == null || deleteRequest.getId() <= 0, ErrorCode.PARAMS_ERROR);
        boolean removed = userService.removeById(deleteRequest.getId());
        return ResultUtils.success(removed);
    }

}

