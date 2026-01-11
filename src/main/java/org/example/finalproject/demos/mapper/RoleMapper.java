package org.example.finalproject.demos.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.example.finalproject.demos.pojo.Role;

import java.util.List;

@Mapper
public interface RoleMapper {
    List<Role> selectAll();
    
    /**
     * 根据角色代码查询角色
     */
    Role selectByRoleCode(@Param("roleCode") String roleCode);
}
