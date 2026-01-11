package org.example.finalproject.demos.controller;

import lombok.extern.slf4j.Slf4j;
import org.example.finalproject.demos.pojo.Order;
import org.example.finalproject.demos.service.OrderService;
import org.example.finalproject.demos.util.SecurityUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Slf4j
@Controller
public class OrderPayController {

    @Autowired
    private OrderService orderService;

    @Autowired
    private SecurityUtil securityUtil;

    /**
     * 商品详情页“立即购买”
     */
    @PostMapping("/order/buyNow")
    public String buyNow(@RequestParam("productId") Integer productId,
                         @RequestParam(value = "quantity", defaultValue = "1") Integer quantity) {
//        获取当前登录用户的用户ID（userId），通常用于权限控制、日志记录或者关联用户相关的操作
        Long userId = securityUtil.getCurrentUserId();
        if (userId == null) {
            return "redirect:/toLoginPage";
        }
        Order order = orderService.createOrderForProduct(userId.intValue(), productId, quantity);
        return "redirect:/pay/alipay/page?orderNo=" + order.getOrderNo();
    }

    /**
     * 购物车“去结算”
     */
    @PostMapping("/order/checkoutCart")
    public String checkoutCart() {
        Long userId = securityUtil.getCurrentUserId();
        if (userId == null) {
            return "redirect:/toLoginPage";
        }
        Order order = orderService.createOrderFromCart(userId.intValue());
        return "redirect:/pay/alipay/page?orderNo=" + order.getOrderNo();
    }
}

