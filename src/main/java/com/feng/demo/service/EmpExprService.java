package com.feng.demo.service;

import com.feng.demo.dto.EmpExprRequest;
import com.feng.demo.pojo.EmpExpr;

import java.util.List;

/**
 * 员工工作经历业务接口
 */
public interface EmpExprService {

    /**
     * 根据员工 ID 查询工作经历
     */
    List<EmpExpr> list(Integer empId);

    /**
     * 添加工作经历
     */
    void add(EmpExprRequest req);

    /**
     * 修改工作经历
     */
    void update(EmpExprRequest req);

    /**
     * 删除工作经历
     */
    void delete(Integer id);
}
