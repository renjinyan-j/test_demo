# 校园便利平台项目文档

## 1. 项目简介
- 面向校园场景的二手/便利交易平台，支持买家、卖家、管理员多角色。
- 提供商品浏览、购物车、下单支付（支付宝沙箱）、订单管理、评价、即时聊天等功能。
- 前后端基于 Spring Boot + Thymeleaf，一体化打包运行。

## 2. 核心功能
- 身份与权限：注册/登录，Spring Security 基于角色的访问控制（ADMIN/SELLER/BUYER/USER），自定义登录成功跳转、权限决策与 403 处理。
- 商品与分类：商品列表、搜索、分类筛选、详情页；卖家/管理员后台的商品与分类管理。
- 购物车与订单：加入/更新/删除购物车，立即购买、购物车结算，订单创建与状态流转（未支付/已支付/已完成）。
- 支付宝沙箱支付：扫码/跳转支付，同步回跳与异步通知回调。
- 即时聊天：基于 STOMP WebSocket（SockJS 回退），买家/卖家点对点会话、未读清零与历史消息拉取。
- 评价与用户信息：订单评价、个人资料管理、邮件验证码。
- 文件上传：商品图片等文件存储到项目根 `uploads/`，通过 `/uploads/**` 静态映射访问。

## 3. 技术栈
- 详见 `TECH_STACK.md`；核心包括 Spring Boot 2.7.6、Spring Security、Thymeleaf、MyBatis + PageHelper、Druid、WebSocket、Alipay Java SDK、Spring Mail、Lombok、DevTools、Bootstrap/Font Awesome/SockJS/Stomp.js。

## 4. 目录速览
```
src/main/java/org/example/finalproject/demos
  ├─config            # 安全、Web、WebSocket、Alipay 配置
  ├─controller        # 页面与 REST 控制器（买家/卖家/管理员/支付/聊天等）
  ├─dto               # 请求 DTO
  ├─mapper            # MyBatis Mapper 接口
  ├─pojo              # 实体与工具类
  ├─service & impl    # 业务接口与实现
  └─util              # 辅助工具（如 SecurityUtil）
src/main/resources
  ├─templates         # Thymeleaf 模板（front/buyer/seller/admin 等）
  ├─static            # 静态资源与登录页资源
  ├─mapper            # MyBatis XML 映射
  ├─i18n              # 国际化资源
  └─application.properties  # 核心配置
uploads/               # 运行期上传文件目录（WebConfig 静态映射）
```

## 5. 环境要求
- JDK 1.8+
- Maven 3.6+
- MySQL 8（默认数据库名 `campusplatform`，可在配置中修改）
- 可选：阿里沙箱账号、QQ 邮箱 SMTP 授权码

## 6. 配置说明（`src/main/resources/application.properties`）
- 端口：`server.port=8080`
- 数据源：`spring.datasource.url/username/password` 指向 MySQL；使用 Druid 连接池。
- MyBatis：`mybatis.mapper-locations=classpath:mapper/*.xml`，`mybatis.type-aliases-package=org.example.finalproject.demos.pojo`，开启下划线转小驼峰。
- 模板与国际化：Thymeleaf 前后缀配置，`spring.messages.basename=i18n.login`。
- 邮件：`spring.mail.*`，请替换为自己的 SMTP 账号与授权码。
- 上传：`spring.servlet.multipart.max-file-size=10MB`，`max-request-size=50MB`。
- 支付宝沙箱：`pay.alipay.*`（appId、私钥、公钥、回调地址、网关等），部署前请替换为自己的沙箱或生产配置。

## 7. 运行步骤
1) 创建数据库并导入表结构/初始数据（数据库名需与配置一致，若未提供脚本，可根据实体与 Mapper 自行建表）。
2) 调整 `application.properties` 中的数据库、邮箱、支付宝等敏感配置。
3) 执行依赖拉取与编译：
   - `mvn clean package -DskipTests`
4) 运行：
   - IDE 运行 `org.example.finalproject.FinalProjectApplication`
   - 或命令行：`mvn spring-boot:run`
5) 访问：
   - 前台首页：`http://localhost:8080/front/homepage` 或根路径
   - 登录/注册：`/toLoginPage`
   - 卖家后台：`/seller/dashboard`
   - 管理员后台：`/admin/dashboard`

## 8. 权限与安全
- 静态资源放行：`/css/**`, `/js/**`, `/images/**`。
- 公开接口：登录/注册、商品浏览、支付回调等（见 `SecurityConfiguration` 中的 `antMatchers`）。
- 角色控制：
  - `ADMIN` 访问 `/admin/**`
  - `SELLER` 或 `ADMIN` 访问 `/seller/**`
  - `BUYER`/`USER`/`SELLER`/`ADMIN` 访问购物与买家页面
- 表单登录：`/toLoginPage` 页面，提交到 `/login`，成功后按角色跳转（自定义成功处理器）。
- CSRF：对购物、登录、注册、支付回调、聊天 API 等路径放行 CSRF 校验；其余保持默认保护。

## 9. 聊天模块
- WebSocket 端点：`/ws-chat`（SockJS 回退，允许跨域）。
- 消息代理：`/topic` & `/queue`，应用前缀 `/app`，用户前缀 `/user`。
- 前端示例：`templates/front/chat.html` 使用 SockJS + Stomp 连接，调用 REST `/api/chat/conversation/{cid}/messages` 拉取历史并标记已读。
- 典型流程：前端连接 → 订阅会话目的地 → REST 拉取历史 → 发送消息到 `/app/chat/send`（根据后端 @MessageMapping 实际路径）→ 服务端转发到对应用户的 `/user/{id}/queue/msg`。

## 10. 支付流程（支付宝沙箱）
- 下单入口：`/order/buyNow` 或 `/order/checkoutCart` 创建订单。
- 支付请求：`/pay/alipay/page` 发起支付。
- 回调：
  - 异步通知：`/pay/alipay/notify`
  - 同步回跳：`/pay/alipay/return`
- 回调处理与验签由 `AlipayController`/`OrderPayController` 与 `AlipayService` 实现，正式部署前务必替换密钥与回调域名。

## 11. 常用前端路径
- 首页/商品列表：`/front/homepage`, `/ershouproducts`
- 商品详情：`/productDetail?id={pid}`
- 购物车：`/cart`
- 订单结果页：`/buyer/yiwancheng`, `/buyer/yizhifu`, `/buyer/weizhifu`
- 卖家聊天列表：`/seller/chat-list`
- 买家聊天列表：`/buyer/chat-list`
- 管理后台：`/admin/dashboard` 等

## 12. 开发与调试建议
- 使用 DevTools 热重载提升开发效率。
- MyBatis Generator 配置位于 `generatorConfig.xml`，如需调整表结构或重新生成，请先更新数据库与配置。
- 替换所有示例密钥/密码为环境变量或外部化配置，避免泄露。
- 若使用反向代理/HTTPS，注意同步更新支付宝回调地址与 WebSocket 代理配置。

## 13. 测试
- 测试基座：`Spring Boot Starter Test`。
- 运行：`mvn test`（当前示例测试位于 `FinalProjectApplicationTests`，可按业务补充单元/集成测试）。





