package org.example.finalproject.demos.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.example.finalproject.demos.pojo.Conversation;

import java.util.List;

@Mapper
public interface ConversationMapper {
    Conversation findById(Long id);

    Conversation findByGoodsAndUsers(@Param("goodsId") Long goodsId,
                                     @Param("buyerId") Long buyerId,
                                     @Param("sellerId") Long sellerId);

    int insertConversation(Conversation conversation);

    int updateLastMessageAndUnread(@Param("conversationId") Long conversationId,
                                   @Param("lastMessage") String lastMessage,
                                   @Param("unreadForBuyerDelta") int unreadForBuyerDelta,
                                   @Param("unreadForSellerDelta") int unreadForSellerDelta);

    int clearUnreadForBuyer(Long conversationId);

    int clearUnreadForSeller(Long conversationId);

    List<Conversation> listByUserId(Long userId);
}


