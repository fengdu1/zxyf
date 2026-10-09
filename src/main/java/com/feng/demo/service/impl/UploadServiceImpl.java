package com.feng.demo.service.impl;

import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.aliyun.oss.model.OSSObject;
import com.feng.demo.config.OssProperties;
import com.feng.demo.service.UploadService;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.UUID;

/**
 * 阿里云 OSS 文件上传服务实现
 * <p>
 * 命名规则：UUID（去除横线）作为文件名，保留原始扩展名，避免文件覆盖。
 */
@Service
public class UploadServiceImpl implements UploadService {

    private final OSS ossClient;
    private final String bucketName;
    private final String urlPrefix;

    public UploadServiceImpl(OssProperties properties) {
        this.bucketName = properties.bucketName();
        this.ossClient = new OSSClientBuilder().build(properties.endpoint(),
                properties.accessKeyId(), properties.accessKeySecret());
        // 由 endpoint 推导访问域名，如 https://oss-cn-beijing.aliyuncs.com/ → oss-cn-beijing.aliyuncs.com
        String domain = properties.endpoint().replaceAll("^https?://", "").replaceAll("/+$", "");
        this.urlPrefix = "https://" + bucketName + "." + domain;
    }

    @Override
    public String upload(MultipartFile file) {
        String originalFilename = file.getOriginalFilename();
        String ext = "";
        if (originalFilename != null) {
            int idx = originalFilename.lastIndexOf('.');
            if (idx >= 0) {
                ext = originalFilename.substring(idx);
            }
        }
        String objectKey = UUID.randomUUID().toString().replace("-", "") + ext;

        try (InputStream in = file.getInputStream()) {
            ossClient.putObject(bucketName, objectKey, in);
        } catch (IOException e) {
            throw new RuntimeException("文件上传失败", e);
        }
        return urlPrefix + "/" + objectKey;
    }

    @Override
    public InputStream downloadStream(String url) {
        OSSObject ossObject = ossClient.getObject(bucketName, extractObjectKey(url));
        // 注意：这里不能关闭流，交给 Controller 的 try-with-resources 去关闭
        return ossObject.getObjectContent();
    }

    /**
     * 从文件 URL 中提取 OSS 对象 key，如
     * https://zxyf-feng.oss-cn-beijing.aliyuncs.com/{objectKey}
     */
    private String extractObjectKey(String url) {
        String prefix = urlPrefix + "/";
        if (url.startsWith(prefix)) {
            return url.substring(prefix.length());
        }
        return URI.create(url).getPath().substring(1);
    }
}
