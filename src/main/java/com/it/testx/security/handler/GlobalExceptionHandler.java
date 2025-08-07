package com.licensed.remitprod.web.security.handler;

import cn.hutool.json.JSONUtil;
import com.licensed.remitprod.common.enums.IorpCommonResultCodeEnum;
import com.licensed.remitprod.common.exception.IorpCommonException;
import com.licensed.remitprod.common.model.result.BaseResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Spring Security 过滤链异常处理类
 *
 * @author yeran
 */
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {
    /**
     * 处理 Security 过滤链的认证异常
     *
     * @param e AuthenticationException 异常对象
     * @return 统一格式的响应结果
     */
    @ExceptionHandler(AuthenticationException.class)
    public BaseResponse<Void> handleAuthenticationException(AuthenticationException e) {
        log.info("Security authentication failed: {}", e.getMessage());
        BaseResponse<Void> response = new BaseResponse<>();
        response.fillResult(IorpCommonResultCodeEnum.NOT_LOGIN_ERROR);
        return response;
    }

    /**
     * 处理 Security 过滤链权限异常
     *
     * @param e AccessDeniedException 异常对象
     * @return 统一格式的响应结果
     */
    @ExceptionHandler(AccessDeniedException.class)
    public BaseResponse<Void> handleAccessDeniedException(AccessDeniedException e) {
        log.info("Security access denied: {}", e.getMessage());
        BaseResponse<Void> response = new BaseResponse<>();
        response.fillResult(IorpCommonResultCodeEnum.PERMISSION_DENIED);
        return response;
    }

    /**
     * 处理自定义业务异常
     *
     * @param e 自定义异常对象
     * @return 统一格式的响应结果
     */
    @ExceptionHandler(IorpCommonException.class)
    public BaseResponse<Void> handleIorpCommonException(IorpCommonException e) {
        log.info("Custom business exception occurred: {}", e.getMessage());
        BaseResponse<Void> response = new BaseResponse<>();
        response.fillResult(e.getResultCode());
        return response;
    }

    /**
     * 处理系统异常
     *
     * @param e 系统异常对象
     * @return 统一格式的响应结果
     */
    @ExceptionHandler(RuntimeException.class)
    public BaseResponse<Void> handleRuntimeException(RuntimeException e) {
        log.info("System exception occurred: {}", e.getMessage());
        BaseResponse<Void> response = new BaseResponse<>();
        response.fillResult(IorpCommonResultCodeEnum.UNKNOWN_EXCEPTION);
        return response;
    }

    public void writeExceptionResponse(HttpServletResponse response, Exception ex) throws IOException {
        BaseResponse<Void> result;
        if (ex instanceof AccessDeniedException) {
            result = handleAccessDeniedException((AccessDeniedException) ex);
        }  else if (ex instanceof AuthenticationException) {
            result = handleAuthenticationException((AuthenticationException) ex);
        } else {
            result = new BaseResponse<>();
            result.fillResult(IorpCommonResultCodeEnum.UNKNOWN_EXCEPTION);
        }
        response.setContentType(MediaType.APPLICATION_JSON_VALUE + ";charset=UTF-8");
        response.setStatus(200);
        response.getWriter().write(JSONUtil.toJsonStr(result));
    }
}


