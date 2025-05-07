package com.tcmp2.controller;

import com.tcmp2.service.MongoDBTestService;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author : Zhang Chengwei
 * @createDate : 2025-02-18 0:04
 * @description :
 */
@RestController
public class TestMongoDBController {

    @Resource
    private MongoDBTestService mongoDBTestService;

    /**
     * 测试 MongoDB 连接
     */
    @GetMapping("/test_mongodb_connection")
    public String testMongoDBConnection() {
        // 实现功能代码
        return null;
    }
}
