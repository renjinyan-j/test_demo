package org.example.finalproject.demos.util;

import org.example.finalproject.demos.pojo.User;
import org.example.finalproject.demos.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

/**
 * Spring Security工具类
 * 用于获取当前登录用户信息
 */
@Component
public class SecurityUtil {

    @Autowired
    private UserService userService;

    /**
     * 获取当前登录用户名
     */
    public String getCurrentUsername() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof UserDetails) {
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            return userDetails.getUsername();
        }
        return null;
    }

    /**
     * 获取当前登录用户对象
     */
    public User getCurrentUser() {
        String username = getCurrentUsername();
        if (username != null) {
            return userService.findByUsername(username);
        }
        return null;
    }

    /**
     * 获取当前登录用户ID
     */
    public Long getCurrentUserId() {
        User user = getCurrentUser();
        return user != null ? user.getId() : null;
    }

    /**
     * 根据用户名获取用户ID（用于WebSocket Principal 场景，通过用户名反查ID）
     */
    public Long getUserIdByUsername(String username) {
        if (username == null) {
            return null;
        }
        User user = userService.findByUsername(username);
        return user != null ? user.getId() : null;
    }

    /**
     * 判断当前用户是否有指定角色
     */
    public boolean hasRole(String roleCode) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null) {
            return authentication.getAuthorities().stream()
                    .anyMatch(authority -> authority.getAuthority().equals(roleCode));
        }
        return false;
    }

    /**
     * 判断当前用户是否是管理员
     */
    public boolean isAdmin() {
        return hasRole("ROLE_ADMIN");
    }

    /**
     * 判断当前用户是否是卖家
     */
    public boolean isSeller() {
        return hasRole("ROLE_SELLER");
    }

    /**
     * 判断当前用户是否是买家
     */
    public boolean isBuyer() {
        return hasRole("ROLE_BUYER") || hasRole("ROLE_USER");
    }
}




