package com.tcmp2.controller;

import com.tcmp2.exception.BusinessException;
import com.tcmp2.service.DataCURDService;
import jakarta.annotation.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * @author : Zhang Chengwei
 * @createDate : 2025-02-19 00:00
 * @description : 材料数据CRUD控制器
 */
@RestController
public class DataCURDController {

    private static final Logger logger = LoggerFactory.getLogger(DataCURDController.class);

    @Resource
    private DataCURDService dataCURDService;

    @PostMapping("/add_material")
    public ResponseEntity<?> addMaterial(@RequestBody Map<String, Object> data) {
        // 实现功能代码
        return null;
    }

    @PutMapping("/update_material/{materialId}")
    public ResponseEntity<?> updateMaterial(@PathVariable String materialId, @RequestBody Map<String, Object> data) {
        // 实现功能代码
        return null;
    }

    @DeleteMapping("/delete_material/{materialId}")
    public ResponseEntity<?> deleteMaterial(@PathVariable String materialId) {
        // 实现功能代码
        return null;
    }

    @DeleteMapping("/delete_all_materials")
    public ResponseEntity<?> deleteAllMaterials() {
        // 实现功能代码
        return null;
    }
}
