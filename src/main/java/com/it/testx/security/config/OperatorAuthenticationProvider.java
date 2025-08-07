package com.licensed.remitprod.web.security.config;

import com.licensed.remitprod.common.enums.IorpCommonResultCodeEnum;
import com.licensed.remitprod.common.exception.IorpCommonException;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

/**
 * Operator 认证逻辑
 *
 * @author yeran
 */
@Component
public class OperatorAuthenticationProvider implements AuthenticationProvider {
    
    private final UserDetailsService userDetailsService;
    private final PasswordEncoder passwordEncoder;

    public OperatorAuthenticationProvider(UserDetailsService userDetailsService,
                                          PasswordEncoder passwordEncoder) {
        this.userDetailsService = userDetailsService;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public Authentication authenticate(Authentication authentication) 
            throws AuthenticationException {
        String username = authentication.getName();
        String password = authentication.getCredentials().toString();

        UserDetails userDetails;
        try {
            userDetails = userDetailsService.loadUserByUsername(username);
        } catch (Exception e) {
            throw new IorpCommonException(IorpCommonResultCodeEnum.OPERATOR_ACCOUNT_NOT_EXIST);
        }

        if (!passwordEncoder.matches(password, userDetails.getPassword())) {
            throw new IorpCommonException(IorpCommonResultCodeEnum.OPERATOR_PASSWORD_INCORRECT);
        }

        return new UsernamePasswordAuthenticationToken(
            userDetails, null, userDetails.getAuthorities());
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return authentication.equals(UsernamePasswordAuthenticationToken.class);
    }
}