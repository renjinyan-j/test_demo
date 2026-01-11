package org.example.finalproject.demos.service;

import com.github.pagehelper.PageInfo;
import org.example.finalproject.demos.pojo.Cart;
import org.example.finalproject.demos.pojo.ProductWithImg;
import org.example.finalproject.demos.pojo.Products;

import java.util.List;

public interface ProductsService {
//    获取商品列表
    // 获取商品列表（包含图片信息）
    public List<ProductWithImg> getProductsList();

    // 根据ID获取商品详情（包含图片信息）
    public ProductWithImg getProductById(Integer id);

    // 获取前N个商品（包含图片信息）
    public List<ProductWithImg> findTopProducts(int limit);

    PageInfo<ProductWithImg> getProductsPage(int pageNum, int pageSize);

    PageInfo<ProductWithImg> searchProducts(String name, Integer minAmount, Integer maxAmount,int pageNum, int pageSize);

    PageInfo<ProductWithImg> productsCategoryById(Integer categoryId,int pageNum, int pageSize);

    List<ProductWithImg> productDetail(Integer id);
        // 更新商品浏览量
    int updateViewCount(Integer id);

    // 添加商品到购物车
    int addToCart(Integer userId, Integer productId, Integer quantity);

    // 获取用户的购物车详情
    List<Cart> getCartDetails(Integer userId);

    // 更新购物车商品数量
    int updateCartQuantity(Integer userId, Integer productId, Integer quantity);

    // 从购物车删除商品
    int removeFromCart(Integer userId, Integer productId);

    // 清空购物车
    int clearCart(Integer userId);

    // 获取购物车商品总数
    int getCartItemCount(Integer userId);



    // 添加商品（包含图片）
    int addProduct(Products product, List<String> imageUrls);

    // 根据卖家ID查询商品列表
    List<ProductWithImg> getProductsBySellerId(Integer sellerId);

    // 更新商品信息
    int updateProduct(Products product, List<String> imageUrls);

    // 删除商品
    int deleteProduct(Integer productId, Integer sellerId);
}
