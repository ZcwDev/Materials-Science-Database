package com.tcmp2.controller;

import com.tcmp2.service.CountService;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * @author : Zhang Chengwei
 * @createDate : 2025-02-18 0:15
 * @description :
 */
@RestController
public class CountController {
    @Resource
    private CountService countService;
    /**
     * 获取所有材料的数量
     */
    @GetMapping("/data-count")
    public Map<String, Object> getCount() {
        // 实现功能代码
        return null;
    }
}
