package com.it.testx.controller;

import com.it.testx.annotation.AuthCheck;
import com.it.testx.common.BaseResponse;
import com.it.testx.common.ResultUtils;
import com.it.testx.constant.UserConstant;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/")
public class MainController {

    /**
     * 健康检查
     */
//    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    @GetMapping("/health")
    public BaseResponse<String> health() {
        return ResultUtils.success("ok");
    }
}

