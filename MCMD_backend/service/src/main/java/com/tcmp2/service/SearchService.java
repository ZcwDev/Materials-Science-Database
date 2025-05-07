package com.tcmp2.service;

import java.util.Map;
import java.util.Optional;

/**
 * @author : Zhang Chengwei
 * @createDate : 2025-02-18 0:22
 * @description :
 */
public interface SearchService {
    /**
     * 搜索材料
     *
     * @param params
     * @return
     */
    public Map<String, Object> searchMaterials(Map<String, String> params);

    /**
     * 根据材料ID获取材料信息
     *
     * @param id 材料ID
     * @return Map<String, Object>
     */
    Map<String, Object> getMaterialById(String id);
}
