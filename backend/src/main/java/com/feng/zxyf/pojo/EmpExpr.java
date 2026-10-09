package com.feng.zxyf.pojo;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.time.LocalDate;

/**
 * 员工工作经历实体
 * 使用 JDK 新语法 record 定义不可变实体
 *
 * @param id        经历 ID
 * @param empId     关联员工的 ID
 * @param company   公司名称
 * @param position  担任职位
 * @param startDate 开始日期
 * @param endDate   结束日期
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public record EmpExpr(Integer id, Integer empId, String company, String position,
                      LocalDate startDate, LocalDate endDate) {
}
