# Nacos-Learning 项目规范文档

## 📋 项目概述

本项目是一个基于 Spring Cloud Alibaba 的微服务学习项目，使用标准的微服务架构模式。

## 🛠️ 技术栈规范

### 核心要求

#### 1. 数据库：MySQL（必须）
- ✅ **必须使用**: MySQL 8.0+
- ❌ **禁止使用**: PostgreSQL、MongoDB、Oracle 等其他数据库
- **驱动依赖**: `mysql-connector-java:8.0.33`
- **配置位置**: Nacos 配置中心

```xml
<dependency>
    <groupId>mysql</groupId>
    <artifactId>mysql-connector-java</artifactId>
    <version>8.0.33</version>
</dependency>
```

#### 2. 消息中间件：RocketMQ（必须）
- ✅ **必须使用**: Apache RocketMQ
- ❌ **禁止使用**: RabbitMQ、Kafka、ActiveMQ 等其他消息中间件
- **依赖**: `spring-cloud-starter-alibaba-rocketmq:2021.0.5.0`

```xml
<dependency>
    <groupId>com.alibaba.cloud</groupId>
    <artifactId>spring-cloud-starter-alibaba-rocketmq</artifactId>
    <version>2021.0.5.0</version>
</dependency>
```

#### 3. 微服务架构：Spring Cloud（必须）
- ✅ **必须使用**: Spring Cloud 2021.0.9
- ✅ **必须使用**: Spring Boot 2.7.18
- ✅ **必须使用**: Spring Cloud Alibaba 2021.0.5.0

#### 4. 服务发现与配置中心：Nacos（必须）
- ✅ **必须使用**: Nacos 作为服务注册中心和配置中心
- ❌ **禁止使用**: Eureka、Consul、Zookeeper 等其他服务发现组件
- **依赖**:
  - `spring-cloud-starter-alibaba-nacos-discovery`
  - `spring-cloud-starter-alibaba-nacos-config`

#### 5. API 网关：Spring Cloud Gateway（必须）
- ✅ **必须使用**: Spring Cloud Gateway
- ❌ **禁止使用**: Zuul、Kong 等其他网关
- **依赖**: `spring-cloud-starter-gateway`

#### 6. 服务间通信：OpenFeign（必须）
- ✅ **必须使用**: OpenFeign 进行服务间调用
- ❌ **禁止使用**: RestTemplate、WebClient 直接调用
- **依赖**: `spring-cloud-starter-openfeign`

## 📁 项目结构规范

### 模块命名规范

```
nacos-learning/
├── nacos-common/              # 公共模块（实体类、工具类等）
├── nacos-gateway/             # API 网关模块
├── nacos-provider/            # 服务提供者示例
├── nacos-consumer/            # 服务消费者示例
├── user-service/              # 用户服务
├── order-service/             # 订单服务
└── nacos-config-examples/     # Nacos 配置示例文件
```

### 包结构规范

```
com.zxx.learning
├── {module-name}/
│   ├── controller/            # 控制器层
│   ├── service/               # 业务逻辑层
│   ├── feign/                 # Feign 客户端
│   ├── config/                # 配置类
│   ├── mq/                    # 消息队列相关
│   │   ├── producer/          # 消息生产者
│   │   └── consumer/          # 消息消费者
│   └── {Module}Application.java  # 启动类
└── common/
    └── entity/                # 公共实体类
```

## 📦 依赖管理规范

### 父 POM 版本管理

所有版本必须在父 `pom.xml` 的 `<properties>` 中统一管理：

```xml
<properties>
    <java.version>1.8</java.version>
    <spring-boot.version>2.7.18</spring-boot.version>
    <spring-cloud.version>2021.0.9</spring-cloud.version>
    <spring-cloud-alibaba.version>2021.0.5.0</spring-cloud-alibaba.version>
    <mysql-connector.version>8.0.33</mysql-connector.version>
</properties>
```

