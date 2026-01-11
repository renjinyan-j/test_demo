package org.example.finalproject.demos.service;

import org.example.finalproject.demos.pojo.Order;
import java.util.List;

/**
 * 订单服务接口
 */
public interface OrderService {
    /**
     * 获取未支付订单列表
     */
    List<Order> getUnpaidOrders(Integer userId);

    /**
     * 获取已支付订单列表
     */
    List<Order> getPaidOrders(Integer userId);

    /**
     * 获取已完成订单列表
     */
    List<Order> getCompletedOrders(Integer userId);

    /**
     * 获取待评价订单列表（已完成但未评价）
     */
    List<Order> getPendingReviewOrders(Integer userId);

    /**
     * 根据ID获取订单详情（包含订单项）
     */
    Order getOrderDetail(Integer orderId, Integer userId);

    /**
     * 根据卖家ID获取订单列表
     */
    List<Order> getOrdersBySellerId(Integer sellerId);

    /**
     * 立即购买：为单个商品创建待支付订单
     */
    Order createOrderForProduct(Integer userId, Integer productId, Integer quantity);

    /**
     * 购物车结算：为购物车商品创建待支付订单，并清空购物车
     */
    Order createOrderFromCart(Integer userId);

    /**
     * 获取所有订单（管理员用）
     */
    List<Order> getAllOrders();
}
