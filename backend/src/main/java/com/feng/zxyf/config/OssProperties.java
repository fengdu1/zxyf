package com.feng.zxyf.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * 阿里云 OSS 配置属性（对应 application.yaml 中 oss.* 配置）
 *
 * @param endpoint        地域节点（含协议，如 https://oss-cn-beijing.aliyuncs.com/）
 * @param accessKeyId     AccessKey ID
 * @param accessKeySecret AccessKey Secret
 * @param bucketName      Bucket 名称
 */
@ConfigurationProperties(prefix = "oss")
public record OssProperties(String endpoint, String accessKeyId, String accessKeySecret, String bucketName) {
}
