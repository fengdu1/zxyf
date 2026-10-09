package com.feng.zxyf.service.impl;

import com.feng.zxyf.mapper.EmpMapper;
import com.feng.zxyf.pojo.Emp;
import com.feng.zxyf.service.LoginService;
import com.feng.zxyf.utils.JwtUtils;
import com.feng.zxyf.vo.LoginVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 员工登录服务实现
 * <p>
 * 校验逻辑：按用户名查询员工，比对密码；校验通过后签发 JWT 令牌。
 */
@Service
public class LoginServiceImpl implements LoginService {

    @Autowired
    private EmpMapper empMapper;

    @Autowired
    private JwtUtils jwtUtils;

    @Override
    public LoginVO login(String username, String password) {
        Emp emp = empMapper.findByUsername(username);
        if (emp == null || !password.equals(emp.getPassword())) {
            return null;
        }

        String token = jwtUtils.generateToken(emp.getId(), emp.getUsername());
        
        return new LoginVO(emp.getId(), emp.getUsername(), emp.getName(),token);
    }
}
