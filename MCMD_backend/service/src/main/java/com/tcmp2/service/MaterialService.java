package com.tcmp2.service;

import com.tcmp2.pojo.entity.Material;

/**
 * 材料服务接口
 *
 * @author : Zhang Chengwei
 * @createDate : 2025/4/17
 */
public interface MaterialService {
    
    /**
     * 根据ID获取材料
     * @param id 材料ID
     * @return 材料实体
     */
    Material getMaterialById(String id);
    
    /**
     * 更新材料信息
     * @param material 材料实体
     * @return 更新后的材料实体
     */
    Material updateMaterial(Material material);
} 