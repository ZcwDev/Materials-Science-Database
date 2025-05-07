package com.tcmp2.service.Impl;

import com.mongodb.client.MongoCollection;
import com.tcmp2.service.MongoDBTestService;
import jakarta.annotation.Resource;
import org.bson.Document;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;

/**
 * @author : Zhang Chengwei
 * @createDate : 2025-02-18 0:14
 * @description :
 */
@Service
public class MongoDBTestServiceImpl implements MongoDBTestService {

    @Resource
    private MongoTemplate mongoTemplate;

    /**
     * 测试MongoDB连接
     *
     * @return
     */
    @Override
    public String testMongoDBConnection() {
        // 实现功能代码
        return null;
    }
}
