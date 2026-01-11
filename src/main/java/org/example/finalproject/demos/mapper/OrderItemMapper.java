package org.example.finalproject.demos.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.example.finalproject.demos.pojo.OrderItem;

import java.util.List;

@Mapper
public interface OrderItemMapper {
    /**
     * 批量插入订单项
     */
    int insertBatch(List<OrderItem> items);

    /**
     * 根据订单ID查询订单项列表
     */
    List<OrderItem> selectByOrderId(@Param("orderId") Integer orderId);

    /**
     * 根据ID查询订单项
     */
    OrderItem selectById(@Param("id") Integer id);
}
