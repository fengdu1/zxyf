package com.feng.demo.dto;

import java.time.LocalDate;

/**
 * 工作经历请求体（新增/修改员工时内嵌使用）
 *
 * @param id        经历 ID（新增时为空，修改时由前端回传）
 * @param empId     关联员工的 ID（新增时为空，服务端自动填充）
 * @param company   公司名称
 * @param position  担任职位
 * @param startDate 开始日期
 * @param endDate   结束日期
 */
public record EmpExprRequest(Integer id, Integer empId, String company, String position,
                             LocalDate startDate, LocalDate endDate) {
}
