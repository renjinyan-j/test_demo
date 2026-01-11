package org.example.finalproject.demos.pojo;

import lombok.Data;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * 订单实体类
 */
@Data
public class Order {
    private Integer id;
    private String orderNo;
    private Integer userId;
    private Integer sellerId;
    private BigDecimal totalAmount;
    private Integer status; // 0-未支付，1-已支付，2-已完成，3-已取消
    private Integer paymentStatus; // 0-未支付，1-已支付，2-已退款
    private String paymentMethod;
    private Date paymentTime;
    private String deliveryAddress;
    private String contactName;
    private String contactPhone;
    private String remark;
    private Date createTime;
    private Date updateTime;
    private Date completeTime;
    
    // 关联的订单项
    private List<OrderItem> items;
    
    // 关联的用户信息（可选）
    private User user;
    private User seller;
}
