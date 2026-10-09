package com.feng.demo.service;

import com.feng.demo.vo.LoginVO;

/**
 * 员工登录服务
 */
public interface LoginService {

    /**
     * 登录校验，成功返回携带 JWT 令牌的登录信息，失败返回 null
     */
    LoginVO login(String username, String password);
}
