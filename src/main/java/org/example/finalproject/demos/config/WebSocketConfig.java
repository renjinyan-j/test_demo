package org.example.finalproject.demos.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.lang.NonNull;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

//底层通信方式
//STOMP文本消息协议
//用于启动并配置项目中的STOMP WebSocket功能
@Configuration
//用于启动 WebSocket 消息代理功能
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

//    registerStompEndpoints在springboot配置中阶段被调用，向StompEndpointRegistry添加客户端可连接的端点"/ws-chat"
//    @NonNull StompEndpointRegistry registry 为端口注册器
    @Override
    public void registerStompEndpoints(@NonNull StompEndpointRegistry registry) {
        // 注册 STOMP 端点，供前端 SockJS 连接；允许跨域；启用 SockJS 回退
        registry.addEndpoint("/ws-chat")
//                允许任意域名发起 WebSocket/SockJS 连接（跨域放行）
                .setAllowedOriginPatterns("*")
                .withSockJS();
    }
    @Override
    public void configureMessageBroker(@NonNull MessageBrokerRegistry registry) {
        // 开启内置简单消息代理，支持群发/topic 与点对点/queue
        registry.enableSimpleBroker("/topic", "/queue");
        // 客户端发送消息的目标前缀（对应 @MessageMapping）
        registry.setApplicationDestinationPrefixes("/app");
        // 点对点用户目的地前缀（对应 convertAndSendToUser）
        registry.setUserDestinationPrefix("/user");
    }
}


