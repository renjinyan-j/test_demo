package org.example.finalproject.demos.service;

import org.example.finalproject.demos.pojo.Cart;

import java.util.List;

public interface CartService {
    /**
     * 将商品加入或更新购物车中的数量
     *
     * @param userId 登录用户的ID
     * @param productId 要添加的商品ID
     * @param quantity 购买数量
     */
    void addItemToCart(Integer userId, Integer productId, Integer quantity);

    // 获取购物车列表
    List<Cart> getCartItems(Integer userId);

    // 更新购物车商品数量
    void updateCartQuantity(Integer userId, Integer productId, Integer quantity);

    // 从购物车删除商品
    void removeFromCart(Integer userId, Integer productId);

    // 清空购物车
    void clearCart(Integer userId);
}
