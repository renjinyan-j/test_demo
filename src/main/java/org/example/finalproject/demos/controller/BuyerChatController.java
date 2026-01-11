package org.example.finalproject.demos.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class BuyerChatController {

    @GetMapping("/buyer/chat-list")
    public String chatList() {
        return "buyer/chat-list";
    }
}


