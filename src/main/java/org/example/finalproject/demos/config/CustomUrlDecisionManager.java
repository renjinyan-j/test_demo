package org.example.finalproject.demos.config;

import org.springframework.security.access.AccessDecisionManager;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.ConfigAttribute;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.Iterator;

//AccessDecisionManager接口的自定义实现类：
//用于根据用户的权限决定是否允许访问特定资源
@Component
public class CustomUrlDecisionManager implements AccessDecisionManager {
    @Override
    public void decide(Authentication authentication, Object object, Collection<ConfigAttribute> configAttributes) throws AccessDeniedException, InsufficientAuthenticationException {
        if (configAttributes == null || configAttributes.isEmpty()) {
            return;
        }

        // 遍历所需权限，判断用户是否拥有其中之一
        Iterator<ConfigAttribute> iterator = configAttributes.iterator();
        while (iterator.hasNext()) {
            ConfigAttribute configAttribute = iterator.next();
            String needPermission = configAttribute.getAttribute();

            // 特殊处理：需要登录的情况
            if ("ROLE_LOGIN".equals(needPermission)) {
                if (authentication == null) {
                    throw new AccessDeniedException("尚未登录，请登录");
                } else {
                    return;
                }
            }

            // 判断用户是否拥有所需权限
            Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
            for (GrantedAuthority authority : authorities) {
                if (needPermission.equals(authority.getAuthority())) {
                    return;
                }
            }
        }

        // 没有匹配到权限
        throw new AccessDeniedException("没有访问权限，请联系管理员");
    }

    @Override
    public boolean supports(ConfigAttribute attribute) {
        return true;
    }

    @Override
    public boolean supports(Class<?> clazz) {
        return true;
    }
}
