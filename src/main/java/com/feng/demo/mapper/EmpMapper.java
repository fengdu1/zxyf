package com.feng.demo.mapper;

import com.feng.demo.pojo.Emp;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.time.LocalDate;
import java.util.List;

/**
 * 员工 Mapper
 * 使用 MyBatis 注解方式编写 SQL（<script> 动态 SQL + 文本块）
 * <p>
 * 说明：Emp 为 Lombok @Data 可变实体，MyBatis 按 setter 自动映射
 * （mapUnderscoreToCamelCase 开启，列名与属性自动对齐），
 * 未查询的字段保持 null，配合 @JsonInclude(NON_NULL) 不参与序列化。
 */
@Mapper
public interface EmpMapper {

    /**
     * 分页条件查询员工列表（联表查询部门名称）
     */
    @Select("""
            <script>
            SELECT e.id, e.username, e.name, e.gender, e.phone, e.position,
                   e.salary, e.image, e.original_name, e.hire_date, e.dept_id, e.create_time, e.update_time,
                   d.name AS dept_name
            FROM emp e
            LEFT JOIN dept d ON e.dept_id = d.id
            <where>
                <if test="name != null and name != ''">
                    AND e.name LIKE CONCAT('%', #{name}, '%')
                </if>
                <if test="gender != null">
                    AND e.gender = #{gender}
                </if>
                <if test="begin != null">
                    AND e.hire_date &gt;= #{begin}
                </if>
                <if test="end != null">
                    AND e.hire_date &lt;= #{end}
                </if>
            </where>
            ORDER BY e.update_time DESC
            </script>
            """)
    List<Emp> list(@Param("name") String name, @Param("gender") Integer gender,
                   @Param("begin") LocalDate begin, @Param("end") LocalDate end);

    /**
     * 根据 ID 查询员工（联表查询部门名称，带出手机号供编辑回显）
     */
    @Select("""
            SELECT e.id, e.username, e.name, e.gender, e.phone, e.position,
                   e.salary, e.image, e.original_name, e.hire_date, e.dept_id, e.create_time, e.update_time,
                   d.name AS dept_name
            FROM emp e
            LEFT JOIN dept d ON e.dept_id = d.id
            WHERE e.id = #{id}
            """)
    Emp findById(Integer id);

    /**
     * 查询全部员工（含密码，供登录等场景使用）
     */
    @Select("""
            SELECT id, username, password, name, gender, phone, position,
                   salary, image, original_name, hire_date, dept_id, create_time, update_time
            FROM emp
            ORDER BY update_time DESC
            """)
    List<Emp> findAllList();

    /**
     * 根据用户名查询员工（含密码，供登录校验）
     */
    @Select("""
            SELECT id, username, password, name, gender, phone, position,
                   salary, image, original_name, hire_date, dept_id, create_time, update_time
            FROM emp
            WHERE username = #{username}
            """)
    Emp findByUsername(String username);

    /**
     * 新增员工（create_time、update_time 由数据库默认值自动填充）
     */
    @Insert("""
            INSERT INTO emp(username, password, name, gender, phone, position, salary, image, original_name, hire_date, dept_id)
            VALUES (#{username}, #{password}, #{name}, #{gender}, #{phone}, #{position}, #{salary}, #{image}, #{originalName}, #{hireDate}, #{deptId})
            """)
    int insert(Emp emp);

    /**
     * 获取最近一次插入的自增主键（需与 insert 在同一事务/同一连接内执行）
     */
    @Select("SELECT LAST_INSERT_ID()")
    Long lastInsertId();

    /**
     * 修改员工（update_time 由数据库 ON UPDATE 自动更新）
     */
    @Update("""
            <script>
            UPDATE emp
            <set>
                username = #{username},
                name = #{name},
                gender = #{gender},
                <if test="phone != null">phone = #{phone},</if>
                position = #{position},
                salary = #{salary},
                image = #{image},
                original_name = #{originalName},
                hire_date = #{hireDate},
                dept_id = #{deptId},
                <if test="password != null and password != ''">password = #{password},</if>
            </set>
            WHERE id = #{id}
            </script>
            """)
    int update(Emp emp);

    /**
     * 根据 ID 批量删除员工
     */
    @Delete("""
            <script>
            DELETE FROM emp WHERE id IN
            <foreach collection="ids" item="id" open="(" separator="," close=")">
                #{id}
            </foreach>
            </script>
            """)
    int deleteByIds(@Param("ids") List<Integer> ids);

    /**
     * 根据头像图片 URL 查询原始文件名（用于下载时显示原始名称）
     */
    @Select("""
            SELECT original_name
            FROM emp
            WHERE image = #{url}
            LIMIT 1
            """)
    String findOriginalNameByUrl(String url);
}
