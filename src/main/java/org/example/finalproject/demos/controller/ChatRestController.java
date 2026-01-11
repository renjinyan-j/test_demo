package org.example.finalproject.demos.controller;

import org.example.finalproject.demos.pojo.ChatMessage;
import org.example.finalproject.demos.pojo.Conversation;
import org.example.finalproject.demos.service.ChatService;
import org.example.finalproject.demos.util.SecurityUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

//提供 REST API，主要负责 会话创建、列表和历史消息
@RestController
@RequestMapping("/api/chat")
public class ChatRestController {

    @Autowired
    private ChatService chatService;

    @Autowired
    private SecurityUtil securityUtil;

    @PostMapping("/conversation")
    public Conversation createConversation(@RequestParam("goodsId") Long goodsId) {
//       从安全工具类中获取当前登录用户的ID
        Long buyerId = securityUtil.getCurrentUserId();
        // 创建或复用同 buyer/seller/goods 的会话
        return chatService.findOrCreateConversation(goodsId, buyerId);
    }

    //加载当前用户参与的所有会话，并展示未读数
    @GetMapping("/conversations/mine")
    public List<Conversation> listMyConversations() {
        Long uid = securityUtil.getCurrentUserId();
        // 买家或卖家自己的所有会话
        return chatService.listByUser(uid);
    }
    //    Spring MVC控制器中的方法，用于处理HTTP GET请求并返回聊天消息列表
    @GetMapping("/conversation/{id}/messages")
//    从URL路径中提取会话ID，并将其作为参数传递给方法
    public List<ChatMessage> listMessages(@PathVariable("id") Long conversationId,
//                                          获取查询参数 offset，如果未提供则默认为0， offset，如果未提供则默认为0。通常
                                          @RequestParam(value = "offset", defaultValue = "0") int offset,
//取查询参数 limit，如果未提供则默认为50。表示每次最多返回多少条数据
                                          @RequestParam(value = "limit", defaultValue = "50") int limit) {
//        调用安全工具类的方法来获取当前登录用户的ID
        Long currentUserId = securityUtil.getCurrentUserId();
        // 仅会话双方可拉取历史消息
        return chatService.listMessages(conversationId, offset, limit, currentUserId);
    }

    @PostMapping("/conversation/{id}/read")
    public void markRead(@PathVariable("id") Long conversationId) {
        Long userId = securityUtil.getCurrentUserId();
        // 清除当前用户侧未读计数
        chatService.markRead(conversationId, userId);
    }
}


