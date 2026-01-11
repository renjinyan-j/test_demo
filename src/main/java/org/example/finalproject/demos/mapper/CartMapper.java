package org.example.finalproject.demos.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Delete;
import org.example.finalproject.demos.pojo.Cart;

import java.util.List;

@Mapper
public interface CartMapper {
    /**
     * 1. 根据用户ID和商品ID查询购物车中是否存在该商品项
     * @param userId 用户ID
     * @param productId 商品ID
     * @return 购物车项（如果存在）或 null
     */
    Cart selectByUserIdAndProductId(@Param("userId") Integer userId,
                                    @Param("productId") Integer productId);

    /**
     * 2. 插入新的购物车项
     * @param cart 要插入的 Cart 实体
     * @return 影响的行数 (通常为 1)
     */
    int insert(Cart cart);

    /**
     * 3. 更新现有购物车项的数量和更新时间
     * @param cart 要更新的 Cart 实体 (需要包含 cartId, quantity, updatedAt)
     * @return 影响的行数 (通常为 1)
     */
    int updateQuantity(Cart cart);

    /**
     * 4. 根据用户ID和商品ID删除购物车项
     * @param userId 用户ID
     * @param productId 商品ID
     * @return 影响的行数
     */
    int deleteByUserIdAndProductId(@Param("userId") Integer userId,
                                   @Param("productId") Integer productId);

    /**
     * 根据用户ID清空购物车
     */
    @Delete("DELETE FROM cart WHERE user_id = #{userId}")
    int deleteByUserId(@Param("userId") Integer userId);

    /**
     * 5. 查询用户所有购物车项，用于展示购物车页面
     * @param userId 用户ID
     * @return 购物车项列表
     */
    List<Cart> selectCartsByUserId(Integer userId);
}
