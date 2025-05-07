package com.tcmp2.controller;

import com.tcmp2.exception.BusinessException;
import com.tcmp2.service.SearchService;
import jakarta.annotation.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * @author : Zhang Chengwei
 * @createDate : 2025-02-18 0:22
 * @description : 材料搜索控制器
 */
@RestController
@RequestMapping("/materials")
public class SearchController {
    
    private static final Logger logger = LoggerFactory.getLogger(SearchController.class);
    
    @Resource
    private SearchService searchService;

    @GetMapping("/search")
    public Map<String, Object> searchMaterials(@RequestParam Map<String, String> params) {
        // 实现功能代码
        return null;
    }

    @GetMapping("/{material_id}")
    public ResponseEntity<?> getMaterialById(@PathVariable("material_id") String materialId) {
        // 实现功能代码
        return null;
    }
}