### 必需依赖清单

#### 所有服务模块必须包含

```xml
<!-- Nacos 服务发现 -->
<dependency>
    <groupId>com.alibaba.cloud</groupId>
    <artifactId>spring-cloud-starter-alibaba-nacos-discovery</artifactId>
</dependency>

<!-- Nacos 配置中心 -->
<dependency>
    <groupId>com.alibaba.cloud</groupId>
    <artifactId>spring-cloud-starter-alibaba-nacos-config</artifactId>
</dependency>

<!-- Web 支持 -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-web</artifactId>
</dependency>
```

#### 需要服务间调用的模块

```xml
<!-- OpenFeign -->
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-openfeign</artifactId>
</dependency>

<!-- Spring Cloud LoadBalancer（OpenFeign 必需，用于服务名解析和负载均衡） -->
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-loadbalancer</artifactId>
</dependency>
```

**⚠️ 重要提示**：
- 使用 OpenFeign 时，**必须同时添加** `spring-cloud-starter-loadbalancer` 依赖
- 否则会出现错误：`No Feign Client for loadBalancing defined. Did you forget to include spring-cloud-starter-loadbalancer?`
- LoadBalancer 会与 Nacos Discovery 配合，将服务名（如 `user-service`）解析为实际的服务实例地址

#### 需要消息队列的模块

```xml
<!-- RocketMQ -->
<dependency>
    <groupId>com.alibaba.cloud</groupId>
    <artifactId>spring-cloud-starter-alibaba-rocketmq</artifactId>
    <version>2021.0.5.0</version>
</dependency>
```

#### 需要数据库的模块

```xml
<!-- MySQL 驱动 -->
<dependency>
    <groupId>mysql</groupId>
    <artifactId>mysql-connector-java</artifactId>
    <version>8.0.33</version>
</dependency>

<!-- JDBC 支持 -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-jdbc</artifactId>
</dependency>
```

#### Gateway 模块专用

```xml
<!-- Gateway -->
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-gateway</artifactId>
</dependency>

<!-- Redis 限流 -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-data-redis-reactive</artifactId>
</dependency>
```

## ⚙️ 配置规范

### Nacos 配置要求

#### 1. 服务发现配置

所有服务必须在 `application.yml` 中配置：

```yaml
spring:
  cloud:
    nacos:
      discovery:
        server-addr: ${NACOS_SERVER:localhost:8848}
        namespace: ${NACOS_NAMESPACE:}
        group: DEFAULT_GROUP
```

#### 2. 配置中心配置

```yaml
spring:
  cloud:
    nacos:
      config:
        server-addr: ${NACOS_SERVER:localhost:8848}
        namespace: ${NACOS_NAMESPACE:}
        group: DEFAULT_GROUP
        file-extension: yaml
        shared-configs:
          - data-id: nacos-${spring.application.name}-${spring.profiles.active}.yaml
            group: DEFAULT_GROUP
            refresh: true
```

#### 3. 配置文件命名规范

- 格式: `nacos-{service-name}-{profile}.yaml`
- 示例: `nacos-provider-dev.yaml`, `nacos-gateway-prod.yaml`
- 位置: `nacos-config-examples/` 目录

#### 4. 配置编写与下发流程（Skill）

- 所有微服务的**业务配置和环境配置**（如数据源、业务开关、超时、限流规则等）：
  - 必须先在 `nacos-config-examples/` 目录下编写对应的 YAML 示例文件；
  - 使用与示例文件同名的 Data ID 上传到 Nacos（例如：`nacos-user-service-dev.yaml`）；
  - 各服务本地的 `application.yml` **只保留**：
    - `server.port`
    - `spring.application.name`
    - Nacos 注册中心和配置中心的连接信息
    - `spring.config.import` 或 `spring.cloud.nacos.config.shared-configs` 等“从 Nacos 拉配置”的入口；
