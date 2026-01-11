# 项目技术栈说明

## 基础环境
- JDK 1.8，构建工具 Maven，父框架 Spring Boot 2.7.6。
- 运行端口 8080，默认使用本地 MySQL `campusplatform` 数据库（参见 `application.properties`）。

## 后端框架与中间件
- Spring MVC + Thymeleaf：模板渲染与页面路由，启用了国际化资源 `i18n/login*.properties`。
- Spring Security：基于 `SecurityConfiguration` 的表单登录、自定义认证/鉴权（自定义用户详情、元数据源、决策器、成功处理器）与角色/URL 访问控制。
- Spring WebSocket (STOMP)：`WebSocketConfig` 暴露端点 `/ws-chat`，使用 SockJS 回退与内置消息代理（`/topic`、`/queue`，应用前缀 `/app`，用户前缀 `/user`）实现即时聊天。
- Spring Mail：通过 QQ SMTP 发送邮件通知。
- Spring Web：REST/页面控制器及文件上传（10MB 单文件上限，50MB 总请求）。

## 数据访问与持久化
- MyBatis-Spring-Boot Starter 2.2.2：XML Mapper 与实体位于 `mapper/*.xml`、`demos/mapper`、`demos/pojo`。
- MyBatis Generator：根据 `generatorConfig.xml` 生成实体/Mapper。
- PageHelper 1.4.6：分页支持。
- Druid 1.1.10：数据源与连接池。
- MySQL Connector/J：MySQL 8 驱动；另外包含 JSR310 TypeHandlers 处理 `java.time` 类型。
- Spring Data JPA 依赖已引入（当前主要使用 MyBatis 访问）。

## 业务集成
- 支付宝 Java SDK（沙箱环境）：`AlipayController`/`OrderPayController` 相关支付回调与跳转配置。
- 邮件与验证码：`MailService` 发送邮件验证码。

## 前端与静态资源
- 模板：Thymeleaf 页面（买家/卖家/管理员等多套模板）。
- UI 库：Bootstrap 5、Bootstrap Icons、Font Awesome（CDN）。
- JS 库：jQuery、Bootstrap Bundle、SockJS、Stomp.js（聊天前端）。
- 样式与资源：自定义 CSS/JS，静态上传目录 `/uploads/**` 由 `WebConfig` 映射到项目根 `uploads/`。

## 开发体验与测试
- Lombok：简化实体/服务样板代码。
- DevTools：开发时自动重载。
- Spring Boot Starter Test：单元测试/集成测试基座。





