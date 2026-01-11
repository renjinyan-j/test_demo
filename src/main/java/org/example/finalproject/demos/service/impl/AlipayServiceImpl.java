package org.example.finalproject.demos.service.impl;

import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.internal.util.AlipaySignature;
import com.alipay.api.request.AlipayTradePagePayRequest;
import lombok.extern.slf4j.Slf4j;
import org.example.finalproject.demos.config.AlipayProperties;
import org.example.finalproject.demos.mapper.OrderMapper;
import org.example.finalproject.demos.pojo.Order;
import org.example.finalproject.demos.service.AlipayService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

//Lombok是java库，用于简化java代码的编写通过注解自动生成常见的样板代码
//Lombok注解，用于自动生成日志记录器Logger实例
@Slf4j
@Service
public class AlipayServiceImpl implements AlipayService {

    @Autowired
    private AlipayProperties alipayProperties;

    @Autowired
    private OrderMapper orderMapper;

//    构建并返回一个支付宝客户端实例
    private AlipayClient buildClient() {
        // 防止因缺少必要的支付宝接入参数而导致程序运行时抛出空指针异常
//        !StringUtils.hasText(alipayProperties.getAppId()) 判断 appId 是否为空或仅包含空白字符
        if (!StringUtils.hasText(alipayProperties.getAppId())
                || !StringUtils.hasText(alipayProperties.getPrivateKey())
                || !StringUtils.hasText(alipayProperties.getAlipayPublicKey())
                || !StringUtils.hasText(alipayProperties.getGateway())) {
            throw new IllegalStateException("支付宝配置缺失，请检查 appId/privateKey/alipayPublicKey/gateway");
        }
//        构建AlipayClient实例，用于与支付宝进行交互
        return new DefaultAlipayClient(
                alipayProperties.getGateway(),
                alipayProperties.getAppId(),
                alipayProperties.getPrivateKey(),
//将格式固定为json格式，目的是为了确保与支付宝API的通信格式一致
                "json",
                alipayProperties.getCharset(),
                alipayProperties.getAlipayPublicKey(),
                alipayProperties.getSignType()
        );
    }

//    创建支付宝页面支付请求，返回支付页面HTML代码
    @Override
//    orderNo:订单号，currentUserId:当前用户ID
    public String createPagePay(String orderNo, Long currentUserId) {
//        检查传入的订单号和用户 ID 是否合法（不能为空），如果不满足条件则抛出非法参数异常。
        if (!StringUtils.hasText(orderNo) || currentUserId == null) {
            throw new IllegalArgumentException("参数错误，订单号或用户为空");
        }
//        使用 MyBatis Mapper 查询数据库中是否存在该订单号对应的订单记录，如果找不到则报错
        Order order = orderMapper.selectByOrderNo(orderNo);
        if (order == null) {
            throw new IllegalArgumentException("订单不存在");
        }
//        确保当前登录用户确实是这个订单的所有者，防止越权操作他人订单。
        if (!currentUserId.equals(order.getUserId().longValue())) {
            throw new IllegalArgumentException("无权操作此订单");
        }
//        判断订单是否已经被支付过（比如状态大于等于 1），如果是的话就不允许再次支付
        if (order.getStatus() != null && order.getStatus() >= 1) {
            throw new IllegalStateException("订单已支付或已完成，无需重复支付");
        }
//        调用buildClient() 方法创建支付宝 SDK 客户端实例
        AlipayClient alipayClient = buildClient();
//        创建一个新的网页支付请求对象
        AlipayTradePagePayRequest request = new AlipayTradePagePayRequest();
//        设置异步通知地址（notifyUrl）和同步回调地址（returnUrl）——分别用于后台接收支付结果和用户浏览器跳转回来时展示结果
        request.setNotifyUrl(alipayProperties.getNotifyUrl());
        request.setReturnUrl(alipayProperties.getReturnUrl());

//        构建支付请求的业务参数，包括订单号、商品码、总金额、订单标题等
        Map<String, Object> biz = new HashMap<>();
        biz.put("out_trade_no", order.getOrderNo());
        biz.put("product_code", "FAST_INSTANT_TRADE_PAY");
        biz.put("total_amount", order.getTotalAmount());
        biz.put("subject", "订单支付-" + order.getOrderNo());
// 这个 Map 转换为 JSON 字符串设置到请求对象中
        request.setBizContent(toJson(biz));

//       使用 SDK 客户端执行支付请求；
        try {
//            成功后返回 HTML 页面内容（可以直接渲染给前端跳转）
            return alipayClient.pageExecute(request).getBody();
        } catch (AlipayApiException e) {
//            失败则记录日志并抛出自定义异常
            log.error("调用支付宝下单失败 orderNo={}", orderNo, e);
            throw new IllegalStateException("创建支付请求失败");
        }
    }

