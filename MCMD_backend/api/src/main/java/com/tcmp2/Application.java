package com.tcmp2;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

/**
 * @author : Zhang Chengwei
 * @createDate : 2025-02-18 0:00
 * @description :
 */
@SpringBootApplication
@ComponentScan(basePackages = {"com.tcmp2.controller", "com.tcmp2.service", "com.tcmp2.service.Impl", "com.tcmp2.common.utils", "com.tcmp2.util", "com.tcmp2.filter"})
public class Application {
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}