- 禁止在各服务的 `application.yml` 中直接硬编码数据库连接、业务参数等配置项，这些必须只存在于 Nacos 中（源头在 `nacos-config-examples/`）。

### 数据库配置规范

数据库配置必须在 Nacos 配置中心的配置文件中：

```yaml
spring:
  datasource:
    driver-class-name: com.mysql.cj.jdbc.Driver
    url: jdbc:mysql://${DB_HOST:localhost}:${DB_PORT:3306}/${DB_NAME:nacolearn}?useUnicode=true&characterEncoding=utf8&useSSL=false&serverTimezone=Asia/Shanghai
    username: ${DB_USER:root}
    password: ${DB_PASSWORD:password}
    hikari:
      maximum-pool-size: 10
      minimum-idle: 5
      connection-timeout: 30000
```

### Gateway 路由配置规范

```yaml
spring:
  cloud:
    gateway:
      routes:
        - id: user-service
          uri: lb://user-service
          predicates:
            - Path=/api/user/**
          filters:
            - StripPrefix=1
        - id: order-service
          uri: lb://order-service
          predicates:
            - Path=/api/order/**
          filters:
            - StripPrefix=1
```

## 💻 代码规范

### Java 版本与兼容性（必须遵守）

- **统一 Java 版本：`1.8`（Java 8）**
  - 所有服务模块的 `source` / `target` 版本必须为 `1.8`。
  - 编写任何 Java 代码时，都必须确保在 **Java 8 环境下可以正常编译**。

- **禁止使用的 JDK 9+ API（仅列常见）**
  - 禁止使用以下工厂方法（会导致类似“找不到符号: 方法 of(...) 位置: 接口 java.util.Map”报错）：
    - `Map.of(...)`
    - `List.of(...)`
    - `Set.of(...)`
    - `Map.copyOf(...)`
  - 统一替代写法（示例）：
    ```java
    // ❌ 禁止
    // Map<String, Object> body = Map.of("username", username, "password", password);

    // ✅ 推荐（Java 8 兼容）
    Map<String, Object> body = new HashMap<>();
    body.put("username", username);
    body.put("password", password);
    ```

- **语法限制**
  - 不使用 Java 10+ 的 `var` 局部变量推断。
  - 不使用 Java 14+ 的 `record`、`switch` 表达式、文本块字符串 `"""..."""` 等新语法。

- **可使用的 Java 8 特性**
  - 允许使用：Lambda 表达式、方法引用、`Stream`、`Optional`、接口 `default` 方法、`java.time` 时间 API 等，但前提是 **该类 / 方法在 Java 8 已存在**。

> ⚠️ **开发自检**：新增或修改 Java 代码时，如果不确定某个类或方法是否为 Java 8 就存在，一律采用更传统、保守的写法（如使用 `new HashMap<>() + put`），避免再次出现 JDK 版本不兼容问题。

### Feign 客户端规范

```java
@FeignClient(name = "user-service", path = "/api/user")
public interface UserServiceClient {
    
    @GetMapping("/{id}")
    User getUserById(@PathVariable Long id);
    
    @PostMapping
    User createUser(@RequestBody User user);
}
```

**规范要求**:
- 接口命名: `{ServiceName}Client`
- 必须使用 `@FeignClient` 注解
- `name` 或 `value` 必须对应 Nacos 中的服务名
- 放在 `feign` 包下

### 服务间调用规范

**✅ 正确示例**:
```java
@Service
public class OrderService {
    
    @Autowired
    private UserServiceClient userServiceClient;  // 使用 Feign
    
    public Order createOrder(Long userId) {
        User user = userServiceClient.getUserById(userId);
        // ...
    }
}
```

**❌ 错误示例**:
```java
// 禁止使用 RestTemplate 直接调用
RestTemplate restTemplate = new RestTemplate();
User user = restTemplate.getForObject("http://user-service/api/user/1", User.class);
```

