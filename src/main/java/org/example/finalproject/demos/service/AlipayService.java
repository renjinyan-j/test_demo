package org.example.finalproject.demos.service;

import javax.servlet.http.HttpServletRequest;

public interface AlipayService {

    /**
     * 生成 PC/H5 支付页面 HTML
     */
    String createPagePay(String orderNo, Long currentUserId);

    /**
     * 处理支付宝异步通知
     */
    String handleNotify(HttpServletRequest request);
}

