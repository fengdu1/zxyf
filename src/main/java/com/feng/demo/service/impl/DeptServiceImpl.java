package com.feng.demo.service.impl;

import com.feng.demo.mapper.DeptMapper;
import com.feng.demo.pojo.Dept;
import com.feng.demo.service.DeptService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 部门业务实现类
 */
@Service
public class DeptServiceImpl implements DeptService {

    @Autowired
    private DeptMapper deptMapper;

    @Override
    public List<Dept> list() {
        return deptMapper.findAll();
    }

    @Override
    public Dept getById(Integer id) {
        return deptMapper.findById(id);
    }

    @Override
    public void add(String name) {
        deptMapper.insert(name);
    }

    @Override
    public void update(Integer id, String name) {
        deptMapper.update(id, name);
    }

    @Override
    public void delete(Integer id) {
        deptMapper.deleteById(id);
    }
}