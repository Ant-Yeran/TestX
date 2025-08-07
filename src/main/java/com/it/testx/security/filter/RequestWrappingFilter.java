package com.licensed.remitprod.web.security.filter;

import com.licensed.remitprod.common.healthcheck.HealthCheckMeta;
import com.licensed.remitprod.common.utils.TraceUtils;
import com.licensed.remitprod.web.controller.healthcheck.HealthController;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.ContentCachingRequestWrapper;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 请求包装过滤器，保证 request body 可重复读取
 *
 * @author yeran
 */
@Component
public class RequestWrappingFilter extends OncePerRequestFilter {
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {
        try {
            TraceUtils.createTaskTraceId();
            this.increaseHttpRequestCount(request);
            // 包装请求，保证 request body 可重复读取
            ContentCachingRequestWrapper wrappedRequest = new ContentCachingRequestWrapper(request);
            chain.doFilter(wrappedRequest, response);
        } finally {
            this.decreaseHttpRequestCount(request);
            TraceUtils.clearTriceId();
        }
    }

    private void increaseHttpRequestCount(HttpServletRequest request) {
        String uri = request.getRequestURI();
        if (!uri.endsWith(HealthController.DEPLOY_URI)) {
            HealthCheckMeta.httpRequestCount.incrementAndGet();
        }
    }

    private void decreaseHttpRequestCount(HttpServletRequest request) {
        String uri = request.getRequestURI();
        if (!uri.endsWith(HealthController.DEPLOY_URI)) {
            HealthCheckMeta.httpRequestCount.decrementAndGet();
        }
    }
}