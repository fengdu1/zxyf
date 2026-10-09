package com.feng.demo.controller;

import com.feng.demo.common.Result;
import com.feng.demo.dto.EmpInsertRequest;
import com.feng.demo.dto.EmpUpdateRequest;
import com.feng.demo.service.EmpService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

/**
 * 员工管理接口
 * <p>
 * GET    /emps        员工列表查询（分页条件）
 * GET    /emps/{id}   根据 ID 查询
 * GET    /emps/list   查询全部员工
 * POST   /emps        添加员工
 * PUT    /emps        修改员工
 * DELETE /emps        批量删除员工（ids=1,2,3）
 */
@RestController
@RequestMapping("/emps")
public class EmpController {

    @Autowired
    private EmpService empService;

    /**
     * 员工列表查询
     */
    @GetMapping
    public Result list(@RequestParam(required = false) String name,
                       @RequestParam(required = false) Integer gender,
                       @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate begin,
                       @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate end,
                       @RequestParam(defaultValue = "1") Integer page,
                       @RequestParam(defaultValue = "10") Integer pageSize) {
        return Result.success(empService.list(name, gender, begin, end, page, pageSize));
    }

    /**
     * 根据 ID 查询
     */
    @GetMapping("/{id}")
    public Result getById(@PathVariable Integer id) {
        return Result.success(empService.getById(id));
    }

    /**
     * 查询全部员工
     */
    @GetMapping("/list")
    public Result listAll() {
        return Result.success(empService.listAll());
    }

    /**
     * 添加员工
     */
    @PostMapping
    public Result add(@RequestBody EmpInsertRequest req) {
        empService.add(req);
        return Result.success();
    }

    /**
     * 修改员工
     */
    @PutMapping
    public Result update(@RequestBody EmpUpdateRequest req) {
        empService.update(req);
        return Result.success();
    }

    /**
     * 批量删除员工（ids 为逗号分隔的查询参数，如 /emps?ids=1,2,3）
     */
    @DeleteMapping
    public Result delete(@RequestParam("ids") String ids) {
        List<Integer> idList = Arrays.stream(ids.split(","))
                .map(String::trim)
                .filter(s -> !s.isBlank())
                .map(Integer::valueOf)
                .toList();
        empService.deleteByIds(idList);
        return Result.success();
    }
}
