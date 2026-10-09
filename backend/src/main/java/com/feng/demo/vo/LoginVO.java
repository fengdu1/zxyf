package com.feng.demo.vo;

/**
 * 登录成功返回数据
 *
 * @param id       员工 ID
 * @param username 用户名
 * @param name     姓名
 * @param token    JWT 令牌（后续请求需携带在 header 的 token 中）
 */
public record LoginVO(Integer id, String username, String name, String token) {
}
