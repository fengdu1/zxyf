package com.feng.demo.common;

import java.util.List;

/**
 * 分页查询结果
 * 使用 JDK 新语法 record 定义不可变结果对象
 *
 * @param total 总记录数
 * @param rows  当前页数据
 */
public record PageResult<T>(long total, List<T> rows) {
}
