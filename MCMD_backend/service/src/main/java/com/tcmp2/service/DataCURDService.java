package com.tcmp2.service;

import java.util.Map;

/**
 * @author : Zhang Chengwei
 * @createDate : 2025-02-18 23:58
 * @description : 材料数据CRUD服务接口
 */
public interface DataCURDService {
    /**
     * 根据ID获取材料
     *
     * @param materialId 材料ID
     * @return 材料数据
     */
    Map<String, Object> getMaterialById(String materialId);

    /**
     * 添加材料
     *
     * @param data 材料数据
     * @return 添加结果
     */
    Map<String, Object> addMaterial(Map<String, Object> data);

    /**
     * 更新材料
     *
     * @param materialId 材料ID
     * @param data      更新数据
     * @return 更新结果
     */
    Map<String, Object> updateMaterial(String materialId, Map<String, Object> data);

    /**
     * 删除材料
     *
     * @param materialId 材料ID
     * @return 删除结果
     */
    Map<String, Object> deleteMaterial(String materialId);

    /**
     * 删除所有材料
     *
     * @return 删除结果
     */
    Map<String, Object> deleteAllMaterials();
}
