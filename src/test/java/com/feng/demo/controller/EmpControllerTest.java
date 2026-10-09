package com.feng.demo.controller;

import com.jayway.jsonpath.JsonPath;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import java.nio.charset.StandardCharsets;

import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.lessThanOrEqualTo;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * 员工管理接口测试（MockMvc 集成测试，命中本地 MySQL，自建数据并自清理）
 */
@SpringBootTest
@AutoConfigureMockMvc
class EmpControllerTest extends AuthTestSupport {

    /* ---------- 测试数据辅助 ---------- */

    private String suffix() {
        return String.valueOf(System.currentTimeMillis());
    }

    private String username(String suffix) {
        return "testu" + suffix;
    }

    private String phone(String suffix) {
        return "188" + suffix.substring(suffix.length() - 8);
    }

    private String name(String suffix) {
        return "测试" + suffix.substring(suffix.length() - 3);
    }

    private String insertPayload(String suffix) {
        return """
                {"username":"%s","name":"%s","gender":1,"position":2,"phone":"%s","salary":6000,
                 "image":null,"hireDate":"2023-05-01","deptId":1,
                 "exprList":[{"company":"测试公司%s","position":"开发","startDate":"2020-01-01","endDate":"2022-01-01"}]}
                """.formatted(username(suffix), name(suffix), phone(suffix), suffix);
    }

    /** 通过列表接口按唯一姓名反查新员工 id */
    private Integer findEmpId(String uniqueName) throws Exception {
        String resp = perform(get("/emps")
                        .param("name", uniqueName).param("page", "1").param("pageSize", "10"))
                .andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8);
        return JsonPath.read(resp, "$.data.rows[0].id");
    }

    private void deleteEmp(Integer id) throws Exception {
        perform(delete("/emps").param("ids", String.valueOf(id)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(1));
    }

    /* ---------- 2.1 员工列表查询 ---------- */

    @Test
    void list_withPagination() throws Exception {
        perform(get("/emps").param("page", "1").param("pageSize", "5"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(1))
                .andExpect(jsonPath("$.data.total", greaterThanOrEqualTo(0)))
                .andExpect(jsonPath("$.data.rows").isArray())
                .andExpect(jsonPath("$.data.rows.length()", lessThanOrEqualTo(5)));
    }

    @Test
    void list_withFilters() throws Exception {
        // 姓名 + 性别组合条件查询
        perform(get("/emps")
                        .param("name", "张").param("gender", "1")
                        .param("page", "1").param("pageSize", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(1))
                .andExpect(jsonPath("$.data.rows").isArray());

        // 入职日期区间条件查询
        perform(get("/emps")
                        .param("begin", "2019-01-01").param("end", "2021-12-31")
                        .param("page", "1").param("pageSize", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(1));
    }

    /* ---------- 2.4 根据 ID 查询 ---------- */

    @Test
    void getById_shouldReturnEmpWithExprList() throws Exception {
        perform(get("/emps/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(1))
                .andExpect(jsonPath("$.data.id").value(1))
                .andExpect(jsonPath("$.data.deptName", notNullValue()))
                .andExpect(jsonPath("$.data.exprList").isArray());
    }

    /* ---------- 2.6 查询全部员工 ---------- */

    @Test
    void listAll_shouldReturnEmpsWithPassword() throws Exception {
        perform(get("/emps/list"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(1))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data[0].password", notNullValue()));
    }

    /* ---------- 2.3 添加员工 ---------- */

    @Test
    void add_shouldInsertEmpAndExpr() throws Exception {
        String suffix = suffix();
        perform(post("/emps")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(insertPayload(suffix)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(1))
                .andExpect(jsonPath("$.msg").value("success"));

        try {
            Integer empId = findEmpId(name(suffix));
            // 验证详情含手机号与工作经历
            perform(get("/emps/{id}", empId))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.data.id").value(empId))
                    .andExpect(jsonPath("$.data.phone").value(phone(suffix)))
                    .andExpect(jsonPath("$.data.exprList[0].company").value("测试公司" + suffix));
        } finally {
            deleteEmp(findEmpId(name(suffix)));
        }
    }

    /* ---------- 2.5 修改员工 ---------- */

    @Test
    void update_shouldReplaceEmpAndExpr() throws Exception {
        String suffix = suffix();
        perform(post("/emps")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(insertPayload(suffix)))
                .andExpect(jsonPath("$.code").value(1));

        Integer empId = findEmpId(name(suffix));
        try {
            String updatePayload = """
                    {"id":%d,"username":"%s","password":"123456","name":"%s","gender":2,
                     "phone":"%s","position":3,"salary":7000,"image":null,"hireDate":"2023-05-01","deptId":2,
                     "exprList":[{"company":"新公司%s","position":"架构","startDate":"2022-01-01","endDate":"2023-01-01"}]}
                    """.formatted(empId, username(suffix), name(suffix), phone(suffix), suffix);
            perform(put("/emps")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(updatePayload))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.code").value(1));

            // 修改生效：职位变更、经历整体替换
            perform(get("/emps/{id}", empId))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.data.position").value(3))
                    .andExpect(jsonPath("$.data.gender").value(2))
                    .andExpect(jsonPath("$.data.exprList.length()").value(1))
                    .andExpect(jsonPath("$.data.exprList[0].company").value("新公司" + suffix));
        } finally {
            deleteEmp(empId);
        }
    }

    /* ---------- 2.2 删除员工 ---------- */

    @Test
    void deleteByIds_shouldRemoveEmp() throws Exception {
        String suffix = suffix();
        perform(post("/emps")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(insertPayload(suffix)))
                .andExpect(jsonPath("$.code").value(1));

        Integer empId = findEmpId(name(suffix));
        deleteEmp(empId);

        // 删除后按 ID 查询返回 data 为 null
        perform(get("/emps/{id}", empId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(1))
                .andExpect(jsonPath("$.data", nullValue()));
    }
}
