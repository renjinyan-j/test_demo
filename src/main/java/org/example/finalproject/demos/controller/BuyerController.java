package org.example.finalproject.demos.controller;

import org.example.finalproject.demos.pojo.Order;
import org.example.finalproject.demos.pojo.Review;
import org.example.finalproject.demos.service.OrderService;
import org.example.finalproject.demos.service.ReviewService;
import org.example.finalproject.demos.util.SecurityUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

/**
 * 买家控制器
 * 用于买家后台管理功能
 */
@Controller
@RequestMapping("/buyer")
@PreAuthorize("hasAnyRole('BUYER', 'USER', 'ADMIN')")
public class BuyerController {

    @Autowired
    private OrderService orderService;

    @Autowired
    private ReviewService reviewService;

    @Autowired
    private SecurityUtil securityUtil;

    /**
     * 系统首页
     */
    @GetMapping("/xitongshouye")
    public String xitongshouye(Model model) {
        Long userId = securityUtil.getCurrentUserId();
        if (userId == null) {
            return "redirect:/toLoginPage";
        }
        model.addAttribute("userId", userId);
        return "buyer/xitongshouye";
    }

    /**
     * 未支付订单列表
     */
    @GetMapping("/order/unpaid")
    public String unpaidOrders(Model model) {
        Long userId = securityUtil.getCurrentUserId();
        if (userId == null) {
            return "redirect:/toLoginPage";
        }
        List<Order> orders = orderService.getUnpaidOrders(userId.intValue());
        model.addAttribute("orders", orders);
        return "buyer/weizhifu";
    }

    /**
     * 已支付订单列表
     */
    @GetMapping("/order/paid")
    public String paidOrders(Model model) {
        Long userId = securityUtil.getCurrentUserId();
        if (userId == null) {
            return "redirect:/toLoginPage";
        }
        List<Order> orders = orderService.getPaidOrders(userId.intValue());
        model.addAttribute("orders", orders);
        return "buyer/yizhifu";
    }

    /**
     * 已完成订单列表
     */
    @GetMapping("/order/completed")
    public String completedOrders(Model model) {
        Long userId = securityUtil.getCurrentUserId();
        if (userId == null) {
            return "redirect:/toLoginPage";
        }
        List<Order> orders = orderService.getCompletedOrders(userId.intValue());
        model.addAttribute("orders", orders);
        return "buyer/yiwancheng";
    }

    /**
     * 待评价订单列表
     */
    @GetMapping("/order/pending-review")
    public String pendingReviewOrders(Model model) {
        Long userId = securityUtil.getCurrentUserId();
        if (userId == null) {
            return "redirect:/toLoginPage";
        }
        List<Order> orders = orderService.getPendingReviewOrders(userId.intValue());
        model.addAttribute("orders", orders);
        return "buyer/yiwancheng"; // 可以创建单独的待评价页面，这里先用已完成页面
    }

    /**
     * 我的评价列表
     */
    @GetMapping("/review/my-reviews")
    public String myReviews(Model model) {
        Long userId = securityUtil.getCurrentUserId();
        if (userId == null) {
            return "redirect:/toLoginPage";
        }
        List<Review> reviews = reviewService.getMyReviews(userId.intValue());
        model.addAttribute("reviews", reviews);
        return "buyer/yiwancheng"; // 可以创建单独的评价页面
    }
}
