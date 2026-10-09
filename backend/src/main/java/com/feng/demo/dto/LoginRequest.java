package com.feng.demo.dto;

/**
 * 登录请求参数
 *
 * @param username 用户名
 * @param password 密码
 */
public record LoginRequest(String username, String password) {
}
