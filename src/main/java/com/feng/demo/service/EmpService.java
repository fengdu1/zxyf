package com.feng.demo.service;

import com.feng.demo.common.PageResult;
import com.feng.demo.dto.EmpInsertRequest;
import com.feng.demo.dto.EmpUpdateRequest;
import com.feng.demo.pojo.Emp;

import java.time.LocalDate;
import java.util.List;

/**
 * 员工业务接口
 */
public interface EmpService {

    /**
     * 分页条件查询员工列表
     */
    PageResult<Emp> list(String name, Integer gender, LocalDate begin, LocalDate end,
                         Integer page, Integer pageSize);

    /**
     * 根据 ID 查询员工（含工作经历）
     */
    Emp getById(Integer id);

    /**
     * 查询全部员工
     */
    List<Emp> listAll();

    /**
     * 根据头像图片 URL 查询原始文件名（用于下载时显示原始名称）
     */
    String findOriginalNameByUrl(String url);

    /**
     * 添加员工（含工作经历）
     */
    void add(EmpInsertRequest req);

    /**
     * 修改员工（含工作经历）
     */
    void update(EmpUpdateRequest req);

    /**
     * 批量删除员工（级联删除工作经历）
     */
    void deleteByIds(List<Integer> ids);
}
