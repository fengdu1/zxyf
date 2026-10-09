package com.feng.zxyf.interceptor;

import com.feng.zxyf.utils.JwtUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * 登录校验拦截器
 * <p>
 * 校验请求头 header 中的 token（登录时下发的 JWT 令牌）：
 * - 校验通过：放行
 * - 未登录 / 令牌无效 / 已过期：响应 401 状态码，由前端统一跳转登录页面
 */
@Component
public class LoginInterceptor implements HandlerInterceptor {

    @Autowired
    private JwtUtils jwtUtils;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String token = request.getHeader("token");
        if (token != null && jwtUtils.parseToken(token) != null) {
            return true;
        }
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json;charset=UTF-8");
        response.getWriter().write("{\"code\":0,\"msg\":\"NOT_LOGIN\",\"data\":null}");
        return false;
    }
}
