package org.example.finalproject.demos.controller;

import org.example.finalproject.demos.pojo.Conversation;
import org.example.finalproject.demos.service.ChatService;
import org.example.finalproject.demos.util.SecurityUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

//会话聊天页面控制器
@Controller
public class ChatPageController {

    @Autowired
    private ChatService chatService;

    @Autowired
    private SecurityUtil securityUtil;

//    用于处理进入聊天页面的HTTP GET请求
    @GetMapping("/front/chat")
//从请求参数中提取名为 cid 的值，并转换为 Long 类型的 conversationId 变量
    public String chatPage(@RequestParam("cid") Long conversationId, Model model) {
//        获取当前用户ID
//        调用安全工具类的方法来获取当前登录用户的ID
        Long currentUserId = securityUtil.getCurrentUserId();
//        通过会话ID从服务层获取对应的会话记录
//        检查会话是否存在以及当前用户是否属于该会话的一方（买家或卖家）。如果不满足条件，则重定向到登录页面
        Conversation conversation = chatService.getConversation(conversationId);
        if (conversation == null
                || (!conversation.getBuyerId().equals(currentUserId)
                && !conversation.getSellerId().equals(currentUserId))) {
            return "redirect:/toLoginPage";
        }
//       计算对方用户ID
//        根据当前用户的身份判断出另一方的用户ID（即聊天对象）
        Long peerId = conversation.getBuyerId().equals(currentUserId)
                ? conversation.getSellerId() : conversation.getBuyerId();
//        将会话ID、当前用户ID和对方用户ID添加到模型中，供前端模板使用
        model.addAttribute("cid", conversationId);
        model.addAttribute("currentUserId", currentUserId);
        model.addAttribute("peerUserId", peerId);
        return "front/chat";
    }
}


