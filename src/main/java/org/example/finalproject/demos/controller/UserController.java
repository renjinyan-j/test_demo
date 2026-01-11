package org.example.finalproject.demos.controller;


import org.example.finalproject.demos.mapper.RoleMapper;
import org.example.finalproject.demos.pojo.Role;
import org.example.finalproject.demos.pojo.User;
import org.example.finalproject.demos.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/users")
@PreAuthorize("hasRole('ADMIN')")
public class UserController {

    @Autowired
    private UserService userService;
    @Autowired
    private RoleMapper roleMapper;

    // 用户列表页（需user:view权限）
    @GetMapping
//    @PreAuthorize("hasAuthority('user:view')")
    public String list(Model model) {
        List<User> users = userService.findAll();
        model.addAttribute("users", users);
        return "user/list";
    }

    // 跳转新增页（需user:add权限）
    @GetMapping("/add")
//    @PreAuthorize("hasAuthority('user:add')")
    public String toAdd(Model model) {
        model.addAttribute("user", new User()); // 空用户对象
        model.addAttribute("allRoles", roleMapper.selectAll()); // 所有角色
        return "user/form";
    }

    // 跳转编辑页（需user:edit权限）
    @GetMapping("/edit/{id}")
//    @PreAuthorize("hasAuthority('user:edit')")
    public String toEdit(@PathVariable Long id, Model model) {
        User user = userService.findById(id);
        // 获取用户已关联的角色ID
        User userWithRoles = userService.findUserWithRoles(id);
        List<Long> userRoleIds = userWithRoles.getRoles().stream()
                .map(Role::getId)
                .collect(Collectors.toList());
        model.addAttribute("user", user);
        model.addAttribute("allRoles", roleMapper.selectAll()); // 所有角色
        model.addAttribute("userRoleIds", userRoleIds); // 用户已选角色ID
        return "user/form";
    }

    // 保存用户（新增/编辑）
    @PostMapping("/save")
//    @PreAuthorize("hasAnyAuthority('user:add', 'user:edit')")
    public String save(
            User user,
            @RequestParam(required = false, name = "roleIds") List<Long> roleIds, // 角色ID列表
            RedirectAttributes redirect
    ) {
        try {
            if (user.getId() == null) {
                // 新增
                userService.add(user, roleIds);
                redirect.addFlashAttribute("msg", "新增用户成功");
            } else {
                // 编辑
                userService.update(user, roleIds);
                redirect.addFlashAttribute("msg", "更新用户成功");
            }
        } catch (Exception e) {
            redirect.addFlashAttribute("error", "操作失败：" + e.getMessage());
            return "redirect:/users";
        }
        return "redirect:/users";
    }

    // 删除用户（需user:delete权限）
    @GetMapping("/delete/{id}")
//    @PreAuthorize("hasAuthority('user:delete')")
    public String delete(@PathVariable Long id, RedirectAttributes redirect) {
        try {
            userService.delete(id);
            redirect.addFlashAttribute("msg", "删除用户成功");
        } catch (Exception e) {
            redirect.addFlashAttribute("error", "删除失败：" + e.getMessage());
        }
        return "redirect:/users";
    }
}
