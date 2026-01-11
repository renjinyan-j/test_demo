package org.example.finalproject.demos.service.impl;

import org.example.finalproject.demos.dto.ChatSendRequest;
import org.example.finalproject.demos.mapper.ChatMessageMapper;
import org.example.finalproject.demos.mapper.ConversationMapper;
import org.example.finalproject.demos.pojo.ChatMessage;
import org.example.finalproject.demos.pojo.Conversation;
import org.example.finalproject.demos.pojo.ProductWithImg;
import org.example.finalproject.demos.service.ChatService;
import org.example.finalproject.demos.service.ProductsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Date;
import java.util.List;

@Service
public class ChatServiceImpl implements ChatService {

    @Autowired
    private ConversationMapper conversationMapper;

    @Autowired
    private ChatMessageMapper chatMessageMapper;

    @Autowired
    private ProductsService productsService;

    @Override
//    会话模型：一件商品对应一条买卖双方会话
    public Conversation findOrCreateConversation(Long goodsId, Long buyerId) {
//        检查商品是否存在及获取卖家ID
        ProductWithImg product = productsService.getProductById(goodsId.intValue());
//        检查商品和卖家信息的有效性
        if (product == null || product.getUserId() == null) {
            throw new IllegalArgumentException("商品不存在或缺少卖家信息");
        }
        Long sellerId = product.getUserId().longValue();
//        检查是否存在已存在的会话
        Conversation existing = conversationMapper.findByGoodsAndUsers(goodsId, buyerId, sellerId);
        if (existing != null) {
            return existing;
        }

        // 不存在则创建新会话，未读初始化为0
        Conversation conversation = new Conversation();
        conversation.setGoodsId(goodsId);
        conversation.setBuyerId(buyerId);
        conversation.setSellerId(sellerId);
        conversation.setCreatedAt(new Date());
        conversation.setUnreadForBuyer(0);
        conversation.setUnreadForSeller(0);
        conversationMapper.insertConversation(conversation);
        return conversation;
    }

    @Override
    public List<ChatMessage> listMessages(Long conversationId, int offset, int limit, Long currentUserId) {
        Conversation conversation = conversationMapper.findById(conversationId);
        validateParticipant(conversation, currentUserId);
        return chatMessageMapper.listByConversation(conversationId, offset, limit);
    }

//    负责落库和未读数更新
    @Override
//    ChatSendRequest request包含客户端发送的聊天请求信息的对象
//    Long fromUserId表示发送消息的用户ID
    public ChatMessage saveMessage(ChatSendRequest request, Long fromUserId) {
//        检查请求对象和会话ID是否存在，若不存在则抛出异常
        if (request == null || request.getConversationId() == null) {
            throw new IllegalArgumentException("缺少会话信息");
        }
        String content = request.getContent();
        if (!StringUtils.hasText(content)) {
            throw new IllegalArgumentException("消息内容不能为空");
        }
//通过会话ID从数据库中查找对应的会话记录
        Conversation conversation = conversationMapper.findById(request.getConversationId());
//        调用validateParticipant方法验证当前用户是否属于该会话的参与者之一
        validateParticipant(conversation, fromUserId);

        // 计算接收方：如果发送者是买家，则接收方是卖家，反之亦然
        Long toUserId = fromUserId.equals(conversation.getBuyerId()) ?
                conversation.getSellerId() : conversation.getBuyerId();

//      构造一个新的ChatMessage对象，并设置各种属性（如会话ID、发送者ID、接收者ID、内容、时间戳等）
        ChatMessage message = new ChatMessage();
        message.setConversationId(conversation.getId());
        message.setFromUserId(fromUserId);
        message.setToUserId(toUserId);
        message.setContent(content.trim());
        message.setSentAt(new Date());
        message.setReadFlag(0);
        chatMessageMapper.insertMessage(message);

        // 更新会话的未读计数与最后一条消息
        int buyerDelta = 0;
        int sellerDelta = 0;
        if (toUserId.equals(conversation.getBuyerId())) {
            buyerDelta = 1;
        } else {
            sellerDelta = 1;
        }
        conversationMapper.updateLastMessageAndUnread(
                conversation.getId(),
                message.getContent(),
                buyerDelta,
                sellerDelta
        );
        return message;
    }

    @Override
    public void markRead(Long conversationId, Long userId) {
        Conversation conversation = conversationMapper.findById(conversationId);
        validateParticipant(conversation, userId);
        if (userId.equals(conversation.getBuyerId())) {
            conversationMapper.clearUnreadForBuyer(conversationId);
        } else {
            conversationMapper.clearUnreadForSeller(conversationId);
        }
    }

    @Override
    public Conversation getConversation(Long conversationId) {
        return conversationMapper.findById(conversationId);
    }

    @Override
    public List<Conversation> listByUser(Long userId) {
        return conversationMapper.listByUserId(userId);
    }

    private void validateParticipant(Conversation conversation, Long userId) {
        if (conversation == null) {
            throw new IllegalArgumentException("会话不存在");
        }
        if (userId == null ||
                (!userId.equals(conversation.getBuyerId()) && !userId.equals(conversation.getSellerId()))) {
            throw new IllegalArgumentException("无权访问该会话");
        }
    }
}


