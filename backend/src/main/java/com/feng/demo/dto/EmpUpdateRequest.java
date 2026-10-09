package com.feng.demo.dto;

import java.time.LocalDate;
import java.util.List;

/**
 * 修改员工请求体
 *
 * @param id       员工 ID
 * @param username 用户名
 * @param password 登录密码
 * @param name     姓名
 * @param gender   性别, 1: 男, 2: 女
 * @param phone    手机号
 * @param position 职位
 * @param salary   薪资
 * @param image        头像路径
 * @param originalName 文件原始文件名
 * @param hireDate     入职日期
 * @param deptId       所属部门 ID
 * @param exprList     工作经历列表
 */
public record EmpUpdateRequest(Integer id, String username, String password, String name,
                               Integer gender, String phone, Integer position, Integer salary,
                               String image, String originalName, LocalDate hireDate, Integer deptId,
                               List<EmpExprRequest> exprList) {
}
