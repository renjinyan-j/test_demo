package org.example.finalproject.demos.service.impl;

import org.example.finalproject.demos.mapper.PermissionMapper;
import org.example.finalproject.demos.pojo.Permission;
import org.example.finalproject.demos.service.PermissionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class PermissionServiceImpl implements PermissionService {

    private static final Logger logger = LoggerFactory.getLogger(PermissionServiceImpl.class);

    @Autowired
    PermissionMapper permissionMapper;

    @Override
    public List<Permission> getAllPermissions() {
        try {
            List<Permission> permissions = permissionMapper.selectAll();
            // 增强健壮性：处理查询结果为null的情况
            if (permissions == null) {
                logger.warn("PermissionMapper.selectAll() returned null, returning empty list instead");
                return new ArrayList<>();
            }
            logger.info("Loaded {} permissions from database", permissions.size());
            return permissions;
        } catch (Exception e) {
            logger.error("Error occurred while fetching permissions from database", e);
            // 发生异常时返回空列表而不是抛出异常
            return new ArrayList<>();
        }
    }
}