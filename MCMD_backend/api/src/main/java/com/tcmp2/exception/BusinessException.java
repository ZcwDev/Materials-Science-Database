package com.tcmp2.exception;

import org.springframework.http.HttpStatus;
/**
 * @author : Zhang Chengwei
 * @createDate : 2025-03-02 19:53
 * @description :业务异常类
 * 用于表示业务逻辑中的异常情况
 */
public class BusinessException extends RuntimeException {
    
    private final HttpStatus status;
    
    /**
     * 创建业务异常
     * 
     * @param message 异常消息
     * @param status HTTP状态码
     */
    public BusinessException(String message, HttpStatus status) {
        super(message);
        this.status = status;
    }
    
    /**
     * 创建业务异常，默认状态码为BAD_REQUEST
     * 
     * @param message 异常消息
     */
    public BusinessException(String message) {
        this(message, HttpStatus.BAD_REQUEST);
    }
    
    /**
     * 获取HTTP状态码
     * 
     * @return HTTP状态码
     */
    public HttpStatus getStatus() {
        return status;
    }
} 