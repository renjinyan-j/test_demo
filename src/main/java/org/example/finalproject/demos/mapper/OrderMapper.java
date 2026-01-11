package org.example.finalproject.demos.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.example.finalproject.demos.pojo.Order;

import java.util.List;

@Mapper
public interface OrderMapper {
    /**
     * 插入订单
     */
    int insert(Order order);

    /**
     * 根据ID查询订单
     */
    Order selectById(@Param("id") Integer id);

    /**
     * 根据订单号查询订单
     */
    Order selectByOrderNo(@Param("orderNo") String orderNo);

    /**
     * 根据用户ID和状态查询订单列表
     */
    List<Order> selectByUserIdAndStatus(@Param("userId") Integer userId, 
                                        @Param("status") Integer status);

    /**
     * 根据用户ID查询所有订单
     */
    List<Order> selectByUserId(@Param("userId") Integer userId);

    /**
     * 更新订单
     */
    int updateById(Order order);

    /**
     * 查询已完成但未评价的订单（用于待评价列表）
     */
    List<Order> selectCompletedOrdersWithoutReview(@Param("userId") Integer userId);

    /**
     * 根据卖家ID查询订单列表
     */
    List<Order> selectBySellerId(@Param("sellerId") Integer sellerId);

    /**
     * 支付成功时更新订单状态与支付信息
     */
    int updatePaySuccess(@Param("orderNo") String orderNo,
                         @Param("paymentMethod") String paymentMethod,
                         @Param("paymentTime") java.util.Date paymentTime,
                         @Param("updateTime") java.util.Date updateTime);

    /**
     * 查询所有订单（管理员用）
     */
    List<Order> selectAll();
}
