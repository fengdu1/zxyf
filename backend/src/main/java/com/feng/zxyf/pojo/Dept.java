package com.feng.zxyf.pojo;

import java.time.LocalDateTime;

/**
 * 部门实体
 * 使用 JDK 新语法 record 定义不可变实体
 *
 * @param id         部门 ID
 * @param name       部门名称
 * @param createTime 创建时间
 * @param updateTime 最后操作时间
 */
public record Dept(Integer id, String name, LocalDateTime createTime, LocalDateTime updateTime) {
}