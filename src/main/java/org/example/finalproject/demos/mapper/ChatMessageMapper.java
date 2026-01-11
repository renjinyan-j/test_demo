package org.example.finalproject.demos.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.example.finalproject.demos.pojo.ChatMessage;

import java.util.List;

@Mapper
public interface ChatMessageMapper {
    int insertMessage(ChatMessage message);

    List<ChatMessage> listByConversation(@Param("conversationId") Long conversationId,
                                         @Param("offset") int offset,
                                         @Param("limit") int limit);
}


