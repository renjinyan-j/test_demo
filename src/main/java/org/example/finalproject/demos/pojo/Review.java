package org.example.finalproject.demos.pojo;

import lombok.Data;
import java.util.Date;

/**
 * 评价实体类
 */
@Data
public class Review {
    private Integer id;
    private Integer orderId;
    private Integer userId;
    private Integer productId;
    private Integer sellerId;
    private Integer rating; // 1-5星
    private String content;
    private String images; // 多个用逗号分隔
    private Integer status; // 0-待审核，1-已审核，2-已屏蔽
    private String replyContent;
    private Date replyTime;
    private Date createTime;
    private Date updateTime;
    
    // 关联的用户和商品信息（可选）
    private User user;
    private Products product;
}
