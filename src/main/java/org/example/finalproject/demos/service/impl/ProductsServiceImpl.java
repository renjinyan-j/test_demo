package org.example.finalproject.demos.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.example.finalproject.demos.mapper.ImagesMapper;
import org.example.finalproject.demos.mapper.ProductsMapper;
import org.example.finalproject.demos.pojo.Cart;
import org.example.finalproject.demos.pojo.Images;
import org.example.finalproject.demos.pojo.ProductWithImg;
import org.example.finalproject.demos.pojo.Products;
import org.example.finalproject.demos.service.ProductsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductsServiceImpl implements ProductsService {
//访问的是持久层
    @Autowired
    ProductsMapper productsMapper;

    @Autowired
    ImagesMapper imagesMapper;


//    实现类实现所有的接口方法

    @Override
    public List<ProductWithImg> getProductsList() {
        return productsMapper.getProductsList();
    }

    @Override
    public ProductWithImg getProductById(Integer id) {
        return productsMapper.getProductById(id);
    }

    @Override
    public List<ProductWithImg> findTopProducts(int limit) {
        return productsMapper.findTopProducts(limit);
    }

    @Override
    public PageInfo<ProductWithImg> getProductsPage(int pageNum, int pageSize) {
        PageHelper.startPage(pageNum,pageSize);
        List<ProductWithImg> products = productsMapper.getProductsList();
        return new PageInfo<>(products);
    }


    @Override
    public PageInfo<ProductWithImg> searchProducts(String name, Integer minAmount, Integer maxAmount,int pageNum, int pageSize) {
        PageHelper.startPage(pageNum,pageSize);
        List<ProductWithImg> products = productsMapper.searchProducts(name,minAmount,maxAmount);
        return new PageInfo<>(products);
    }

    @Override
    public PageInfo<ProductWithImg> productsCategoryById(Integer categoryId,int pageNum, int pageSize) {
        PageHelper.startPage(pageNum,pageSize);
        List<ProductWithImg> products = productsMapper.productsCategoryById(categoryId);
        return new PageInfo<>(products);
    }

    @Override
    public List<ProductWithImg> productDetail(Integer id) {
        return productsMapper.productDetail(id);
    }

    @Override
    public int updateViewCount(Integer id) {
        return productsMapper.updateViewCount(id);
    }

    // 购物车相关方法的实现
    @Override
    public int addToCart(Integer userId, Integer productId, Integer quantity) {
        return productsMapper.addToCart(userId, productId, quantity);
    }
    @Override
    public List<Cart> getCartDetails(Integer userId) {
        return productsMapper.getCartDetails(userId);
    }
    @Override
    public int updateCartQuantity(Integer userId, Integer productId, Integer quantity) {
        return productsMapper.updateCartQuantity(userId, productId, quantity);
    }
    @Override
    public int removeFromCart(Integer userId, Integer productId) {
        return productsMapper.removeFromCart(userId, productId);
    }
    @Override
    public int clearCart(Integer userId) {
        return productsMapper.clearCart(userId);
    }

    @Override
    public int getCartItemCount(Integer userId) {
        return productsMapper.getCartItemCount(userId);
    }

    @Override
    public int addProduct(Products product, List<String> imageUrls) {
        // 1. 插入商品
        int result = productsMapper.insertProduct(product);

        // 2. 插入图片
        if (result > 0 && product.getId() != null && imageUrls != null) {
            for (String url : imageUrls) {
                Images image = new Images();
                image.setProductId(product.getId());
                image.setUrl(url);
                imagesMapper.insertImage(image);
            }
        }

        return result;
    }

    @Override
    public List<ProductWithImg> getProductsBySellerId(Integer sellerId) {
        return productsMapper.getProductsBySellerId(sellerId);
    }

    @Override
    public int updateProduct(Products product, List<String> imageUrls) {
        // 1. 更新商品信息
        int result = productsMapper.updateProduct(product);

        // 2. 删除旧图片
        if (result > 0 && product.getId() != null) {
            imagesMapper.deleteImagesByProductId(product.getId());

            // 3. 插入新图片
            if (imageUrls != null && !imageUrls.isEmpty()) {
                for (String url : imageUrls) {
                    Images image = new Images();
                    image.setProductId(product.getId());
                    image.setUrl(url);
                    imagesMapper.insertImage(image);
                }
            }
        }

        return result;
    }

    @Override
    public int deleteProduct(Integer productId, Integer sellerId) {
        // 先删除图片
        imagesMapper.deleteImagesByProductId(productId);
        // 再删除商品
        return productsMapper.deleteProduct(productId);
    }
}
