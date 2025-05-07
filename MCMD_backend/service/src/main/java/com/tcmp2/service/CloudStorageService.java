package com.tcmp2.service;

import com.tcmp2.pojo.entity.Material;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;

/**
 * 云存储服务接口
 * 处理各种数据的上传和存储到腾讯云COS
 *
 * @author : Zhang Chengwei
 * @createDate : 2025/4/17
 */
public interface CloudStorageService {
    
    /**
     * 上传文件到腾讯云COS
     * 
     * @param inputStream 输入流
     * @param fileType 文件类型(如"xrd", "cif", "image"等)
     * @param category 文件分类(如"cuKa", "cuKa1", "cuKa2", "thumbnail"等)
     * @param originalFilename 原始文件名
     * @return 文件访问URL
     */
    String uploadFile(InputStream inputStream, String fileType, String category, String originalFilename) throws IOException;
    
    /**
     * 上传MultipartFile到腾讯云COS
     * 
     * @param file 上传的文件
     * @param fileType 文件类型(如"xrd", "cif", "image"等)
     * @param category 文件分类(如"cuKa", "cuKa1", "cuKa2", "thumbnail"等)
     * @return 文件访问URL
     */
    String uploadFile(MultipartFile file, String fileType, String category) throws IOException;
    
    /**
     * 上传XRD数据并更新材料实体
     * 
     * @param material 要更新的材料实体
     * @param dataType 数据类型(cuKa, cuKa1, cuKa2)
     * @param file 要上传的XRD数据文件
     * @return 文件访问URL
     */
    String uploadXrdData(Material material, String dataType, MultipartFile file) throws IOException;
    
    /**
     * 上传CIF晶体结构文件并关联到材料
     * 
     * @param material 要关联的材料
     * @param file 要上传的CIF文件
     * @return 文件访问URL
     */
    String uploadCifData(Material material, MultipartFile file) throws IOException;
    
    /**
     * 验证XRD数据类型是否有效
     * 
     * @param dataType 数据类型
     * @return 如果数据类型有效则返回true
     */
    boolean isValidXrdDataType(String dataType);
} 