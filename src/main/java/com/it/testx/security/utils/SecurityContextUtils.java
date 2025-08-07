package com.licensed.remitprod.web.security.utils;

import com.licensed.remitprod.core.service.component.auth.JwtService;
import com.licensed.remitprod.core.model.domain.operation.LoginOperator;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;

import javax.servlet.http.HttpServletRequest;


/**
 * Utility class for security context operations
 */
public final class SecurityContextUtils {

    private SecurityContextUtils() {
        throw new UnsupportedOperationException("Utility class");
    }

    /**
     * Establishes security context from validated JWT token
     * @param token Validated JWT token
     * @param request HTTP servlet request
     * @param userDetailsService User details service implementation
     */
    public static void setupSecurityContext(String token,
                                            HttpServletRequest request,
                                            UserDetailsService userDetailsService) {
        // 1. Check existing authentication
        Authentication existingAuth = SecurityContextHolder.getContext().getAuthentication();

        // 2. Reuse if existing principal matches token
        if (isReusableExistingAuth(existingAuth, token)) {
            SecurityContextHolder.getContext().setAuthentication(existingAuth);
            return;
        }

        // 3. Create fresh authentication
        createNewAuth(token, request, userDetailsService);
    }

    /**
     * Verifies if existing authentication can be reused
     * @param auth Current authentication object
     * @param token JWT token to validate against
     * @return true if authentication can be reused
     */
    private static boolean isReusableExistingAuth(Authentication auth, String token) {
        return auth != null &&
                auth.getPrincipal() instanceof LoginOperator &&
                ((LoginOperator) auth.getPrincipal()).getUsername()
                        .equals(JwtService.getUsernameFromToken(token));
    }

    /**
     * Creates new authentication context
     * @param token Valid JWT token
     * @param request HTTP servlet request
     * @param userDetailsService User details service
     */
    private static void createNewAuth(String token,
                                      HttpServletRequest request,
                                      UserDetailsService userDetailsService) {
        UserDetails userDetails = userDetailsService.loadUserByUsername(
                JwtService.getUsernameFromToken(token));

        UsernamePasswordAuthenticationToken newAuth = new UsernamePasswordAuthenticationToken(
                userDetails, null, userDetails.getAuthorities());

        newAuth.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
        SecurityContextHolder.getContext().setAuthentication(newAuth);
    }
}