### RocketMQ 使用规范

#### 消息生产者

```java
@Service
public class OrderMessageProducer {
    
    @Autowired
    private RocketMQTemplate rocketMQTemplate;
    
    public void sendOrderCreated(Order order) {
        rocketMQTemplate.convertAndSend("order-topic:order-created", order);
    }
}
```

#### 消息消费者

```java
@Component
@RocketMQMessageListener(
    topic = "order-topic",
    selectorExpression = "order-created",
    consumerGroup = "order-consumer-group"
)
public class OrderMessageConsumer implements RocketMQListener<Order> {
    
    @Override
    public void onMessage(Order order) {
        // 处理消息
    }
}
```

## 🌐 API 接口规范

### 统一响应格式

所有 API 接口必须使用统一的响应格式，确保前端能够正确解析和展示数据。

#### 标准响应格式

```json
{
  "success": true/false,
  "msg": "操作结果消息",
  "data": {数据对象}
}
```

#### 成功响应示例

```json
{
  "success": true,
  "msg": "操作成功",
  "data": {
    "id": 1,
    "username": "admin",
    "email": "admin@example.com"
  }
}
```

#### 失败响应示例

```json
{
  "success": false,
  "msg": "用户名已存在"
}
```

**规范要求**:
- ✅ 所有接口必须返回 `{success, msg, data}` 格式
- ✅ `success` 字段：`true` 表示成功，`false` 表示失败
- ✅ `msg` 字段：操作结果消息，成功时可为空或提示信息，失败时必须包含错误原因
- ✅ `data` 字段：成功时包含返回数据，失败时可为 `null` 或省略
- ❌ 禁止直接返回实体对象（如 `User`）
- ❌ 禁止使用其他格式（如 `{message, code, result}`）

### 接口实现示例

```java
@RestController
@RequestMapping("/api/user")
public class UserController {
    
    @GetMapping("/{id}")
    public Map<String, Object> getUserById(@PathVariable Long id) {
        Map<String, Object> result = new HashMap<>();
        try {
            User user = userService.getUserById(id);
            result.put("success", true);
            result.put("msg", "查询成功");
            result.put("data", user);
        } catch (Exception e) {
            result.put("success", false);
            result.put("msg", e.getMessage());
        }
        return result;
    }
}
```

### 管理员接口规范

#### 接口路径规范

管理员接口统一使用 `/api/admin/**` 路径前缀：

- 用户管理：`/api/admin/user/**`
- 订单管理：`/api/admin/order/**`
- 其他管理功能：`/api/admin/{resource}/**`

#### Gateway 路由配置

管理员接口路由配置示例：

```yaml
spring:
  cloud:
    gateway:
      routes:
        - id: admin-service-route
          uri: lb://user-service
          predicates:
            - Path=/api/admin/**
          filters:
            - StripPrefix=1  # 去掉 /api 前缀
```

#### 权限要求

- ✅ 所有管理员接口必须验证 `admin` 角色权限
- ✅ 使用 Spring Security 或自定义拦截器进行权限验证
- ❌ 禁止未授权访问管理员接口

#### 请求响应格式

管理员接口同样遵循统一响应格式：

**创建用户示例**：
```json
// 请求
POST /api/admin/user
{
  "username": "newuser",
  "password": "password123",
  "email": "user@example.com",
  "role": "USER",
  "status": "ACTIVE"
}

// 响应
{
  "success": true,
  "msg": "创建成功",
  "data": {
    "id": 1,
    "username": "newuser",
    "email": "user@example.com"
  }
}
```

## ⚠️ 错误处理规范

### 异常分类

系统异常分为以下几类，需要统一处理：

1. **参数错误** (`IllegalArgumentException`)
   - 请求参数不合法、缺失或格式错误
   - 响应格式：`{success: false, msg: "参数错误：具体原因"}`

