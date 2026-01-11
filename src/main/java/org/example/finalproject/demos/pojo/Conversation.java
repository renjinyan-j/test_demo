package org.example.finalproject.demos.pojo;

import java.util.Date;

/**
 * 买家与卖家的会话
 */
public class Conversation {
    private Long id;
    private Long goodsId;
    private Long buyerId;
    private Long sellerId;
    private String lastMessage;
    private Date lastMessageAt;
    private Integer unreadForBuyer;
    private Integer unreadForSeller;
    private Date createdAt;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getGoodsId() {
        return goodsId;
    }

    public void setGoodsId(Long goodsId) {
        this.goodsId = goodsId;
    }

    public Long getBuyerId() {
        return buyerId;
    }

    public void setBuyerId(Long buyerId) {
        this.buyerId = buyerId;
    }

    public Long getSellerId() {
        return sellerId;
    }

    public void setSellerId(Long sellerId) {
        this.sellerId = sellerId;
    }

    public String getLastMessage() {
        return lastMessage;
    }

    public void setLastMessage(String lastMessage) {
        this.lastMessage = lastMessage;
    }

    public Date getLastMessageAt() {
        return lastMessageAt;
    }

    public void setLastMessageAt(Date lastMessageAt) {
        this.lastMessageAt = lastMessageAt;
    }

    public Integer getUnreadForBuyer() {
        return unreadForBuyer;
    }

    public void setUnreadForBuyer(Integer unreadForBuyer) {
        this.unreadForBuyer = unreadForBuyer;
    }

    public Integer getUnreadForSeller() {
        return unreadForSeller;
    }

    public void setUnreadForSeller(Integer unreadForSeller) {
        this.unreadForSeller = unreadForSeller;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }
}


