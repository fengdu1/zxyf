package com.feng.zxyf.service.impl;

import com.feng.zxyf.dto.EmpExprRequest;
import com.feng.zxyf.mapper.EmpExprMapper;
import com.feng.zxyf.pojo.EmpExpr;
import com.feng.zxyf.service.EmpExprService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 员工工作经历业务实现类
 */
@Service
public class EmpExprServiceImpl implements EmpExprService {

    @Autowired
    private EmpExprMapper empExprMapper;

    @Override
    public List<EmpExpr> list(Integer empId) {
        return empExprMapper.findByEmpId(empId);
    }

    @Override
    public void add(EmpExprRequest req) {
        EmpExpr expr = new EmpExpr(null, req.empId(), req.company(), req.position(),
                req.startDate(), req.endDate());
        empExprMapper.insertExpr(expr);
    }

    @Override
    public void update(EmpExprRequest req) {
        EmpExpr expr = new EmpExpr(req.id(), req.empId(), req.company(), req.position(),
                req.startDate(), req.endDate());
        empExprMapper.updateExpr(expr);
    }

    @Override
    public void delete(Integer id) {
        empExprMapper.deleteById(id);
    }
}
