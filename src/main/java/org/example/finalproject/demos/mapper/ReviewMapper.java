package org.example.finalproject.demos.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.example.finalproject.demos.pojo.Review;

import java.util.List;

@Mapper
public interface ReviewMapper {
    /**
     * 插入评价
     */
    int insert(Review review);

    /**
     * 根据ID查询评价
     */
    Review selectById(@Param("id") Integer id);

    /**
     * 根据订单ID查询评价
     */
    Review selectByOrderId(@Param("orderId") Integer orderId);

    /**
     * 根据用户ID查询评价列表
     */
    List<Review> selectByUserId(@Param("userId") Integer userId);

    /**
     * 根据商品ID查询评价列表
     */
    List<Review> selectByProductId(@Param("productId") Integer productId);

    /**
     * 更新评价
     */
    int updateById(Review review);

    /**
     * 查询所有评价（管理员用）
     */
    List<Review> selectAll();
}
