package com.feng.demo.mapper;

import com.feng.demo.pojo.Dept;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

/**
 * 部门 Mapper
 * 使用 MyBatis 注解方式编写 SQL
 */
@Mapper
public interface DeptMapper {

    /**
     * 查询全部部门
     */
    @Select("SELECT id, name, create_time, update_time FROM dept")
    List<Dept> findAll();

    /**
     * 根据 ID 查询部门
     */
    @Select("SELECT id, name, create_time, update_time FROM dept WHERE id = #{id}")
    Dept findById(Integer id);

    /**
     * 新增部门（create_time、update_time 由数据库默认值自动填充）
     */
    @Insert("INSERT INTO dept(name) VALUES (#{name})")
    int insert(@Param("name") String name);

    /**
     * 修改部门名称（update_time 由数据库 ON UPDATE 自动更新）
     */
    @Update("UPDATE dept SET name = #{name} WHERE id = #{id}")
    int update(@Param("id") Integer id, @Param("name") String name);

    /**
     * 根据 ID 删除部门
     */
    @Delete("DELETE FROM dept WHERE id = #{id}")
    int deleteById(Integer id);
}