2. **业务异常** (`RuntimeException`)
   - 业务逻辑错误，如用户名已存在、订单不存在等
   - 响应格式：`{success: false, msg: "业务错误：具体原因"}`

3. **系统异常** (`Exception`)
   - 系统内部错误、数据库连接失败等
   - 响应格式：`{success: false, msg: "系统错误，请稍后重试"}`（避免暴露内部错误）

### 全局异常处理器

#### 各服务异常处理器

每个微服务必须实现全局异常处理器，统一处理异常并返回标准格式。

**文件位置**：
- `user-service/src/main/java/com/zxx/learning/user/exception/GlobalExceptionHandler.java`
- `auth-service/src/main/java/com/zxx/learning/auth/exception/GlobalExceptionHandler.java`
- `order-service/src/main/java/com/zxx/learning/order/exception/GlobalExceptionHandler.java`

**实现示例**：

```java
@RestControllerAdvice
public class GlobalExceptionHandler {
    
    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);
    
    @ExceptionHandler(IllegalArgumentException.class)
    public Map<String, Object> handleIllegalArgumentException(IllegalArgumentException e) {
        logger.warn("参数错误: {}", e.getMessage());
        Map<String, Object> result = new HashMap<>();
        result.put("success", false);
        result.put("msg", "参数错误：" + e.getMessage());
        return result;
    }
    
    @ExceptionHandler(RuntimeException.class)
    public Map<String, Object> handleRuntimeException(RuntimeException e) {
        logger.error("业务异常: {}", e.getMessage(), e);
        Map<String, Object> result = new HashMap<>();
        result.put("success", false);
        result.put("msg", e.getMessage());
        return result;
    }
    
    @ExceptionHandler(Exception.class)
    public Map<String, Object> handleException(Exception e) {
        logger.error("系统异常: {}", e.getMessage(), e);
        Map<String, Object> result = new HashMap<>();
        result.put("success", false);
        result.put("msg", "系统错误，请稍后重试");
        return result;
    }
}
```

#### Gateway 异常处理器

Gateway 作为统一入口，需要处理路由异常、服务不可用等异常。

**文件位置**：
- `nacos-learning/nacos-gateway/src/main/java/com/zxx/learning/gateway/exception/GlobalExceptionHandler.java`

**响应格式**：

```json
{
  "success": false,
  "msg": "错误信息",
  "status": 500,
  "timestamp": 1234567890
}
```

**实现示例**：

```java
@RestControllerAdvice
public class GlobalExceptionHandler {
    
    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);
    
    @ExceptionHandler(Exception.class)
    public Map<String, Object> handleException(Exception e) {
        logger.error("Gateway异常: {}", e.getMessage(), e);
        Map<String, Object> result = new HashMap<>();
        result.put("success", false);
        result.put("msg", e.getMessage());
        result.put("status", HttpStatus.INTERNAL_SERVER_ERROR.value());
        result.put("timestamp", System.currentTimeMillis());
        return result;
    }
}
```

**规范要求**：
- ✅ Gateway 异常响应必须使用 `msg` 字段（而非 `message`）
- ✅ 保持与统一响应格式一致
- ✅ 记录异常日志，便于排查问题

### 前端错误处理

前端需要统一处理错误响应，优先使用 `msg` 字段，兼容 `message` 字段。

**文件位置**：
- `naco-web/src/api/request.js`

**处理逻辑**：

```javascript
// 错误处理示例
axios.interceptors.response.use(
  response => {
    const res = response.data;
    if (res.success === false) {
      // 优先使用 msg 字段
      const errorMsg = res.msg || res.message || '操作失败';
      // 显示错误提示
      Message.error(errorMsg);
      return Promise.reject(new Error(errorMsg));
    }
    return res;
  },
  error => {
    // HTTP 状态码错误处理
    if (error.response) {
      const res = error.response.data;
      const errorMsg = res?.msg || res?.message || error.message || '请求失败';
      Message.error(errorMsg);
    } else {
      Message.error('网络错误，请检查网络连接');
    }
    return Promise.reject(error);
  }
);
```

