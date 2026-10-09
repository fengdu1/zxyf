package com.feng.demo.mapper;

import com.feng.demo.pojo.EmpExpr;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

/**
 * 员工工作经历 Mapper
 * 使用 MyBatis 注解方式编写 SQL（<script> 动态 SQL + 文本块）
 */
@Mapper
public interface EmpExprMapper {

    /**
     * 根据员工 ID 查询工作经历
     */
    @Select("""
            SELECT id, emp_id, company, position, start_date, end_date
            FROM emp_expr
            WHERE emp_id = #{empId}
            ORDER BY start_date ASC
            """)
    List<EmpExpr> findByEmpId(Integer empId);

    /**
     * 新增单条工作经历
     */
    @Insert("""
            INSERT INTO emp_expr(emp_id, company, position, start_date, end_date)
            VALUES (#{empId}, #{company}, #{position}, #{startDate}, #{endDate})
            """)
    int insertExpr(EmpExpr expr);

    /**
     * 批量新增工作经历
     */
    @Insert("""
            <script>
            INSERT INTO emp_expr(emp_id, company, position, start_date, end_date) VALUES
            <foreach collection="list" item="e" separator=",">
                (#{e.empId}, #{e.company}, #{e.position}, #{e.startDate}, #{e.endDate})
            </foreach>
            </script>
            """)
    int insertExprs(@Param("list") List<EmpExpr> list);

    /**
     * 修改单条工作经历
     */
    @Update("""
            UPDATE emp_expr
            SET company = #{company}, position = #{position},
                start_date = #{startDate}, end_date = #{endDate}
            WHERE id = #{id}
            """)
    int updateExpr(EmpExpr expr);

    /**
     * 根据 ID 删除单条工作经历
     */
    @Delete("DELETE FROM emp_expr WHERE id = #{id}")
    int deleteById(Integer id);

    /**
     * 根据员工 ID 删除全部工作经历
     */
    @Delete("DELETE FROM emp_expr WHERE emp_id = #{empId}")
    int deleteByEmpId(Integer empId);

    /**
     * 根据员工 ID 批量删除工作经历
     */
    @Delete("""
            <script>
            DELETE FROM emp_expr WHERE emp_id IN
            <foreach collection="ids" item="id" open="(" separator="," close=")">
                #{id}
            </foreach>
            </script>
            """)
    int deleteByEmpIds(@Param("ids") List<Integer> ids);
}
