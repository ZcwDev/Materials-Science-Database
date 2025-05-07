package com.tcmp2.controller;

import com.tcmp2.service.LoginService;
import com.tcmp2.util.CookieUtil;
import jakarta.annotation.Resource;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * @author : Zhang Chengwei
 * @createDate : 2025-02-18 23:36
 * @description : 登录控制器
 */
@RestController
public class LoginController {
    
    private static final Logger logger = LoggerFactory.getLogger(LoginController.class);
    
    @Resource
    private LoginService loginService;
    
    @Resource
    private CookieUtil cookieUtil;
    
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> loginRequest, HttpServletResponse response) {
        // 实现功能代码
        return null;
    }
    
    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpServletResponse response) {
        // 实现功能代码
        return null;
    }
    
    /**
     * 刷新令牌
     */
    @PostMapping("/refresh-token")
    public ResponseEntity<?> refreshToken(HttpServletRequest request, HttpServletResponse response) {
        // 实现功能代码
        return null;
    }
}
