package com.tcmp2.repository;

import com.tcmp2.pojo.entity.Material;
import org.springframework.data.mongodb.repository.MongoRepository;

/**
 * @author : Zhang Chengwei
 * @createDate : 2025-02-18 0:23
 * @description :
 */
public interface MaterialRepository extends MongoRepository<Material, String> {
}
