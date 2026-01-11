package org.example.finalproject.demos.controller;

import org.example.finalproject.demos.dto.ChatSendRequest;
import org.example.finalproject.demos.pojo.ChatMessage;
import org.example.finalproject.demos.service.ChatService;
import org.example.finalproject.demos.util.SecurityUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import java.security.Principal;

//发送信息
//发送信息的WebSocket控制器
@Controller
public class ChatWsController {

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @Autowired
    private ChatService chatService;

    @Autowired
    private SecurityUtil securityUtil;

//    处理来自客户端发送到 /chat.send 路径的消息
    @MessageMapping("/chat.send")
//    ChatSendRequest request接收客户端发送的聊天消息请求对象，包含了要发送的内容等信息
//    Principal principal表示当前认证用户的身份信息，可用于获取用户名等属性
    public void send(ChatSendRequest request, Principal principal) {
//        从安全工具类中获取当前登录用户的ID
        Long fromUserId = securityUtil.getCurrentUserId();
//        如果无法通过安全工具类获取用户ID，则尝试从 Principal 对象中获取用户名，并通过用户名获取用户ID
        if (fromUserId == null && principal != null) {

            fromUserId = securityUtil.getUserIdByUsername(principal.getName());
        }
//        如果仍然无法获取用户ID，则抛出异常，表示用户未登录，无法发送消息
        if (fromUserId == null) {
            throw new IllegalArgumentException("用户未登录，无法发送消息");
        }
        // 1校验会话参与者并保存消息+更新未读
        ChatMessage saved = chatService.saveMessage(request, fromUserId);

        // 2会话内广播，前端根据 cid 刷新
//        使用messagingTemplate将消息广播到特定话题（Topic），该话题对应于当前会话（由conversationId标识）
        messagingTemplate.convertAndSend("/topic/chat." + saved.getConversationId(), saved);
        // 3定向发送给接收方，便于做全局未读提醒
//        将消息单独推送给目标用户（即消息的接收者）
        messagingTemplate.convertAndSendToUser(
                String.valueOf(saved.getToUserId()),
                "/queue/chat",
                saved
        );
    }
}


