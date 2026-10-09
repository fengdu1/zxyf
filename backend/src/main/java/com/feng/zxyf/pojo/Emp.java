package com.feng.zxyf.pojo;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 员工实体
 * <p>
 * 说明：
 * 1. 使用 Lombok @Data 提供 getter/setter，便于 MyBatis 按 setter 自动映射
 *    （MyBatis 的构造器自动映射要求结果集必须包含全部构造参数，record 无法满足）。
 * 2. @JsonInclude(NON_NULL) 保证列表/详情查询中未查询的字段（如 password、exprList）不返回。
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Emp {

    /** 员工 ID */
    private Integer id;

    /** 用户名 */
    private String username;

    /** 登录密码 */
    private String password;

    /** 姓名 */
    private String name;

    /** 性别, 1: 男, 2: 女 */
    private Integer gender;

    /** 手机号 */
    private String phone;

    /** 职位, 1: 班主任, 2: 讲师, 3: 学工主管, 4: 教研主管, 5: 咨询师 */
    private Integer position;

    /** 薪资 */
    private Integer salary;

    /** 头像路径 */
    private String image;

    /** 文件原始文件名（上传时保存，下载时用于文件名显示） */
    private String originalName;

    /** 入职日期 */
    private LocalDate hireDate;

    /** 所属部门 ID */
    private Integer deptId;

    /** 创建时间 */
    private LocalDateTime createTime;

    /** 最后操作时间 */
    private LocalDateTime updateTime;

    /** 所属部门名称（联表查询填充） */
    private String deptName;

    /** 工作经历列表（详情查询填充） */
    private List<EmpExpr> exprList;
}
