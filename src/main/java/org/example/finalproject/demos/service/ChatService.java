package org.example.finalproject.demos.service;

import org.example.finalproject.demos.dto.ChatSendRequest;
import org.example.finalproject.demos.pojo.ChatMessage;
import org.example.finalproject.demos.pojo.Conversation;

import java.util.List;

public interface ChatService {
    Conversation findOrCreateConversation(Long goodsId, Long buyerId);

    List<ChatMessage> listMessages(Long conversationId, int offset, int limit, Long currentUserId);

    ChatMessage saveMessage(ChatSendRequest request, Long fromUserId);

    void markRead(Long conversationId, Long userId);

    List<Conversation> listByUser(Long userId);

    Conversation getConversation(Long conversationId);
}


