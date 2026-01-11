package org.example.finalproject.demos.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class HelloController {
    @RequestMapping("/hello")
    public String Hello(Model model) {
        model.addAttribute("msg", "Spring 安全管理");
        return "denglu/Hello";
    }
}
