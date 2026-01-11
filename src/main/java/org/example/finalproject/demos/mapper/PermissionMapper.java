package org.example.finalproject.demos.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.example.finalproject.demos.pojo.Permission;

import java.util.List;

@Mapper

public interface PermissionMapper {
    List<Permission> selectAll();
}
