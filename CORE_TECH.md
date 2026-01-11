## 校园便利平台核心技术说明

本文从框架选型、关键模块设计和典型业务流程三个角度，说明本项目的核心技术实现思路，便于毕业设计说明书或结题文档使用。

---

## 一、整体架构与框架选型

- **架构模式**
  - 采用 **Spring Boot + Spring MVC + Thymeleaf** 的单体 Web 应用架构。
  - 分层结构：表现层（Controller + Thymeleaf）、业务层（Service）、数据访问层（Mapper + MyBatis）、基础组件层（安全、WebSocket、配置等）。

- **技术组合理由**
  - **Spring Boot 2.7.6**：简化配置、内嵌容器、一键打包部署，适合课程设计和中小型项目。
  - **Thymeleaf**：与 Spring 深度整合，支持表达式、国际化标签，便于实现服务端渲染页面。
  - **MyBatis + PageHelper + Druid**：控制 SQL 细节、易于调优，PageHelper 处理分页查询，Druid 提供成熟的连接池和监控能力。
  - **Spring Security**：统一处理认证与授权，便于实现多角色（管理员、卖家、买家）细粒度访问控制。
  - **WebSocket (STOMP)**：为即时聊天提供全双工消息通道，比轮询更高效。
  - **支付宝沙箱 SDK + Spring Mail**：满足在线支付与邮件通知等典型电商场景需求。

---

## 二、核心技术模块

### 1. 用户认证与权限控制（Spring Security）

- **关键类**
  - `SecurityConfiguration`：整体安全配置，继承 `WebSecurityConfigurerAdapter`。
  - `CustomUserDetailsService`：根据用户名从数据库加载用户、角色与权限信息。
  - `CustomFilterInvocationSecurityMetadataSource`：根据请求 URL 动态匹配所需权限。
  - `CustomUrlDecisionManager`：判断当前用户是否具备访问该 URL 所需的角色/权限。
  - `CustomAuthenticationSuccessHandler`：登录成功后的自定义跳转逻辑（根据角色跳转到不同后台）。

- **核心配置要点**
  - **静态资源放行**：在 `configure(WebSecurity web)` 中忽略 `/css/**`, `/js/**`, `/images/**` 等。
  - **URL 鉴权规则**（`configure(HttpSecurity http)`）：
    - 未登录可访问：登录页、注册、商品浏览、搜索、支付回调等公开接口。
    - 管理员路径：`/admin/**` 仅 `ADMIN` 角色访问。
    - 卖家路径：`/seller/**` 由 `SELLER` 或 `ADMIN` 访问。
    - 买家路径：`/buyer/**` 由 `BUYER`/`USER`/`ADMIN` 访问。
    - 购物相关页面（`/cart`、下单、结算等）要求登录，且限制在指定角色内。
  - **表单登录与注销**：
    - 登录页：`/toLoginPage`，提交地址：`/login`。
    - 登录成功：交由 `CustomAuthenticationSuccessHandler` 按角色重定向（如买家首页、卖家后台、管理员后台）。
    - 登录失败与注销：配置失败 URL 和注销 URL（`/logout`），统一返回登录页。
  - **CSRF 策略**：
    - 默认开启 CSRF 保护。
    - 对部分接口（购物车、下单、注册、支付回调、聊天 REST 接口等）在 `csrf().ignoringAntMatchers(...)` 中放行，保证前端调用方便与支付回调可用。

- **技术价值**
  - 实现了从 **数据库用户-角色-权限模型** → **URL 访问控制** 的完整链路。
  - 自定义元数据源与决策器，可根据实际菜单/权限表灵活扩展，非常贴近真实企业项目做法。

---

### 2. 数据持久化与分页查询（MyBatis + PageHelper + Druid）

- **关键配置**
  - `application.properties`：
    - `spring.datasource.*`：配置 MySQL 连接信息和 Druid 数据源。
    - `mybatis.mapper-locations=classpath:mapper/*.xml`：Mapper XML 映射路径。
    - `mybatis.type-aliases-package=org.example.finalproject.demos.pojo`：实体别名包。
    - `mybatis.configuration.map-underscore-to-camel-case=true`：数据库下划线命名与 Java 小驼峰自动映射。
  - `generatorConfig.xml`：
    - 配置 JDBC 连接、实体类生成包路径、Mapper 接口与 XML 生成路径。
    - 利用 **MyBatis Generator** 自动生成基础 CRUD 代码，减少重复劳动。

