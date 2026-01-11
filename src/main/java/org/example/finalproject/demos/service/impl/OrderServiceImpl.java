package org.example.finalproject.demos.service.impl;

import org.example.finalproject.demos.mapper.OrderItemMapper;
import org.example.finalproject.demos.mapper.OrderMapper;
import org.example.finalproject.demos.mapper.CartMapper;
import org.example.finalproject.demos.mapper.ProductsMapper;
import org.example.finalproject.demos.pojo.Order;
import org.example.finalproject.demos.pojo.OrderItem;
import org.example.finalproject.demos.pojo.Cart;
import org.example.finalproject.demos.pojo.ProductWithImg;
import org.example.finalproject.demos.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

/**
 * 订单服务实现类
 */
@Service
public class OrderServiceImpl implements OrderService {

    @Autowired
    private OrderMapper orderMapper;

    @Autowired
    private OrderItemMapper orderItemMapper;

    @Autowired
    private CartMapper cartMapper;

    @Autowired
    private ProductsMapper productsMapper;

//    获取用户未支付订单（状态为 0）
    @Override
    public List<Order> getUnpaidOrders(Integer userId) {
        List<Order> orders = orderMapper.selectByUserIdAndStatus(userId, 0);
        // 为每个订单加载订单项
        for (Order order : orders) {
            order.setItems(orderItemMapper.selectByOrderId(order.getId()));
        }
        return orders;
    }
//    获取用户已支付订单（状态为 1）
    @Override
    public List<Order> getPaidOrders(Integer userId) {
        List<Order> orders = orderMapper.selectByUserIdAndStatus(userId, 1);
        // 为每个订单加载订单项
        for (Order order : orders) {
            order.setItems(orderItemMapper.selectByOrderId(order.getId()));
        }
        return orders;
    }
//完成
    @Override
    public List<Order> getCompletedOrders(Integer userId) {
        List<Order> orders = orderMapper.selectByUserIdAndStatus(userId, 2);
        // 为每个订单加载订单项
        for (Order order : orders) {
            order.setItems(orderItemMapper.selectByOrderId(order.getId()));
        }
        return orders;
    }
//待评价订单
    @Override
    public List<Order> getPendingReviewOrders(Integer userId) {
        List<Order> orders = orderMapper.selectCompletedOrdersWithoutReview(userId);
        // 为每个订单加载订单项
        for (Order order : orders) {
            order.setItems(orderItemMapper.selectByOrderId(order.getId()));
        }
        return orders;
    }

//    获取订单详情
    @Override
    public Order getOrderDetail(Integer orderId, Integer userId) {
        Order order = orderMapper.selectById(orderId);
        if (order != null && order.getUserId().equals(userId)) {
            // 加载订单项
            order.setItems(orderItemMapper.selectByOrderId(orderId));
            return order;
        }
        return null;
    }
//    根据卖家ID获取订单列表
    @Override
    public List<Order> getOrdersBySellerId(Integer sellerId) {
        List<Order> orders = orderMapper.selectBySellerId(sellerId);
        // 为每个订单加载订单项
        for (Order order : orders) {
            order.setItems(orderItemMapper.selectByOrderId(order.getId()));
        }
        return orders;
    }
//    立即购买：为单个商品创建待支付订单
    @Override
    public Order createOrderForProduct(Integer userId, Integer productId, Integer quantity) {
        if (userId == null || productId == null || quantity == null || quantity <= 0) {
            throw new IllegalArgumentException("参数错误");
        }
        List<ProductWithImg> productDetail = productsMapper.productDetail(productId);
        if (productDetail == null || productDetail.isEmpty()) {
            throw new IllegalArgumentException("商品不存在");
        }
        ProductWithImg product = productDetail.get(0);

        Order order = buildBaseOrder(userId, product.getUserId(), product.getPrice().multiply(BigDecimal.valueOf(quantity)));
        orderMapper.insert(order);

        OrderItem item = new OrderItem();
        item.setOrderId(order.getId());
        item.setProductId(product.getId());
        item.setProductName(product.getTitle());
        item.setPrice(product.getPrice());
        item.setQuantity(quantity);
        item.setSubtotal(product.getPrice().multiply(BigDecimal.valueOf(quantity)));
        item.setCreateTime(new Date());
        if (product.getImages() != null && !product.getImages().isEmpty()) {
            item.setProductImage(product.getImages().get(0).getUrl());
        }
        orderItemMapper.insertBatch(java.util.Collections.singletonList(item));
        return orderMapper.selectById(order.getId());
    }
//    购物车结算：为购物车商品创建待支付订单，并清空购物车
    @Override
    public Order createOrderFromCart(Integer userId) {
        if (userId == null) {
            throw new IllegalArgumentException("用户未登录");
        }
        List<Cart> cartItems = cartMapper.selectCartsByUserId(userId);
        if (cartItems == null || cartItems.isEmpty()) {
            throw new IllegalStateException("购物车为空");
        }
        BigDecimal total = cartItems.stream()
                .map(Cart::getTotalPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // 取第一个商品的卖家ID，若需要多卖家拆单可后续扩展
        Integer sellerId = null;
        Integer firstProductId = cartItems.get(0).getProductId();
        List<ProductWithImg> firstProduct = productsMapper.productDetail(firstProductId);
        if (firstProduct != null && !firstProduct.isEmpty()) {
            sellerId = firstProduct.get(0).getUserId();
        }

        Order order = buildBaseOrder(userId, sellerId, total);
        orderMapper.insert(order);

        List<OrderItem> items = new ArrayList<>();
        Date now = new Date();
        for (Cart cart : cartItems) {
            OrderItem item = new OrderItem();
            item.setOrderId(order.getId());
            item.setProductId(cart.getProductId());
            item.setProductName(cart.getProductName());
            item.setProductImage(cart.getImageUrl());
            item.setPrice(cart.getPrice());
            item.setQuantity(cart.getQuantity());
            item.setSubtotal(cart.getTotalPrice());
            item.setCreateTime(now);
            items.add(item);
        }
        orderItemMapper.insertBatch(items);

        // 清空购物车
        cartMapper.deleteByUserId(userId);

        return orderMapper.selectById(order.getId());
    }

    private Order buildBaseOrder(Integer userId, Integer sellerId, BigDecimal totalAmount) {
        Date now = new Date();
        Order order = new Order();
        order.setOrderNo(generateOrderNo());
        order.setUserId(userId);
        order.setSellerId(sellerId);
        order.setTotalAmount(totalAmount);
        order.setStatus(0);
        order.setPaymentStatus(0);
        order.setCreateTime(now);
        order.setUpdateTime(now);
        return order;
    }

    private String generateOrderNo() {
        return "ORD" + System.currentTimeMillis() + UUID.randomUUID().toString().substring(0, 6).toUpperCase();
    }
//    获取所有订单（管理员用）
    @Override
    public List<Order> getAllOrders() {
        List<Order> orders = orderMapper.selectAll();
        // 为每个订单加载订单项
        for (Order order : orders) {
            order.setItems(orderItemMapper.selectByOrderId(order.getId()));
        }
        return orders;
    }
}
