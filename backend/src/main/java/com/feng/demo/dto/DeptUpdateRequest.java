package com.feng.demo.dto;

/**
 * 修改部门请求体
 *
 * @param id   部门 ID
 * @param name 部门名称
 */
public record DeptUpdateRequest(Integer id, String name) {
}