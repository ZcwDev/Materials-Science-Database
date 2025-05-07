package com.tcmp2.exception;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureException;
import io.jsonwebtoken.UnsupportedJwtException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.HashMap;
import java.util.Map;
/**
 * @author : Zhang Chengwei
 * @createDate : 2025-03-02 19:53
 * @description :全局异常处理器
 * 用于统一处理应用程序中的各种异常，提供友好的错误响应
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    /**
     * 处理通用异常
     */
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public Map<String, String> handleException(Exception e) {
        logger.error("未处理的异常", e);
        Map<String, String> response = new HashMap<>();
        response.put("message", "Internal server error, please try again later");
        return response;
    }

    /**
     * 处理数据库访问异常
     */
    @ExceptionHandler(DataAccessException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public Map<String, String> handleDataAccessException(DataAccessException e) {
        logger.error("数据库访问异常", e);
        Map<String, String> response = new HashMap<>();
        response.put("message", "Database operation failed, please try again later");
        return response;
    }

    /**
     * 处理请求参数缺失异常
     */
    @ExceptionHandler(MissingServletRequestParameterException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, String> handleMissingParams(MissingServletRequestParameterException e) {
        logger.warn("请求参数缺失: {}", e.getMessage());
        Map<String, String> response = new HashMap<>();
        response.put("message", "Missing required parameter: " + e.getParameterName());
        return response;
    }

    /**
     * 处理请求方法不支持异常
     */
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    @ResponseStatus(HttpStatus.METHOD_NOT_ALLOWED)
    public Map<String, String> handleMethodNotSupported(HttpRequestMethodNotSupportedException e) {
        logger.warn("不支持的请求方法: {}", e.getMessage());
        Map<String, String> response = new HashMap<>();
        response.put("message", "Method not supported: " + e.getMethod());
        return response;
    }

    /**
     * 处理请求体解析异常
     */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, String> handleMessageNotReadable(HttpMessageNotReadableException e) {
        logger.warn("请求体解析失败: {}", e.getMessage());
        Map<String, String> response = new HashMap<>();
        response.put("message", "Invalid request format");
        return response;
    }

    /**
     * 处理参数类型不匹配异常
     */
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, String> handleTypeMismatch(MethodArgumentTypeMismatchException e) {
        logger.warn("参数类型不匹配: {}", e.getMessage());
        Map<String, String> response = new HashMap<>();
        response.put("message", "Invalid parameter type: " + e.getName());
        return response;
    }

    /**
     * 处理JWT相关异常
     */
    @ExceptionHandler({
            ExpiredJwtException.class,
            UnsupportedJwtException.class,
            MalformedJwtException.class,
            SignatureException.class,
            IllegalArgumentException.class
    })
    public ResponseEntity<Map<String, String>> handleJwtException(Exception e) {
        logger.warn("JWT验证失败: {}", e.getMessage());
        
        Map<String, String> response = new HashMap<>();
        HttpStatus status = HttpStatus.UNAUTHORIZED;
        
        if (e instanceof ExpiredJwtException) {
            response.put("message", "Login session expired, please login again");
        } else if (e instanceof UnsupportedJwtException) {
            response.put("message", "Unsupported token format");
        } else if (e instanceof MalformedJwtException || e instanceof SignatureException) {
            response.put("message", "Invalid token");
        } else {
            response.put("message", "Token verification failed");
        }
        
        return new ResponseEntity<>(response, status);
    }

    /**
     * 处理自定义业务异常
     */
    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<Map<String, String>> handleBusinessException(BusinessException e) {
        logger.warn("业务异常: {}", e.getMessage());
        Map<String, String> response = new HashMap<>();
        response.put("message", e.getMessage());
        return new ResponseEntity<>(response, e.getStatus());
    }
} 