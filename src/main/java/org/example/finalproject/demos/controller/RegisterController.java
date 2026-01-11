package org.example.finalproject.demos.controller;

import org.example.finalproject.demos.mapper.RoleMapper;
import org.example.finalproject.demos.pojo.Role;
import org.example.finalproject.demos.pojo.User;
import org.example.finalproject.demos.pojo.VerifyCodeUtil;
import org.example.finalproject.demos.service.UserService;
import org.example.finalproject.demos.service.impl.MailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
// 注册控制器（处理用户注册、验证码验证、角色选择）
@Controller
public class RegisterController {
    @Autowired
    UserService userService;
    @Autowired
    MailService mailService;
    @Autowired
    RoleMapper roleMapper;

    @GetMapping("/register")
    public String register(Model model) {
        model.addAttribute("user", new User());
        
        // 获取可注册的角色（买家和卖家，不包括管理员）
        List<Role> availableRoles = new ArrayList<>();
        Role buyerRole = roleMapper.selectByRoleCode("ROLE_BUYER");
        Role sellerRole = roleMapper.selectByRoleCode("ROLE_SELLER");
        // 兼容旧的ROLE_USER
        Role userRole = roleMapper.selectByRoleCode("ROLE_USER");
        
        if (buyerRole != null) {
            availableRoles.add(buyerRole);
        } else if (userRole != null) {
            // 如果没有ROLE_BUYER，使用ROLE_USER作为买家
            availableRoles.add(userRole);
        }
        
        if (sellerRole != null) {
            availableRoles.add(sellerRole);
        }
        
        model.addAttribute("availableRoles", availableRoles);
        return "denglu/register";
    }

//    发送验证码到邮箱
    @PostMapping("/sendCode")
    @ResponseBody
    public String sendCode(String email, HttpSession session) {

        if (userService.existsByEmail(email)) {
            return "邮箱已注册";
        }

//        调用VerifyCodeUtil工具类的静态方法generateCode()来生成一个随机的验证码字符串
        String code = VerifyCodeUtil.generateCode();
        VerifyCodeUtil.storeCode(session, email, code);

        mailService.sendVerifyCode(email, code);

        return "success";
    }

//    提交固定表单
    @PostMapping("/doRegister")
    public String doRegister(User user, String verifyCode, String roleCode,
                             HttpSession session, RedirectAttributes redirect) {

//        验证验证码是否正确
        if (!VerifyCodeUtil.validateCode(session, user.getEmail(), verifyCode)) {
            redirect.addFlashAttribute("error", "验证码错误或已过期");
            return "redirect:/register";
        }
//        验证用户名是否已存在
        if (userService.findByUsername(user.getUsername()) != null) {
            redirect.addFlashAttribute("error", "用户名已存在");
            return "redirect:/register";
        }

        // 验证角色代码（只允许买家或卖家，不允许管理员）
        if (roleCode == null || roleCode.trim().isEmpty()) {
            redirect.addFlashAttribute("error", "请选择注册类型");
            return "redirect:/register";
        }

        // 防止通过注册页面创建管理员
        if ("ROLE_ADMIN".equals(roleCode)) {
            redirect.addFlashAttribute("error", "管理员账号不能通过注册页面创建");
            return "redirect:/register";
        }

        // 根据角色代码获取角色ID
        Role selectedRole = roleMapper.selectByRoleCode(roleCode);
        if (selectedRole == null) {
            redirect.addFlashAttribute("error", "选择的角色不存在");
            return "redirect:/register";
        }

        // 只允许买家或卖家注册
        if (!"ROLE_BUYER".equals(roleCode) && !"ROLE_SELLER".equals(roleCode) && !"ROLE_USER".equals(roleCode)) {
            redirect.addFlashAttribute("error", "只能注册为买家或卖家");
            return "redirect:/register";
        }

        user.setStatus(1);

        // 使用选择的角色ID
        userService.add(user, Collections.singletonList(selectedRole.getId()));

        redirect.addFlashAttribute("msg", "注册成功，请登录");
        return "redirect:/toLoginPage";
    }
}
