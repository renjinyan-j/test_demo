package org.example.finalproject.demos.controller;

import com.github.pagehelper.PageInfo;
import org.example.finalproject.demos.pojo.ProductWithImg;
import org.example.finalproject.demos.service.ProductsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@Controller
public class ProductsController {
//    访问服务层
    @Autowired
    ProductsService productsService;
//    首页设置
    @GetMapping("/ProductsFront")
    public String productsFront(Model model) {
        List<ProductWithImg> recommendedProducts = productsService.findTopProducts(1);
        model.addAttribute("xx", recommendedProducts.get(0));
        model.addAttribute("yy", recommendedProducts.get(1));
        return "front/homepage";
    }
    /**
     * 登录成功后重定向的首页入口（路径与模板路径一致，避免404）
     */
    @GetMapping("/front/homepage")
    public String homepage(Model model) {
        return productsFront(model);
    }
//    二手商品设置
    @GetMapping("/ershouproducts")
//    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public String productsProducts(Model model,@RequestParam(defaultValue = "1") int pageNum,
                                   @RequestParam(defaultValue = "9") int pageSize) {
        model.addAttribute("pageInfo", productsService.getProductsPage(pageNum, pageSize));
        return "front/ershouproducts";
    }
//    商品查询设置
    @GetMapping("/searchProducts")
//    当前后端传递的值为一样时直接写，不一样时要加@RequestParam注解
//    包装类型支持空值，而int类型不支持空值，所以要使用包装类型
    public String searchProducts(Model model,String name,Integer minAmount,Integer maxAmount,@RequestParam(defaultValue = "1") int pageNum,
                                 @RequestParam(defaultValue = "10") int pageSize) {
//        传递给服务层
        PageInfo<ProductWithImg> pageInfo = productsService.searchProducts(name,minAmount,maxAmount,pageNum, pageSize);
//        model.addAttribute表示向前端传递值
        model.addAttribute("pageInfo",pageInfo);
        return "front/ershouproducts";

    }
//    点击商品类别时根据商品类型id显示商品
    @GetMapping("/productsCategory")
    public String productsCategory(Model model,Integer categoryId,@RequestParam(defaultValue = "1") int pageNum,
                                   @RequestParam(defaultValue = "10") int pageSize) {
        PageInfo<ProductWithImg> pageInfo = productsService.productsCategoryById(categoryId,pageNum, pageSize);
        model.addAttribute("pageInfo",pageInfo);
        return "front/ershouproducts";
    }
    //    点击商品详情时根据商品id显示商品详情
    @GetMapping("/productDetail")
    public String productDetail(Model model, Integer id, HttpServletRequest request) {
        // 确保会话已创建，避免模板阶段再去建 session
        request.getSession(true);
        productsService.updateViewCount(id);
        List<ProductWithImg> productDetail = productsService.productDetail(id);
        if (productDetail != null && !productDetail.isEmpty()) {
            model.addAttribute("product", productDetail.get(0));
        }
        return "front/shangpinxiangqing";
    }
}
