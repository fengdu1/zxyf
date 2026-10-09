package com.feng.zxyf.controller;

import com.feng.zxyf.common.Result;
import com.feng.zxyf.dto.LoginRequest;
import com.feng.zxyf.service.LoginService;
import com.feng.zxyf.vo.LoginVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 员工登录接口
 * <p>
 * POST /login 登录成功后下发 JWT 令牌，后续请求需在 header 的 token 中携带。
 */
@RestController
@RequestMapping("/login")
public class LoginController {

    @Autowired
    private LoginService loginService;

    @PostMapping
    public Result login(@RequestBody LoginRequest req) {
        LoginVO loginVO = loginService.login(req.username(), req.password());
        if (loginVO == null) {
            return Result.error("用户名或密码错误");
        }
        return Result.success(loginVO);
    }
}
