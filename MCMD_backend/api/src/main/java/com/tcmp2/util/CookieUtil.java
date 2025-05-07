package com.tcmp2.util;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
/**
 * @author : Zhang Chengwei
 * @createDate : 2025-03-02 19:53
 * @description :Cookie工具类
 * 用于处理JWT令牌的cookie操作
 */
@Component
public class CookieUtil {

    // Cookie名称
    public static final String JWT_COOKIE_NAME = "jwt_token";
    
    // Cookie有效期（秒）
    private static final int COOKIE_MAX_AGE = 24 * 60 * 60; // 24小时
    
    // Cookie路径
    private static final String COOKIE_PATH = "/";
    
    @Value("${cookie.secure:false}")
    private boolean secureCookie;
    
    @Value("${cookie.domain:}")
    private String cookieDomain;
    
    /**
     * 创建JWT令牌的cookie
     *
     * @param response HTTP响应
     * @param token JWT令牌
     */
    public void createTokenCookie(HttpServletResponse response, String token) {
        // 实现功能代码
    }
    
    /**
     * 从请求中获取JWT令牌
     *
     * @param request HTTP请求
     * @return JWT令牌，如果不存在则返回null
     */
    public String getTokenFromCookie(HttpServletRequest request) {
        // 实现功能代码
        return null;
    }
    
    /**
     * 清除JWT令牌的cookie
     *
     * @param response HTTP响应
     */
    public void clearTokenCookie(HttpServletResponse response) {
        // 实现功能代码
    }
} 