- **数据访问层设计**
  - `demos/pojo`：实体类，对应数据库表，如 `Products`, `Order`, `Cart`, `Review` 等。
  - `demos/mapper`：Mapper 接口，与 `resources/mapper/*.xml` 一一对应。
  - 通过 Service 层组合调用多个 Mapper，封装业务逻辑。

- **分页查询（PageHelper）**
  - 在需要分页的查询前调用 `PageHelper.startPage(pageNum, pageSize)`。
  - Mapper 执行普通的 `select` 语句，由 PageHelper 自动追加 `limit` / `offset`。
  - 返回 `PageInfo` 或自定义分页 DTO，前端展示分页信息。

- **技术价值**
  - 保留了 SQL 可控性，适合复杂查询和性能调优。
  - 分层清晰，便于维护，也符合课程设计对 “三层结构 + ORM/持久层框架” 的要求。

---

### 3. 即时聊天模块（WebSocket + STOMP）

- **关键类与页面**
  - `WebSocketConfig`：实现 `WebSocketMessageBrokerConfigurer`，启用 STOMP WebSocket。
    - 注册端点：`/ws-chat`，允许跨域并支持 SockJS 回退。
    - 启用简单消息代理：`/topic`、`/queue`。
    - 设置应用前缀：`/app`，用户目的地前缀：`/user`。
  - 聊天相关 Controller：`ChatPageController`, `ChatRestController`, `ChatWsController`（具体类名以实际代码为准）。
  - 前端页面：`templates/front/chat.html`，使用 SockJS + Stomp.js 连接服务器。

- **消息流转流程**
  1. 前端加载聊天页面后，通过 SockJS 连接 `/ws-chat`。
  2. 使用 Stomp 订阅与当前会话相关的目的地（如 `/user/{currentUserId}/queue/msg`）。
  3. 首次载入时，前端通过 REST 接口 `/api/chat/conversation/{cid}/messages` 拉取历史消息，并调用 `markRead()` 清除未读。
  4. 发送消息时，前端将消息 POST 到 REST 或直接发送到 `@MessageMapping` 标注的地址（如 `/app/chat/send`）。
  5. 服务端校验发送者身份后，将消息持久化到数据库，并通过 `convertAndSendToUser` 推送到接收方的 `/user/{peerUserId}/queue/msg` 队列。
  6. 前端监听到新消息后动态渲染气泡，并更新未读状态。

- **技术价值**
  - 展示了 Spring WebSocket + STOMP 的完整使用流程，包括 **端点注册、消息代理、用户目的地、前后端订阅与推送**。
  - 与权限体系和会话管理结合，保证聊天仅限于已登录用户、并与订单/商品等业务实体关联。

---

### 4. 支付宝沙箱支付集成

- **关键配置**
  - 依赖：`alipay-sdk-java`。
  - 配置类：`AlipayProperties`，通过 `@ConfigurationProperties("pay.alipay")` 读取 `application.properties` 中的 Alipay 参数：
    - `appId`、`privateKey`、`alipayPublicKey`、`notifyUrl`、`returnUrl`、`gateway`、`charset`、`signType` 等。

- **业务流程**
  1. 用户下单（立即购买或购物车结算），由 `OrderService` 创建订单记录，状态为“未支付”。
  2. 前端请求支付接口（如 `/pay/alipay/page`），后端使用 Alipay SDK 构造 `AlipayTradePagePayRequest`。
  3. 将订单号、金额、商品描述等信息封装为 `bizContent`，并设置同步、异步回调地址。
  4. 请求支付宝沙箱网关，生成支付表单/跳转链接，返回给前端，在浏览器中打开进行支付。
  5. 支付完成后：
     - **异步通知**：支付宝服务器回调 `pay.alipay.notifyUrl`，后端验签并检查交易状态，更新订单状态为“已支付”，记录支付时间与流水号。
     - **同步回跳**：用户浏览器跳转到 `pay.alipay.returnUrl`，展示支付成功或失败页面。

