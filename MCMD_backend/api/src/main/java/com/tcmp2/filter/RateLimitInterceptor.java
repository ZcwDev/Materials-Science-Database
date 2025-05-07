package com.tcmp2.filter;

import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.io.IOException;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
/**
 * @author : Zhang Chengwei
 * @createDate : 2025-03-02 19:53
 * @description :请求速率限制拦截器
 * 用于防止暴力破解攻击
 */
@Component
public class RateLimitInterceptor implements HandlerInterceptor {
    
    private static final Logger logger = LoggerFactory.getLogger(RateLimitInterceptor.class);
    
    // 存储IP地址及其请求记录的缓存
    private final Map<String, RequestCount> requestCounts = new ConcurrentHashMap<>();
    
    // 配置参数
    private static final int MAX_REQUESTS = 60; // 最大请求数
    private static final long TIME_WINDOW = 60; // 时间窗口（秒）
    private static final long BLOCK_DURATION = 300; // 封禁时长（秒）
    private static final long CLEANUP_INTERVAL = 3600; // 清理间隔（秒）
    
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
    
    @PostConstruct
    public void init() {
        // 启动定时清理任务
        scheduler.scheduleAtFixedRate(this::cleanupExpiredRecords, 
                CLEANUP_INTERVAL, CLEANUP_INTERVAL, TimeUnit.SECONDS);
        logger.info("速率限制拦截器已初始化，清理间隔: {}秒", CLEANUP_INTERVAL);
    }
    
    /**
     * 清理过期的请求记录
     */
    private void cleanupExpiredRecords() {
        long currentTime = System.currentTimeMillis();
        long expirationThreshold = currentTime - TimeUnit.SECONDS.toMillis(TIME_WINDOW + BLOCK_DURATION);
        
        int removedCount = 0;
        Iterator<Map.Entry<String, RequestCount>> iterator = requestCounts.entrySet().iterator();
        
        while (iterator.hasNext()) {
            Map.Entry<String, RequestCount> entry = iterator.next();
            RequestCount count = entry.getValue();
            
            if (count.isExpired(expirationThreshold)) {
                iterator.remove();
                removedCount++;
            }
        }
        
        logger.info("清理过期请求记录完成，移除 {} 条记录", removedCount);
    }
    
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws IOException {
        // 获取客户端IP地址
        String clientIp = getClientIp(request);
        
        // 检查是否是登录请求
        boolean isLoginRequest = request.getRequestURI().equals("/login") && 
                                request.getMethod().equalsIgnoreCase("POST");
        
        // 只对登录请求进行速率限制
        if (!isLoginRequest) {
            return true;
        }
        
        // 获取当前时间
        long currentTime = System.currentTimeMillis();
        
        // 获取或创建请求计数对象
        RequestCount count = requestCounts.computeIfAbsent(clientIp, k -> new RequestCount());
        
        // 检查是否被封禁
        if (count.isBlocked(currentTime)) {
            logger.warn("IP {} 已被封禁，拒绝请求", clientIp);
            response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
            response.setContentType("application/json;charset=UTF-8");
            response.getWriter().write("{\"message\":\"请求过于频繁，请稍后再试\"}");
            return false;
        }
        
        // 检查是否超过速率限制
        if (count.incrementAndCheck(currentTime)) {
            logger.info("IP {} 请求正常", clientIp);
            return true;
        } else {
            // 超过限制，封禁IP
            count.block(currentTime);
            logger.warn("IP {} 请求过于频繁，已被封禁", clientIp);
            response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
            response.setContentType("application/json;charset=UTF-8");
            response.getWriter().write("{\"message\":\"请求过于频繁，请稍后再试\"}");
            return false;
        }
    }
    
    /**
     * 获取客户端真实IP地址
     */
    private String getClientIp(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_CLIENT_IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_X_FORWARDED_FOR");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        // 如果是多级代理，取第一个IP地址
        if (ip != null && ip.contains(",")) {
            ip = ip.split(",")[0].trim();
        }
        return ip;
    }
    
    /**
     * 请求计数类，用于记录IP的请求次数和封禁状态
     */
    private static class RequestCount {
        private int count = 0;
        private long windowStart = System.currentTimeMillis();
        private long blockedUntil = 0;
        
        /**
         * 检查是否被封禁
         */
        public boolean isBlocked(long currentTime) {
            return currentTime < blockedUntil;
        }
        
        /**
         * 增加请求计数并检查是否超过限制
         */
        public synchronized boolean incrementAndCheck(long currentTime) {
            // 检查是否需要重置时间窗口
            if (currentTime - windowStart > TimeUnit.SECONDS.toMillis(TIME_WINDOW)) {
                count = 0;
                windowStart = currentTime;
            }
            
            // 增加计数
            count++;
            
            // 检查是否超过限制
            return count <= MAX_REQUESTS;
        }
        
        /**
         * 封禁IP
         */
        public synchronized void block(long currentTime) {
            blockedUntil = currentTime + TimeUnit.SECONDS.toMillis(BLOCK_DURATION);
        }
        
        /**
         * 检查记录是否已过期
         */
        public boolean isExpired(long expirationThreshold) {
            return windowStart < expirationThreshold && blockedUntil < System.currentTimeMillis();
        }
    }
} 