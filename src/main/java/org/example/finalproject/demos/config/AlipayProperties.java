package org.example.finalproject.demos.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * 支付宝沙箱配置（通过 application.properties 注入）
 */

//Lombok自动生成常规方法，如getter、setter、toString等
@Data
//Spring注解，表示该类是一个组件，可以被Spring容器管理
@Component
//Spring注解，表示该类的属性将从配置文件中以"pay.alipay"为前缀的属性进行注入
//会自动生成application.properties 读取以 pay.alipay 开头的配置
@ConfigurationProperties(prefix = "pay.alipay")
public class AlipayProperties {
    /**
     * 沙箱 APPID
     */
    private String appId;
    /**
     * 应用私钥（PKCS8）
     */
    private String privateKey;
    /**
     * 支付宝公钥
     */
    private String alipayPublicKey;
    /**
     * 网关地址，沙箱默认：https://openapi-sandbox.dl.alipaydev.com/gateway.do
     */
    private String gateway;
    /**
     * 异步通知地址
     */
    private String notifyUrl;
    /**
     * 同步回跳地址
     */
    private String returnUrl;
    /**
     * 字符集，默认 utf-8
     */
    private String charset = "utf-8";
    /**
     * 签名类型，默认 RSA2
     */
    private String signType = "RSA2";
}

