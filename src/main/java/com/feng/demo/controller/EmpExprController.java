package com.feng.demo.controller;

import com.feng.demo.common.Result;
import com.feng.demo.dto.EmpExprRequest;
import com.feng.demo.service.EmpExprService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 员工工作经历接口
 * <p>
 * GET    /exprs?empId=1  查询某员工的工作经历
 * POST   /exprs          添加工作经历
 * PUT    /exprs          修改工作经历
 * DELETE /exprs/{id}     删除工作经历
 */
@RestController
@RequestMapping("/exprs")
public class EmpExprController {

    @Autowired
    private EmpExprService empExprService;

    /**
     * 查询某员工的工作经历
     */
    @GetMapping
    public Result list(@RequestParam("empId") Integer empId) {
        return Result.success(empExprService.list(empId));
    }

    /**
     * 添加工作经历
     */
    @PostMapping
    public Result add(@RequestBody EmpExprRequest req) {
        empExprService.add(req);
        return Result.success();
    }

    /**
     * 修改工作经历
     */
    @PutMapping
    public Result update(@RequestBody EmpExprRequest req) {
        empExprService.update(req);
        return Result.success();
    }

    /**
     * 删除工作经历
     */
    @DeleteMapping("/{id}")
    public Result delete(@PathVariable Integer id) {
        empExprService.delete(id);
        return Result.success();
    }
}
