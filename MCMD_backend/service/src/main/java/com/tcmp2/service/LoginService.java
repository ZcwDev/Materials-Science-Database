package com.tcmp2.service;

import java.util.Map;

/**
 * @author : Zhang Chengwei
 * @createDate : 2025-02-18 23:37
 * @description : 登录服务接口
 */
public interface LoginService {
    /**
     * 用户登录
     *
     * @param username 用户名
     * @param password 密码
     * @return 登录结果
     */
    Map<String, String> login(String username, String password);

    /**
     * 用户登出
     *
     * @return 登出结果
     */
    Map<String, String> logout();
    
    /**
     * 验证JWT令牌
     *
     * @param token JWT令牌
     * @return 如果有效，返回用户名；否则返回null
     */
    String validateToken(String token);
    
    /**
     * 刷新JWT令牌
     *
     * @param token 当前JWT令牌
     * @return 刷新结果，包含新令牌
     */
    Map<String, String> refreshToken(String token);
}
