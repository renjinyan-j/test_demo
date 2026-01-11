package org.example.finalproject.demos.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.example.finalproject.demos.pojo.Cart;
import org.example.finalproject.demos.pojo.ProductWithImg;
import org.example.finalproject.demos.pojo.Products;

import java.util.List;

@Mapper
public interface ProductsMapper {
//    获取商品列表
// 获取商品列表（包含图片信息）
    public List<ProductWithImg> getProductsList();

    // 根据ID获取商品详情（包含图片信息）
    public ProductWithImg getProductById(Integer id);

    // 获取前N个商品（包含图片信息）
    public List<ProductWithImg> findTopProducts(int limit);

//当存在多个参数时需要弄一个@Param注解
//    每一个都要使用@Param注解
//    根据条件搜索商品
    List<ProductWithImg> searchProducts(@Param("name")String name, @Param("minAmount") Integer minAmount, @Param("maxAmount") Integer maxAmount);

    // 根据商品类别ID获取商品列表（包含图片信息）
    List<ProductWithImg> productsCategoryById(Integer categoryId);

    List<ProductWithImg> productDetail(Integer id);
        // 更新商品浏览量
    int updateViewCount(Integer id);

    
    
    int addToCart(Integer userId, Integer productId, Integer quantity);

    List<Cart> getCartDetails(Integer userId);

    int updateCartQuantity(Integer userId, Integer productId, Integer quantity);

    int removeFromCart(Integer userId, Integer productId);

    int clearCart(Integer userId);

    int getCartItemCount(Integer userId);

    // 插入商品
    int insertProduct(Products product);

    // 根据卖家ID查询商品列表
    List<ProductWithImg> getProductsBySellerId(Integer sellerId);

    // 更新商品信息
    int updateProduct(Products product);

    // 删除商品（软删除或硬删除）
    int deleteProduct(Integer productId);
}