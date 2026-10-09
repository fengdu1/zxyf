package com.feng.zxyf.service;

import com.feng.zxyf.pojo.Dept;

import java.util.List;

/**
 * 部门业务接口
 */
public interface DeptService {

    /**
     * 部门列表查询
     */
    List<Dept> list();

    /**
     * 根据 ID 查询
     */
    Dept getById(Integer id);

    /**
     * 添加部门
     */
    void add(String name);

    /**
     * 修改部门
     */
    void update(Integer id, String name);

    /**
     * 删除部门
     */
    void delete(Integer id);
}