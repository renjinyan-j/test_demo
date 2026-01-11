package org.example.finalproject.demos.service.impl;

import org.example.finalproject.demos.mapper.CartMapper;
import org.example.finalproject.demos.mapper.ProductsMapper;
import org.example.finalproject.demos.pojo.Cart;
import org.example.finalproject.demos.pojo.ProductWithImg;
import org.example.finalproject.demos.service.CartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class CartServiceImpl implements CartService {
    @Autowired
    private CartMapper cartMapper;

    @Autowired
    private ProductsMapper productsMapper; // 用于获取商品的详细信息

    /**
     * 实现 CartService 接口中的方法
     */
    @Override
    public void addItemToCart(Integer userId, Integer productId, Integer quantity) {
        // 1. 检查购物车中是否已存在该商品
        Cart existingCartItem = cartMapper.selectByUserIdAndProductId(userId, productId);

        if (existingCartItem != null) {
            // --- 场景 A: 购物车中已存在该商品 ---
            existingCartItem.setQuantity(existingCartItem.getQuantity() + quantity);
            existingCartItem.setUpdatedAt(new Date());
            cartMapper.updateQuantity(existingCartItem);
        } else {
            // --- 场景 B: 购物车中不存在该商品，需要新增 ---
            // 2. 查询商品详细信息
            List<ProductWithImg> productDetails = productsMapper.productDetail(productId);

            if (productDetails == null || productDetails.isEmpty()) {
                throw new RuntimeException("商品ID: " + productId + " 不存在！");
            }

            ProductWithImg product = productDetails.get(0);

            // 3. 构建新的 Cart 实体
            Cart newCartItem = new Cart();
            newCartItem.setUserId(userId);
            newCartItem.setProductId(productId);
            newCartItem.setQuantity(quantity);

            // 填充冗余信息
            newCartItem.setProductName(product.getTitle());
            newCartItem.setPrice(product.getPrice());

            // 设置图片URL（取第一张图片）
            if (product.getImages() != null && !product.getImages().isEmpty()) {
                newCartItem.setImageUrl(product.getImages().get(0).getUrl());
            }

            newCartItem.setCreatedAt(new Date());
            newCartItem.setUpdatedAt(new Date());

            // 4. 插入数据库
            cartMapper.insert(newCartItem);
        }
    }
    @Override
    public List<Cart> getCartItems(Integer userId) {
        return cartMapper.selectCartsByUserId(userId);
    }

    @Override
    public void updateCartQuantity(Integer userId, Integer productId, Integer quantity) {
        // 先查询购物车项
        Cart cartItem = cartMapper.selectByUserIdAndProductId(userId, productId);
        if (cartItem != null) {
            // 更新数量和时间
            cartItem.setQuantity(quantity);
            cartItem.setUpdatedAt(new Date());
            cartMapper.updateQuantity(cartItem);
        }
    }

    @Override
    public void removeFromCart(Integer userId, Integer productId) {
        cartMapper.deleteByUserIdAndProductId(userId, productId);
    }

    @Override
    public void clearCart(Integer userId) {
        cartMapper.deleteByUserId(userId);
    }
}
