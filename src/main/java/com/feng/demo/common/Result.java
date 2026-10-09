package com.feng.demo.common;

/**
 * 统一响应结果
 * 使用 JDK 新语法 record 定义不可变结果对象
 *
 * @param code 操作状态码，1：成功，0：失败
 * @param msg  提示信息
 * @param data 返回的数据
 */
public record Result(Integer code, String msg, Object data) {

    public static Result success() {
        return new Result(1, "success", null);
    }

    public static Result success(Object data) {
        return new Result(1, "success", data);
    }

    public static Result error(String msg) {
        return new Result(0, msg, null);
    }
}