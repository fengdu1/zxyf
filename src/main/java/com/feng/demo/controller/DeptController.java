package com.feng.demo.controller;

import com.feng.demo.common.Result;
import com.feng.demo.dto.DeptInsertRequest;
import com.feng.demo.dto.DeptUpdateRequest;
import com.feng.demo.pojo.Dept;
import com.feng.demo.service.DeptService;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 * 部门管理接口
 * <p>
 * GET    /depts      部门列表查询
 * GET    /depts/{id} 根据 ID 查询
 * POST   /depts      添加部门
 * PUT    /depts      修改部门
 * DELETE /depts/{id} 删除部门
 */
@RestController
@RequestMapping("/depts")
public class DeptController {

    @Autowired
    private DeptService deptService;

    /**
     * 部门列表查询
     */
    @GetMapping
    public Result list() {
        List<Dept> list = deptService.list();
        return Result.success(list);
    }

    /**
     * 根据 ID 查询
     */
    @GetMapping("/{id}")
    public Result getById(@PathVariable Integer id) {
        return Result.success(deptService.getById(id));
    }

    /**
     * 添加部门
     */
    @PostMapping
    public Result add(@RequestBody DeptInsertRequest req) {
        var name = req.name();
        deptService.add(name);
        return Result.success();
    }

    /**
     * 修改部门
     */
    @PutMapping
    public Result update(@RequestBody DeptUpdateRequest req) {
        deptService.update(req.id(), req.name());
        return Result.success();
    }

    /**
     * 删除部门
     */
    @DeleteMapping("/{id}")
    public Result delete(@PathVariable Integer id) {
        deptService.delete(id);
        return Result.success();
    }
}