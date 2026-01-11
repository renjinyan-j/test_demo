package org.example.finalproject.demos.controller;

import org.example.finalproject.demos.util.SecurityUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class SellerChatController {

    @Autowired
    private SecurityUtil securityUtil;

    @GetMapping("/seller/chat-list")
    public String chatList(Model model) {
        model.addAttribute("sellerId", securityUtil.getCurrentUserId());
        return "seller/chat-list";
    }
}


