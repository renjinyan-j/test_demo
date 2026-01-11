package org.example.finalproject.demos.config;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collection;

/**
 * 自定义登录成功处理器
 * 根据用户角色跳转到不同页面
 */
//注册Bean
@Component
//SavedRequestAwareAuthenticationSuccessHandler是Spring Security提供的一个类，用于处理认证成功后的逻辑
//继承该类可以自定义用户登录成功后的行为
public class CustomAuthenticationSuccessHandler extends SavedRequestAwareAuthenticationSuccessHandler {

//    onAuthenticationSuccess： 该方法在用户成功认证后被调用
    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {
        // 获取用户角色
        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
        
        String targetUrl = determineTargetUrl(authorities);
        
        // 跳转到目标页面
        getRedirectStrategy().sendRedirect(request, response, targetUrl);
    }

    /**
     * 根据用户角色确定跳转目标URL
     */
    private String determineTargetUrl(Collection<? extends GrantedAuthority> authorities) {
        // 若包含管理员，跳转后台；其余角色（卖家/买家/普通用户）统一跳前台首页
        for (GrantedAuthority authority : authorities) {
            String authorityName = authority.getAuthority();
            if ("ROLE_ADMIN".equals(authorityName)) {
                return "/admin/dashboard";
            }
        }
        // 默认前台首页（买家/卖家公用）
        return "/front/homepage";
    }
}




