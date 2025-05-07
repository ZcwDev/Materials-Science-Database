package com.tcmp2.service.Impl;

import com.tcmp2.repository.MaterialRepository;
import com.tcmp2.service.CountService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

/**
 * @author : Zhang Chengwei
 * @createDate : 2025-02-18 0:17
 * @description :
 */
@Service
public class CountServiceImpl implements CountService {

    @Resource
    private MaterialRepository materialRepository;
    /**
     * 获取数据库总数
     *
     * @return
     */
    @Override
    public Long getCount() {
        // 实现功能代码
        return null;
    }
}
