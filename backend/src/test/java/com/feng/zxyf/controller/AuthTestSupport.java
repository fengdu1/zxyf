package com.feng.zxyf.controller;

import com.feng.zxyf.mapper.EmpMapper;
import com.feng.zxyf.pojo.Emp;
import com.jayway.jsonpath.JsonPath;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * 接口测试基类：登录拦截器生效后，所有业务接口需携带 token。
 * <p>
 * 流程：直接经 Mapper 插入登录测试用户（绕过拦截器）→ 调 /login 获取 JWT 令牌
 * → 子类所有请求统一通过 {@link #perform} 携带 token；用例结束后删除测试用户。
 */
@SpringBootTest
@AutoConfigureMockMvc
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public abstract class AuthTestSupport {

    @Autowired
    protected MockMvc mockMvc;

    @Autowired
    private EmpMapper empMapper;

    protected String token;
    private Integer loginEmpId;

    @BeforeAll
    void setUpLoginToken() throws Exception {
        String suffix = String.valueOf(System.currentTimeMillis());
        String username = "lt" + suffix;
        String phone = "188" + suffix.substring(suffix.length() - 8);

        Emp emp = new Emp(null, username, "123456", "登录测试", 1, phone, 1, 5000,
                null, null, LocalDate.of(2024, 1, 1), 1, null, null, null, null);
        empMapper.insert(emp);
        loginEmpId = empMapper.lastInsertId().intValue();

        // 登录接口放行拦截器，借此获取令牌
        String resp = mockMvc.perform(post("/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\":\"%s\",\"password\":\"123456\"}".formatted(username)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(1))
                .andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8);
        token = JsonPath.read(resp, "$.data.token");
    }

    @AfterAll
    void tearDownLoginUser() {
        empMapper.deleteByIds(List.of(loginEmpId));
    }

    /** 携带登录令牌执行请求 */
    protected ResultActions perform(MockHttpServletRequestBuilder builder) throws Exception {
        return mockMvc.perform(builder.header("token", token));
    }
}
