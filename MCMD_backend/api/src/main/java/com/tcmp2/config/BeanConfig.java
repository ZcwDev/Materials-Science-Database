package com.tcmp2.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

/**
 * Bean配置类
 *
 * @author : Zhang Chengwei
 * @createDate : 2025/4/17
 */
@Configuration
@ComponentScan(basePackages = {"com.tcmp2.service", "com.tcmp2.service.Impl", "com.tcmp2.common.utils", "com.tcmp2.util", "com.tcmp2.filter"})
@EnableMongoRepositories(basePackages = "com.tcmp2.dao")
public class BeanConfig {
    
} 