package com.licensed.remitprod.web.security.handler;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Spring Security 认证异常处理类
 *
 * @author yeran
 */
@Component
@RequiredArgsConstructor
public class AuthenticationHandler implements AuthenticationEntryPoint {

    private final GlobalExceptionHandler globalExceptionHandler;

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response,
                         AuthenticationException authException) throws IOException {
        globalExceptionHandler.writeExceptionResponse(response, authException);
    }
}