    //    处理来自支付宝的异步通知（Notify）请求，它主要用于验证支付结果、更新本地订单状态，并做出相应的响应
    @Override
//    接收一个 HttpServletRequest 对象，包含支付宝 POST 过来的所有参数
    public String handleNotify(HttpServletRequest request) {
//       从 HTTP 请求中抽取所有的参数（如订单号、金额、签名等）封装成 Map，便于后续处理
        Map<String, String> params = extractParams(request);
//        使用支付宝 SDK 提供的工具类对请求参数进行签名验证，确保通知确实来自支付宝且未被篡改
        try {
//            签名验证失败则记录警告日志并返回失败响应
            boolean signVerified = AlipaySignature.rsaCheckV1(
                    params,
                    alipayProperties.getAlipayPublicKey(),
                    alipayProperties.getCharset(),
                    alipayProperties.getSignType()
            );
            if (!signVerified) {
                log.warn("支付宝验签失败 params={}", params);
                return "failure";
            }
        } catch (Exception e) {
            log.error("支付宝验签异常", e);
            return "failure";
        }
//        提取订单号、交易状态、支付金额等关键参数，用于后续业务逻辑处理
        String outTradeNo = params.get("out_trade_no");
        String tradeStatus = params.get("trade_status");
        String totalAmountStr = params.get("total_amount");

//        使用订单号查询本地数据库，确认该订单确实存在于数据库中
        Order order = orderMapper.selectByOrderNo(outTradeNo);
        if (order == null) {
            log.warn("回调订单不存在 outTradeNo={}", outTradeNo);
            return "failure";
        }
//        比较支付宝回调中的金额是否与本地订单金额一致，防止篡改金额
        // 金额校验
        if (StringUtils.hasText(totalAmountStr)) {
            BigDecimal callbackAmount = new BigDecimal(totalAmountStr);
            if (order.getTotalAmount() != null && order.getTotalAmount().compareTo(callbackAmount) != 0) {
                log.warn("金额不一致 outTradeNo={} expect={} actual={}", outTradeNo, order.getTotalAmount(), callbackAmount);
                return "failure";
            }
        }

        // 支付成功/完成 -> 更新为已支付（status=1/payment_status=1）
        if ("TRADE_SUCCESS".equals(tradeStatus) || "TRADE_FINISHED".equals(tradeStatus)) {
            Date now = new Date();
            orderMapper.updatePaySuccess(outTradeNo, "ALIPAY_SANDBOX", now, now);
            log.info("订单支付成功 outTradeNo={}", outTradeNo);
            return "success";
        }

        // 其他状态（未支付/支付中/关闭等）保持未支付状态，显式回写为 0，便于排查
        Date now = new Date();
        Order pending = new Order();
        pending.setId(order.getId());
        pending.setStatus(0);
        pending.setPaymentStatus(0);
        pending.setUpdateTime(now);
        orderMapper.updateById(pending);
        log.info("订单未支付或支付失败，保持未支付状态 outTradeNo={} tradeStatus={}", outTradeNo, tradeStatus);
        return "success";
    }

    /**
     * 读取 request 中的所有参数，封装成 Map（处理支付宝回调时的参数格式）
     */
    private Map<String, String> extractParams(HttpServletRequest request) {
        Map<String, String[]> requestParams = request.getParameterMap();
        Map<String, String> params = new HashMap<>(requestParams.size());
//        遍历原始参数 map 的每一个条目
        for (Map.Entry<String, String[]> entry : requestParams.entrySet()) {
            String name = entry.getKey();
            String[] values = entry.getValue();
            StringBuilder valueStr = new StringBuilder();
            for (int i = 0; i < values.length; i++) {
                valueStr.append(values[i]);
                if (i < values.length - 1) {
                    valueStr.append(",");
                }
            }
            params.put(name, valueStr.toString());
        }
        return params;
    }

    /**
     * 简单 JSON 序列化（避免额外依赖）
     */
    private String toJson(Map<String, Object> map) {
//        创建一个可变字符串构造器，用于拼接 JSON 字符串
        StringBuilder sb = new StringBuilder();
        sb.append("{");
        boolean first = true;
        for (Map.Entry<String, Object> e : map.entrySet()) {
            if (!first) sb.append(",");
            sb.append("\"").append(e.getKey()).append("\":");
            Object v = e.getValue();
            if (v instanceof Number) {
                sb.append(v);
            } else {
                sb.append("\"").append(String.valueOf(v)).append("\"");
            }
            first = false;
        }
        sb.append("}");
        return sb.toString();
    }
}

