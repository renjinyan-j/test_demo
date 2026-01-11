package org.example.finalproject.demos.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class LoginController {
    @GetMapping("/toLoginPage")
    public String toLogin() {
        return "denglu/login";
    }

    @GetMapping("/")
    public String home() {
        return "index"; // 若没有 index，可改为列表页
    }
}
