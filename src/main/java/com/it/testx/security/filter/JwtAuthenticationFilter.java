package com.licensed.remitprod.web.security.filter;


import com.licensed.remitprod.core.service.component.auth.JwtService;
import com.licensed.remitprod.core.service.component.cache.RedisService;
import com.licensed.remitprod.web.security.config.SecurityProperties;
import com.licensed.remitprod.web.security.utils.SecurityContextUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.annotation.PostConstruct;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

/**
 * JWT 认证过滤器
 *
 * @author yeran
 */
@Component
@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    @Autowired
    private UserDetailsService userDetailsService;

    @Autowired
    private RedisService redisService;

    @Autowired
    private SecurityProperties securityProperties;

    // 预编译的白名单匹配器
    private List<AntPathRequestMatcher> whitelistMatchers;

    @PostConstruct
    public void init() {
        // 预编译正则表达式
        this.whitelistMatchers = securityProperties.getWhitelist().stream()
                .map(AntPathRequestMatcher::new)
                .collect(Collectors.toList());
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain chain) throws IOException, ServletException {
        if (isWhitelistEndpoint(request)) {
            chain.doFilter(request, response);
            return;
        }
        log.debug("JwtAuthenticationFilter 拦截请求: {}", request.getRequestURI());
        try {
            handleToken(request, response, chain);
        } catch (AuthenticationException ex) {
            log.warn("认证失败: URI={}, 原因={}", request.getRequestURI(), ex.getMessage());
            throw ex; // handle by GlobalExceptionHandler
        }
    }

    private void handleToken(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws IOException, ServletException, AuthenticationException {
        String token = JwtService.extractToken(request);
        validateToken(token, request);
        SecurityContextUtils.setupSecurityContext(token, request, userDetailsService);
        chain.doFilter(request, response);
    }

    private void validateToken(String token, HttpServletRequest request) throws AuthenticationException {
        if (token == null) {
            throw new BadCredentialsException("未提供Token");
        }
        try {
            if (!JwtService.validateToken(token, request)) {
                throw new BadCredentialsException("Token无效或已过期");
            }
        } catch (Exception e) {
            throw new BadCredentialsException("Token无效或已过期");
        }
        if (Boolean.FALSE.equals(redisService.hasKey(JwtService.TOKEN_STORE_PREFIX + token))) {
            throw new BadCredentialsException("Token已注销");
        }
    }

    /**
     * 是否是白名单接口
     *
     * @param request http 请求
     * @return 是否是白名单接口
     */
    private boolean isWhitelistEndpoint(HttpServletRequest request) {
        return whitelistMatchers.stream().anyMatch(matcher -> matcher.matches(request));
    }
}
