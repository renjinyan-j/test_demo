package org.example.finalproject.demos.pojo;

import lombok.Data;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 订单项实体类
 */
@Data
public class OrderItem {
    private Integer id;
    private Integer orderId;
    private Integer productId;
    private String productName;
    private String productImage;
    private BigDecimal price;
    private Integer quantity;
    private BigDecimal subtotal;
    private Date createTime;
}
