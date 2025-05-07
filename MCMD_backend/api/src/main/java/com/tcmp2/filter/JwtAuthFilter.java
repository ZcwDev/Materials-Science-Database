package com.tcmp2.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tcmp2.service.LoginService;
import com.tcmp2.util.CookieUtil;
import jakarta.annotation.Resource;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
/**
 * @author : Zhang Chengwei
 * @createDate : 2025-03-02 19:53
 * @description :JWT认证过滤器
 * 用于验证请求中的JWT令牌，保护API端点
 */
@Component
public class JwtAuthFilter extends OncePerRequestFilter {

    private static final Logger logger = LoggerFactory.getLogger(JwtAuthFilter.class);

    @Resource
    private LoginService loginService;
    
    @Resource
    private CookieUtil cookieUtil;
    
    private final ObjectMapper objectMapper = new ObjectMapper();
    
    // 不需要验证的路径
    private static final List<String> EXCLUDED_PATHS = Arrays.asList(
        "/login", "/logout", "/about", "/refresh-token"
    );

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) 
            throws ServletException, IOException {
        
        // 处理OPTIONS预检请求
        if (request.getMethod().equals("OPTIONS")) {
            response.setStatus(HttpServletResponse.SC_OK);
            return;
        }
        
        String path = request.getRequestURI();
        
        // 如果是排除的路径，直接放行
        if (isExcludedPath(path)) {
            filterChain.doFilter(request, response);
            return;
        }
        
        try {
            // 首先尝试从cookie中获取令牌
            String token = cookieUtil.getTokenFromCookie(request);
            
            // 如果cookie中没有令牌，尝试从Authorization头中获取
            if (token == null) {
                String authHeader = request.getHeader("Authorization");
                if (authHeader != null && authHeader.startsWith("Bearer ")) {
                    token = authHeader.substring(7);
                }
            }
            
            // 如果没有找到令牌，返回未授权错误
            if (token == null) {
                logger.warn("请求缺少有效的JWT令牌: {}", path);
                handleAuthError(response, "未授权访问，请先登录", HttpStatus.UNAUTHORIZED);
                return;
            }
            
            // 验证令牌
            String username = loginService.validateToken(token);
            
            if (username == null) {
                logger.warn("无效的JWT令牌: {}", path);
                handleAuthError(response, "无效的令牌，请重新登录", HttpStatus.UNAUTHORIZED);
                return;
            }
            
            // 令牌有效，放行请求
            filterChain.doFilter(request, response);
            
        } catch (Exception e) {
            logger.error("JWT认证过程中发生异常", e);
            handleAuthError(response, "认证过程中发生错误，请重新登录", HttpStatus.UNAUTHORIZED);
        }
    }
    
    /**
     * 处理认证错误
     */
    private void handleAuthError(HttpServletResponse response, String message, HttpStatus status) throws IOException {
        response.setStatus(status.value());
        response.setContentType("application/json;charset=UTF-8");
        
        Map<String, String> errorResponse = new HashMap<>();
        errorResponse.put("message", message);
        
        response.getWriter().write(objectMapper.writeValueAsString(errorResponse));
    }
    
    private boolean isExcludedPath(String path) {
        return EXCLUDED_PATHS.stream().anyMatch(path::startsWith);
    }
} 