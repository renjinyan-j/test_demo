package org.example.finalproject.demos.pojo;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class Role {
    private Long id;
    private String roleName;
    private String roleCode;
    private String description;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;

    // 角色包含的权限列表
    private List<Permission> permissions;
}
