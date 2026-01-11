package org.example.finalproject.demos.controller;

import org.example.finalproject.demos.pojo.Cart;
import org.example.finalproject.demos.pojo.User;
import org.example.finalproject.demos.service.CartService;
import org.example.finalproject.demos.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;
import java.math.BigDecimal;
import java.util.List;

@Controller
public class CartController {
    @Autowired
    CartService cartService;
    @Autowired
    UserService userService;
    // 从Spring Security获取当前登录用户ID
    private Long getCurrentUserId(HttpSession session) {
        // 从Spring Security的SecurityContext中获取当前认证信息
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        
        // 检查是否已认证（用户已登录）
        if (authentication == null || !authentication.isAuthenticated() 
                || "anonymousUser".equals(authentication.getPrincipal().toString())) {
            return null; // 用户未登录
        }
        
        // 获取当前登录用户的用户名
        String username = authentication.getName();
        
        // 通过用户名查询用户信息，获取用户ID
        User user = userService.findByUsername(username);
        if (user != null) {
            return user.getId();
        }
        
        // 如果查询不到用户，返回null
        return null;
    }

    /**
     * 展示购物车页面
     * URL: /cart
     * Method: GET
     */
    @GetMapping("/cart")
    public String showCart(HttpSession session, Model model) {
        Long userId = getCurrentUserId(session);

        if (userId == null) {
            // 用户未登录，重定向到登录页面
            return "redirect:/toLoginPage";
        }

        // 获取用户的购物车项
        List<Cart> cartItems = cartService.getCartItems(userId.intValue());
        model.addAttribute("cartItems", cartItems);
        
        // 计算购物车总价
        BigDecimal totalAmount = cartItems.stream()
                .map(Cart::getTotalPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        model.addAttribute("totalAmount", totalAmount);

        return "front/gouwuche"; // 返回购物车页面模板
    }

    /**
     * 处理加入购物车的请求
     * URL: /addToCart
     * Method: POST
     * Parameters: productId, quantity
     */
    @PostMapping("/addToCart")
    @ResponseBody
    public String addToCart(
            @RequestParam("productId") Long productId,
            @RequestParam("quantity") Integer quantity,
            HttpSession session) {

        Long userId = getCurrentUserId(session);

        if (userId == null) {
            return "user_not_logged_in"; // 返回未登录提示
        }

        if (productId == null || quantity == null || quantity <= 0) {
            return "invalid_parameters"; // 参数错误
        }

        try {
            // 核心业务逻辑：调用 Service 层方法处理加入购物车
            cartService.addItemToCart(userId.intValue(), productId.intValue(), quantity);
            return "success"; // 成功加入购物车
        } catch (Exception e) {
            // 记录错误日志
            e.printStackTrace();
            return "server_error: " + e.getMessage(); // 返回具体错误信息
        }
    }
    /**
     * 更新购物车商品数量
     * URL: /updateCart
     * Method: POST
     * Parameters: productId, quantity
     */
    @PostMapping("/updateCart")
    @ResponseBody
    public String updateCart(
            @RequestParam("productId") Integer productId,
            @RequestParam("quantity") Integer quantity,
            HttpSession session) {

        Long userId = getCurrentUserId(session);

        if (userId == null) {
            return "user_not_logged_in";
        }

        if (productId == null || quantity == null) {
            return "invalid_parameters";
        }

        try {
            if (quantity <= 0) {
                // 如果数量小于等于0，则删除该商品
                cartService.removeFromCart(userId.intValue(), productId);
            } else {
                // 更新商品数量
                cartService.updateCartQuantity(userId.intValue(), productId, quantity);
            }
            return "success";
        } catch (Exception e) {
            e.printStackTrace();
            return "server_error: " + e.getMessage();
        }
    }

    /**
     * 从购物车删除商品
     * URL: /removeFromCart
     * Method: POST
     * Parameters: productId
     */
    @PostMapping("/removeFromCart")
    @ResponseBody
    public String removeFromCart(
            @RequestParam("productId") Integer productId,
            HttpSession session) {

        Long userId = getCurrentUserId(session);

        if (userId == null) {
            return "user_not_logged_in";
        }

        if (productId == null) {
            return "invalid_parameters";
        }

        try {
            cartService.removeFromCart(userId.intValue(), productId);
            return "success";
        } catch (Exception e) {
            e.printStackTrace();
            return "server_error: " + e.getMessage();
        }
    }


}
