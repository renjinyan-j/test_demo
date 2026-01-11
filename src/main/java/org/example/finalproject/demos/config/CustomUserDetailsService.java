package org.example.finalproject.demos.config;

import org.example.finalproject.demos.pojo.Permission;
import org.example.finalproject.demos.pojo.Role;
import org.example.finalproject.demos.pojo.User;
import org.example.finalproject.demos.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;


import java.util.ArrayList;
import java.util.List;

//从数据库加载用户信息与角色权限
@Component
public class CustomUserDetailsService implements UserDetailsService {
    @Lazy
    @Autowired
    private UserService userService;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userService.findByUsername(username);
        if (user == null) {
            throw new UsernameNotFoundException("用户名不存在");
        }
        User fullUser = userService.findUserWithRoles(user.getId());
        List<GrantedAuthority> authList = new ArrayList<>();
        if (fullUser.getRoles() != null) {
            for (Role role : fullUser.getRoles()) {
                authList.add(new SimpleGrantedAuthority(role.getRoleCode()));

                if (role.getPermissions() != null) {
                    for (Permission p : role.getPermissions()) {
                        authList.add(new SimpleGrantedAuthority(p.getPermissionCode()));
                    }
                }
            }
        }
        return new org.springframework.security.core.userdetails.User(
                fullUser.getUsername(),
                fullUser.getPassword(),
                fullUser.getStatus() == 1,
                true,
                true,
                true,
                authList
        );
    }
}
