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
