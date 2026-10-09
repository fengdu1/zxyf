package com.feng.zxyf.service;

import java.io.InputStream;

import org.springframework.web.multipart.MultipartFile;

/**
 * 文件上传服务
 */
public interface UploadService {

    /**
     * 上传文件到阿里云 OSS，返回可访问的文件 URL
     */
    String upload(MultipartFile file);

    /**
     * 根据文件 URL 从 OSS 下载文件内容（字节数组）
     */
    InputStream downloadStream(String url);
}
