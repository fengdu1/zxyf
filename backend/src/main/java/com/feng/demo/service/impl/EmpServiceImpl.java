package com.feng.demo.service.impl;

import com.feng.demo.common.PageResult;
import com.feng.demo.dto.EmpExprRequest;
import com.feng.demo.dto.EmpInsertRequest;
import com.feng.demo.dto.EmpUpdateRequest;
import com.feng.demo.mapper.EmpExprMapper;
import com.feng.demo.mapper.EmpMapper;
import com.feng.demo.pojo.Emp;
import com.feng.demo.pojo.EmpExpr;
import com.feng.demo.service.EmpService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

/**
 * 员工业务实现类
 */
@Service
public class EmpServiceImpl implements EmpService {

    @Autowired
    private EmpMapper empMapper;

    @Autowired
    private EmpExprMapper empExprMapper;

    @Override
    public PageResult<Emp> list(String name, Integer gender, LocalDate begin, LocalDate end,
                                Integer page, Integer pageSize) {
        PageHelper.startPage(page, pageSize);
        List<Emp> list = empMapper.list(name, gender, begin, end);
        PageInfo<Emp> pageInfo = new PageInfo<>(list);
        return new PageResult<>(pageInfo.getTotal(), list);
    }

    @Override
    public Emp getById(Integer id) {
        Emp emp = empMapper.findById(id);
        if (emp == null) {
            return null;
        }
        // 工作经历列表单独查询后组装（Emp 为可变实体，直接 set）
        emp.setExprList(empExprMapper.findByEmpId(id));
        return emp;
    }

    @Override
    public List<Emp> listAll() {
        return empMapper.findAllList();
    }

    @Transactional
    @Override
    public void add(EmpInsertRequest req) {
        // 新增员工密码统一使用默认值 123456
        Emp emp = new Emp(null, req.username(), "123456", req.name(), req.gender(), req.phone(),
                req.position(), req.salary(), req.image(), req.originalName(), req.hireDate(), req.deptId(),
                null, null, null, null);
        empMapper.insert(emp);
        insertExprs(empMapper.lastInsertId().intValue(), req.exprList());
    }

    @Transactional
    @Override
    public void update(EmpUpdateRequest req) {
        Emp emp = new Emp(req.id(), req.username(), req.password(), req.name(), req.gender(), req.phone(),
                req.position(), req.salary(), req.image(), req.originalName(), req.hireDate(), req.deptId(),
                null, null, null, null);
        empMapper.update(emp);
        // 先删除旧工作经历，再插入最新工作经历
        empExprMapper.deleteByEmpId(req.id());
        insertExprs(req.id(), req.exprList());
    }

    @Override
    public String findOriginalNameByUrl(String url) {
        return empMapper.findOriginalNameByUrl(url);
    }

    @Transactional
    @Override
    public void deleteByIds(List<Integer> ids) {
        empExprMapper.deleteByEmpIds(ids);
        empMapper.deleteByIds(ids);
    }

    /**
     * 批量插入工作经历（列表为空时跳过）
     */
    private void insertExprs(Integer empId, List<EmpExprRequest> exprList) {
        if (exprList == null || exprList.isEmpty()) {
            return;
        }
        List<EmpExpr> exprs = exprList.stream()
                .map(e -> new EmpExpr(e.id(), empId, e.company(), e.position(), e.startDate(), e.endDate()))
                .toList();
        empExprMapper.insertExprs(exprs);
    }
}