- **技术价值**
  - 覆盖了从 **订单系统 → 第三方支付平台 → 回调处理 → 订单状态流转** 的典型电商闭环。
  - 使用配置类 + SDK 封装，参数集中管理，便于从沙箱切换到生产环境。

---

### 5. 文件上传与静态资源访问

- **上传配置**
  - `application.properties`：
    - 启用 multipart：`spring.servlet.multipart.enabled=true`
    - 单文件大小：`max-file-size=10MB`
    - 总请求大小：`max-request-size=50MB`
  - 主要用于卖家上传商品图片等。

- **静态访问映射**
  - `WebConfig` 实现 `WebMvcConfigurer`，重写 `addResourceHandlers`：
    - 将 `/uploads/**` 映射到项目根目录下的 `uploads/` 文件夹。
    - 浏览器可直接通过 URL 访问已上传图片，例如：`/uploads/xxx.webp`。

- **技术价值**
  - 符合实际业务中“上传即静态访问”的常见设计。
  - 将上传路径抽象为配置，便于后续迁移到对象存储（如 OSS、COS）等。

---

### 6. 邮件发送与验证码（Spring Mail）

- **配置**
  - 使用 QQ 邮箱 SMTP：
    - `spring.mail.host=smtp.qq.com`
    - `spring.mail.username` / `spring.mail.password`（授权码）
    - 启用 TLS/SSL 相关属性，指定协议版本 `TLSv1.2`。

- **业务用法**
  - 在 `MailService` 中使用 `JavaMailSender` 构造邮件，发送注册验证码或通知类邮件。
  - 结合 `VerifyCodeUtil` 等工具，生成随机验证码并存入 Session 或数据库，前端输入后进行校验。

- **技术价值**
  - 体现了与第三方邮箱服务的集成能力。
  - 为登录注册流程增加安全校验环节，提升项目完整度。

---

## 三、典型业务链路示例

### 1. 用户登录与后台跳转

1. 用户访问 `/toLoginPage`，填写用户名与密码。
2. 表单提交到 `/login`，由 Spring Security 过滤器链拦截。
3. `CustomUserDetailsService` 根据用户名加载数据库中的用户与角色。
4. 使用 `BCryptPasswordEncoder` 校验密码。
5. 认证通过后，交由 `CustomAuthenticationSuccessHandler` 判断用户角色并重定向：
   - 买家：前台首页或买家后台页面。
   - 卖家：卖家控制台 `/seller/dashboard`。
   - 管理员：管理后台 `/admin/dashboard`。

### 2. 买家下单与支付

1. 买家在商品详情页点击“立即购买”或在购物车页面选择结算，调用 `/order/buyNow` 或 `/order/checkoutCart`。
2. 后端创建订单记录，包含用户 ID、商品信息、金额、状态等。
3. 页面跳转到支付确认页，点击“去支付”时，请求 `/pay/alipay/page`。
4. 后端构造支付宝支付请求，跳转到沙箱支付页面完成支付。
5. 支付宝异步/同步回调更新订单状态，并跳转到已支付/未支付/已完成等结果页面。

### 3. 买家与卖家聊天

1. 在商品详情页或订单详情页点击“联系卖家”，创建或打开一条会话（Conversation）。
2. 页面加载 `chat.html`，前端通过 SockJS 连接 `/ws-chat` 并订阅对应会话的消息通道。
3. 前端调用 `/api/chat/conversation/{cid}/messages` 获取历史消息并展示。
4. 用户输入消息，调用 REST 或 WebSocket 接口发送到服务器。
5. 服务端持久化消息并推送给对方用户，对话双方页面实时更新。

---

## 四、小结

本项目围绕 “校园便利/二手交易” 业务，综合运用了 **Spring Boot、Spring Security、MyBatis、PageHelper、Druid、WebSocket、支付宝 SDK、邮件服务** 等核心技术，完成了从认证授权、数据持久化、实时通信到第三方支付集成的完整闭环。  
这些技术模块之间通过清晰的分层和接口解耦，既能满足课程设计对功能与复杂度的要求，也便于后续扩展与部署上线。





