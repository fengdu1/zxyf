package com.feng.zxyf.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

import java.nio.charset.StandardCharsets;
import java.util.stream.StreamSupport;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * 员工工作经历接口测试（MockMvc 集成测试，命中本地 MySQL，自建数据并自清理）
 * <p>
 * 说明：响应解析统一使用 ObjectMapper + 显式 UTF-8 解码 + AssertJ 断言，
 * 避免 MockMvc 内置 jsonPath 过滤器按默认字符集读响应导致的中文匹配问题。
 */
@SpringBootTest
@AutoConfigureMockMvc
class EmpExprControllerTest extends AuthTestSupport {

    @Autowired
    private ObjectMapper objectMapper;

    /** 唯一公司名（ASCII，避免编码干扰） */
    private String company() {
        return "TestExpr" + System.currentTimeMillis();
    }

    /** 从某员工的经历列表中反查指定公司的经历 id */
    private Integer findExprId(Integer empId, String targetCompany) throws Exception {
        JsonNode data = listExprs(empId);
        for (JsonNode node : data) {
            if (targetCompany.equals(node.path("company").asText())) {
                return node.path("id").asInt();
            }
        }
        throw new AssertionError("未找到公司为 [" + targetCompany + "] 的经历");
    }

    private JsonNode listExprs(Integer empId) throws Exception {
        String resp = perform(get("/exprs").param("empId", String.valueOf(empId)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(1))
                .andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8);
        return objectMapper.readTree(resp).path("data");
    }

    private void deleteExpr(Integer id) throws Exception {
        perform(delete("/exprs/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(1));
    }

    /* ---------- 查询某员工的工作经历 ---------- */

    @Test
    void list_byEmpId() throws Exception {
        JsonNode data = listExprs(1);
        assertThat(data.isArray()).isTrue();
        assertThat(data.size()).isGreaterThanOrEqualTo(1);
    }

    /* ---------- 添加 / 删除工作经历 ---------- */

    @Test
    void addAndDelete_expr() throws Exception {
        String target = company();
        perform(post("/exprs")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"empId":1,"company":"%s","position":"dev","startDate":"2020-01-01","endDate":"2022-01-01"}
                                """.formatted(target)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(1));

        Integer exprId = findExprId(1, target);
        try {
            // 查询确认新增记录存在
            boolean found = StreamSupport.stream(listExprs(1).spliterator(), false)
                    .anyMatch(n -> target.equals(n.path("company").asText()));
            assertThat(found).isTrue();
        } finally {
            deleteExpr(exprId);
        }
    }

    /* ---------- 修改工作经历 ---------- */

    @Test
    void update_expr() throws Exception {
        String origin = company();
        String changed = "Changed" + System.currentTimeMillis();
        perform(post("/exprs")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"empId":1,"company":"%s","position":"dev","startDate":"2020-01-01","endDate":"2022-01-01"}
                                """.formatted(origin)))
                .andExpect(jsonPath("$.code").value(1));

        Integer exprId = findExprId(1, origin);
        try {
            perform(put("/exprs")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("""
                                    {"id":%d,"empId":1,"company":"%s","position":"arch","startDate":"2022-01-01","endDate":"2023-01-01"}
                                    """.formatted(exprId, changed)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.code").value(1));

            // 修改生效：公司名已变更
            JsonNode data = listExprs(1);
            boolean hasChanged = StreamSupport.stream(data.spliterator(), false)
                    .anyMatch(n -> changed.equals(n.path("company").asText()));
            boolean hasOrigin = StreamSupport.stream(data.spliterator(), false)
                    .anyMatch(n -> origin.equals(n.path("company").asText()));
            assertThat(hasChanged).isTrue();
            assertThat(hasOrigin).isFalse();
        } finally {
            deleteExpr(exprId);
        }
    }
}
