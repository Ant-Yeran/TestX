package com.licensed.remitprod.web.security.config;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Base64;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.core.GrantedAuthorityDefaults;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.access.channel.ChannelProcessingFilter;
import org.springframework.security.web.access.expression.DefaultWebSecurityExpressionHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import com.licensed.remitprod.common.config.KeysConfigSets;
import com.licensed.remitprod.core.service.component.kms.KmsService;
import com.licensed.remitprod.web.security.filter.JwtAuthenticationFilter;
import com.licensed.remitprod.web.security.filter.RequestWrappingFilter;
import com.licensed.remitprod.web.security.handler.AccessDeniedHandler;
import com.licensed.remitprod.web.security.handler.AuthenticationHandler;

import lombok.extern.slf4j.Slf4j;

/**
 * Security 主配置类
 *
 * @author yeran
 */
@Slf4j
@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    private SecurityProperties securityProperties;

    @Autowired
    private KmsService kmsService;

    @Autowired
    private KeysConfigSets keysConfigSets;

    @Autowired
    private OperatorAuthenticationProvider operatorAuthenticationProvider;

    @Autowired
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @Autowired
    private RequestWrappingFilter requestWrappingFilter;

    @Autowired
    private AuthenticationHandler authenticationHandler;

    @Autowired
    private AccessDeniedHandler accessDeniedHandler;

    /**
     * 配置密码编码器
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        // 前端加密，后端解密校验
        return new PasswordEncoder() {
            @Override
            public String encode(CharSequence rawPassword) {
                return rawPassword.toString(); // 直接返回前端加密密文
            }

            @Override
            public boolean matches(CharSequence rawPassword, String encodedPassword) {
                if (rawPassword == null || encodedPassword == null) {
                    return false;
                }

                try {
                    byte[] rawBytes = Base64.getDecoder().decode(rawPassword.toString());
                    byte[] encodedBytes = Base64.getDecoder().decode(encodedPassword);

//                    if (rawBytes.length != 256 || encodedBytes.length != 256) {
//                        log.warn("Invalid ciphertext length (raw: {}, encoded: {})",
//                                rawBytes.length, encodedBytes.length);
//                        return false;
//                    }

                    String decryptedRaw = kmsService.decryptAsymmetric(keysConfigSets.getOperationLoginKey(), rawBytes);
                    String decryptedEncoded = kmsService.decryptAsymmetric(keysConfigSets.getOperationLoginKey(), encodedBytes);
                    return MessageDigest.isEqual(
                            decryptedRaw.getBytes(StandardCharsets.UTF_8),
                            decryptedEncoded.getBytes(StandardCharsets.UTF_8)
                    );
                } catch (IllegalArgumentException e) {
                    log.warn("Base64 decode failed", e);
                    return false;
                } catch (Exception e) {
                    log.error("Password match failed", e);
                    return false;
                }
            }
        };
    }

    /**
     * 自定义权限校验器
     */
    @Bean
    public MultiPermissionEvaluator multiPermissionEvaluator() {
        return new MultiPermissionEvaluator();
    }

    /**
     * 移除默认的 "ROLE_" 前缀
     */
    @Bean
    static GrantedAuthorityDefaults grantedAuthorityDefaults() {
        return new GrantedAuthorityDefaults("");
    }

    /**
     * 暴露 AuthenticationManager 为 Bean
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    /**
     * CORS 过滤器配置，使用 SecurityProperties 中的 CORS 配置
     */
    @Bean
    public CorsFilter corsFilter() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowCredentials(securityProperties.getCors().isAllowCredentials());
        config.addAllowedOriginPattern(securityProperties.getCors().getAllowedOrigins());
        config.addAllowedHeader(securityProperties.getCors().getAllowedHeaders());
        config.addAllowedMethod(securityProperties.getCors().getAllowedMethods());
        source.registerCorsConfiguration(securityProperties.getCors().getPathPattern(), config);
        return new CorsFilter(source);
    }

    /**
     * 认证逻辑配置
     */
    @Override
    protected void configure(AuthenticationManagerBuilder auth) {
        auth.authenticationProvider(operatorAuthenticationProvider);
    }

    /**
     * 主安全配置
     */
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        // 创建带自定义权限评估器的表达式处理器
        DefaultWebSecurityExpressionHandler expressionHandler = new DefaultWebSecurityExpressionHandler();
        expressionHandler.setPermissionEvaluator(multiPermissionEvaluator());

        http
                // Disable CSRF protection
                .csrf().disable()

                // Configure stateless session management
                .sessionManagement()
                    .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()

                // Configure exception handling
                .exceptionHandling()
                    .authenticationEntryPoint(authenticationHandler)
                    .accessDeniedHandler(accessDeniedHandler)
                .and()

                // Configure authorization rules
                .authorizeRequests()
                    .expressionHandler(expressionHandler) // 正确设置表达式处理器
                    .antMatchers(securityProperties.getWhitelist().toArray(new String[0])).permitAll()
                    .anyRequest().authenticated()
                .and()

                // Add filters in specific order:
                .addFilterBefore(corsFilter(), ChannelProcessingFilter.class) // CORS filter
                .addFilterBefore(requestWrappingFilter, UsernamePasswordAuthenticationFilter.class)
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class); // JWT auth before default auth
    }
}
