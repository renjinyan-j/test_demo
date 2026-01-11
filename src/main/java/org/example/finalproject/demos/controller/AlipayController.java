package org.example.finalproject.demos.controller;

import lombok.extern.slf4j.Slf4j;
import org.example.finalproject.demos.service.AlipayService;
import org.example.finalproject.demos.util.SecurityUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

//是Lombok中的注解，用于为类自动生成一个名为log的日志记录器对象，简化日志记录的使用。
@Slf4j
@Controller
public class AlipayController {
    @Autowired
    private AlipayService alipayService;
    @Autowired
    private SecurityUtil securityUtil;
    /**
     * 生成支付页面并输出 HTML（PC/H5）
     */
//    生成支付表单
    @GetMapping("/pay/alipay/page")
    public void pagePay(String orderNo, HttpServletResponse response) throws IOException {
        Long currentUserId = securityUtil.getCurrentUserId();
//        获取当前用户Id
        String formHtml = alipayService.createPagePay(orderNo, currentUserId);
//        调用alipayService.createPagePay方法生成支付表单HTML
        response.setContentType(MediaType.TEXT_HTML_VALUE + ";charset=UTF-8");
        //        将生成的支付表单HTML写入响应体
        response.getWriter().write(formHtml);
        //        刷新响应体，确保所有数据都被发送到客户端
        response.flushBuffer();
    }
    /**
     * 支付宝异步通知
     */
    @PostMapping("/pay/alipay/notify")
    @ResponseBody
    public String notify(HttpServletRequest request) {
        return alipayService.handleNotify(request);
    }
    /**
     * 支付完成后的同步跳转，简单重定向到前台首页并带上标记
     */
    @GetMapping("/pay/alipay/return")
    public String returnPage(String out_trade_no) {
        log.info("支付同步返回 outTradeNo={}", out_trade_no);
        return "redirect:/ProductsFront?payResult=success&orderNo=" + out_trade_no;
    }
}