### 错误处理最佳实践

1. **日志记录**
   - ✅ 所有异常必须记录日志
   - ✅ 使用适当的日志级别（`warn` 用于参数错误，`error` 用于系统异常）
   - ✅ 记录异常堆栈信息，便于排查问题

2. **错误信息友好性**
   - ✅ 给用户友好的错误提示
   - ❌ 避免暴露系统内部错误（如数据库连接字符串、内部异常堆栈）
   - ✅ 业务异常返回具体原因（如"用户名已存在"）
   - ✅ 系统异常返回通用提示（如"系统错误，请稍后重试"）

3. **向后兼容**
   - ✅ 如果其他服务通过 Feign 调用，需要考虑兼容性
   - ✅ 前端错误处理兼容 `msg` 和 `message` 字段

4. **权限验证**
   - ✅ 管理员接口必须验证 `admin` 角色权限
   - ✅ 权限验证失败返回：`{success: false, msg: "无权限访问"}`

## ✅ 检查清单

在添加新模块或功能时，请确保：

### 新服务模块检查
- [ ] 添加了 Nacos Discovery 依赖
- [ ] 添加了 Nacos Config 依赖
- [ ] 配置了服务发现和配置中心
- [ ] 在 Nacos 配置中心创建了配置文件
- [ ] 配置文件命名符合规范

### 数据库相关检查
- [ ] 使用了 MySQL 数据库（不是其他数据库）
- [ ] 添加了 `mysql-connector-java` 依赖
- [ ] 数据库配置在 Nacos 配置中心
- [ ] 连接池配置合理

### 消息队列相关检查
- [ ] 使用了 RocketMQ（不是其他 MQ）
- [ ] 添加了 `spring-cloud-starter-alibaba-rocketmq` 依赖
- [ ] 生产者类命名规范
- [ ] 消费者类命名规范
- [ ] 使用了 `@RocketMQMessageListener` 注解

### 服务间调用检查
- [ ] 使用了 OpenFeign（不是 RestTemplate）
- [ ] **添加了 `spring-cloud-starter-loadbalancer` 依赖（OpenFeign 必需）**
- [ ] Feign 客户端放在 `feign` 包下
- [ ] 接口命名符合规范
- [ ] 通过 Nacos 服务发现调用

### Gateway 模块检查
- [ ] 使用了 Spring Cloud Gateway
- [ ] 配置了服务发现路由
- [ ] 配置了限流规则
- [ ] 配置了 CORS

## 🚫 禁止事项

1. ❌ **禁止使用其他数据库**（如 PostgreSQL、MongoDB）
2. ❌ **禁止使用其他消息中间件**（如 RabbitMQ、Kafka）
3. ❌ **禁止使用其他服务发现组件**（如 Eureka、Consul）
4. ❌ **禁止使用其他网关**（如 Zuul）
5. ❌ **禁止使用 RestTemplate 进行服务间调用**
6. ❌ **禁止在代码中硬编码配置**（必须使用 Nacos 配置中心）

## 📝 版本信息

- **Java**: 1.8
- **Spring Boot**: 2.7.18
- **Spring Cloud**: 2021.0.9
- **Spring Cloud Alibaba**: 2021.0.5.0
- **MySQL Connector**: 8.0.33

## 📚 参考文档

- [Spring Cloud Alibaba 官方文档](https://github.com/alibaba/spring-cloud-alibaba)
- [Nacos 官方文档](https://nacos.io/)
- [Spring Cloud Gateway 文档](https://spring.io/projects/spring-cloud-gateway)
- [OpenFeign 文档](https://spring.io/projects/spring-cloud-openfeign)
- [RocketMQ 文档](https://rocketmq.apache.org/)
