package org.example.finalproject.demos.controller;

import org.example.finalproject.demos.pojo.*;
import org.example.finalproject.demos.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 后台管理员入口与仪表盘
 */
@Controller
@RequestMapping("/admin")
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    @Autowired
    private UserService userService;
    @Autowired
    private ProductsService productsService;
    @Autowired
    private OrderService orderService;
    @Autowired
    private ReviewService reviewService;
    @Autowired
    private CategoriesService categoriesService;

    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        List<User> users = safeList(userService.findAll());
        int userCount = users.size();

        // 统计管理员数量
        long adminCount = users.stream()
                .filter(u -> u.getRoles() != null)
                .flatMap(u -> u.getRoles().stream())
                .map(Role::getRoleCode)
                .filter("ROLE_ADMIN"::equals)
                .count();

        // 商品数量（包含图片信息的列表）
        int productCount = safeList(productsService.getProductsList()).size();

        // 订单数量
        int orderCount = safeList(orderService.getAllOrders()).size();

        // 评价数量
        int reviewCount = safeList(reviewService.getAllReviews()).size();

        model.addAttribute("userCount", userCount);
        model.addAttribute("adminCount", adminCount);
        model.addAttribute("productCount", productCount);
        model.addAttribute("orderCount", orderCount);
        model.addAttribute("reviewCount", reviewCount);
        model.addAttribute("activeMenu", "dashboard");
        return "admin/dashboard";
    }

    /**
     * 商品类型管理
     */
    @GetMapping("/categories")
    public String categories(Model model) {
        List<Category> categories = safeList(categoriesService.getAllCategories());
        model.addAttribute("categories", categories);
        model.addAttribute("activeMenu", "category");
        return "admin/categories";
    }

    /**
     * 二手商品管理 - 查看所有商品
     */
    @GetMapping("/products")
    public String products(Model model) {
        List<ProductWithImg> products = safeList(productsService.getProductsList());
        model.addAttribute("products", products);
        model.addAttribute("activeMenu", "product");
        return "admin/products";
    }

    /**
     * 订单管理 - 查看所有订单
     */
    @GetMapping("/orders")
    public String orders(Model model) {
        List<Order> orders = safeList(orderService.getAllOrders());
        model.addAttribute("orders", orders);
        model.addAttribute("activeMenu", "order-all");
        return "admin/orders";
    }

    /**
     * 订单管理 - 未支付订单
     */
    @GetMapping("/orders/unpaid")
    public String unpaidOrders(Model model) {
        List<Order> allOrders = safeList(orderService.getAllOrders());
        List<Order> unpaidOrders = allOrders.stream()
                .filter(o -> o.getStatus() != null && o.getStatus() == 0)
                .collect(Collectors.toList());
        model.addAttribute("orders", unpaidOrders);
        model.addAttribute("activeMenu", "order-unpaid");
        return "admin/orders";
    }

    /**
     * 订单管理 - 已支付订单
     */
    @GetMapping("/orders/paid")
    public String paidOrders(Model model) {
        List<Order> allOrders = safeList(orderService.getAllOrders());
        List<Order> paidOrders = allOrders.stream()
                .filter(o -> o.getStatus() != null && o.getStatus() == 1)
                .collect(Collectors.toList());
        model.addAttribute("orders", paidOrders);
        model.addAttribute("activeMenu", "order-paid");
        return "admin/orders";
    }

    /**
     * 订单管理 - 已完成订单
     */
    @GetMapping("/orders/completed")
    public String completedOrders(Model model) {
        List<Order> allOrders = safeList(orderService.getAllOrders());
        List<Order> completedOrders = allOrders.stream()
                .filter(o -> o.getStatus() != null && o.getStatus() == 2)
                .collect(Collectors.toList());
        model.addAttribute("orders", completedOrders);
        model.addAttribute("activeMenu", "order-completed");
        return "admin/orders";
    }

    /**
     * 评价信息管理 - 查看所有评价
     */
    @GetMapping("/reviews")
    public String reviews(Model model) {
        List<Review> reviews = safeList(reviewService.getAllReviews());
        model.addAttribute("reviews", reviews);
        model.addAttribute("activeMenu", "review");
        return "admin/reviews";
    }

    /**
     * 个人中心 - 个人信息
     */
    @GetMapping("/profile")
    public String profile(Model model) {
        model.addAttribute("activeMenu", "profile");
        return "admin/profile";
    }

    /**
     * 角色管理
     */
    @GetMapping("/roles")
    public String roles(Model model) {
        model.addAttribute("activeMenu", "role");
        return "admin/roles";
    }

    /**
     * 权限管理
     */
    @GetMapping("/permissions")
    public String permissions(Model model) {
        model.addAttribute("activeMenu", "permission");
        return "admin/permissions";
    }

    private <T> List<T> safeList(List<T> source) {
        return source == null ? Collections.emptyList() : source;
    }
}


