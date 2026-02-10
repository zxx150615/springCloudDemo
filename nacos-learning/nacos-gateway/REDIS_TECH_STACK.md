# Redis 技术栈规范

## 项目技术框架声明

**本项目统一使用 `StringRedisTemplate`（同步版本）进行所有 Redis 操作，不使用 `ReactiveStringRedisTemplate`（响应式版本）。**

---

## 技术选型说明

### 为什么选择 StringRedisTemplate（同步版本）？

1. **与 Sa-Token 兼容性**
   - `StpInterface.getRoleList()` 和 `getPermissionList()` 是同步方法
   - 响应式 Redis 在同步方法中使用会导致线程阻塞异常
   - 同步版本可以直接在同步上下文中使用，无需转换

2. **代码简洁性**
   - 同步代码更直观，易于理解和维护
   - 不需要处理 `Mono`、`Flux` 等响应式类型
   - 错误处理更简单

3. **性能考虑**
   - 对于角色、技能等低频操作，同步性能足够
   - 避免响应式编程的复杂性带来的性能开销

### 为什么不使用 ReactiveStringRedisTemplate？

1. **线程安全问题**
   - 在响应式线程（如 `reactor-http-epoll-1`）中使用 `block()` 会抛出异常
   - 错误信息：`blockOptional() is blocking, which is not supported in thread reactor-http-epoll-1`

2. **架构不匹配**
   - Gateway 虽然是响应式的，但 Sa-Token 的权限接口是同步的
   - 混合使用会导致复杂的线程上下文切换问题

---

## 依赖配置

### pom.xml 依赖说明

```xml
<!-- 同步 Redis 依赖 - 用于所有业务代码 -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-data-redis</artifactId>
</dependency>

<!-- 响应式 Redis 依赖 - 仅用于 Gateway 限流功能（框架内部使用） -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-data-redis-reactive</artifactId>
</dependency>
```

**注意**：
- `spring-boot-starter-data-redis` 用于所有业务代码（角色、技能存储等）
- `spring-boot-starter-data-redis-reactive` 仅用于 Gateway 的 `RequestRateLimiter` 限流功能（框架内部使用）
- **所有业务代码必须使用同步版本**

---

## 使用规范

### ✅ 正确示例

```java
import org.springframework.data.redis.core.StringRedisTemplate;

@Component
@RequiredArgsConstructor
public class RedisRoleStore {
    // 使用同步的 StringRedisTemplate
    private final StringRedisTemplate redisTemplate;
    
    public void addRole(String loginId, String role) {
        // 直接使用同步方法
        redisTemplate.opsForSet().add(key, value);
    }
    
    public List<String> getRoles(String loginId) {
        // 直接返回结果，无需 block()
        Set<String> roles = redisTemplate.opsForSet().members(key);
        return new ArrayList<>(roles);
    }
}
```

### ❌ 错误示例

```java
import org.springframework.data.redis.core.ReactiveStringRedisTemplate;

@Component
@RequiredArgsConstructor
public class RedisRoleStore {
    // ❌ 不要使用响应式版本
    private final ReactiveStringRedisTemplate redisTemplate;
    
    public List<String> getRoles(String loginId) {
        // ❌ 在同步方法中使用 block() 会抛出异常
        return redisTemplate.opsForSet()
            .members(key)
            .collectList()
            .block(); // 会抛出 IllegalStateException
    }
}
```

---

## 项目中的使用情况

### 已使用 StringRedisTemplate 的类

1. **RedisRoleStore**
   - 用途：存储和管理用户角色
   - Redis Key：`sa:roles:{loginId}`
   - 操作：添加角色、查询角色列表

2. **RedisSkillStore**
   - 用途：存储和管理用户技能
   - Redis Key：`sa:skills:{loginId}`
   - 操作：添加技能、查询技能列表、移除技能、检查技能

### 使用响应式 Redis 的地方（框架内部）

1. **Gateway RequestRateLimiter**
   - 用途：Gateway 限流功能
   - 说明：这是 Spring Cloud Gateway 框架内部使用的，不在业务代码中直接操作

---

## 开发规范

### 新增 Redis 操作类时

1. **必须使用 `StringRedisTemplate`**
   ```java
   @Component
   @RequiredArgsConstructor
   public class YourRedisStore {
       private final StringRedisTemplate redisTemplate; // ✅ 正确
   }
   ```

2. **禁止使用 `ReactiveStringRedisTemplate`**
   ```java
   @Component
   @RequiredArgsConstructor
   public class YourRedisStore {
       private final ReactiveStringRedisTemplate redisTemplate; // ❌ 禁止
   }
   ```

3. **在类注释中声明使用的技术栈**
   ```java
   /**
    * 使用 Redis 存储 XXX 的实现（基于 StringRedisTemplate，同步版本）。
    * 
    * <p>技术栈：统一使用 StringRedisTemplate，不使用响应式版本。</p>
    */
   ```

---

## 常见问题

### Q: Gateway 是响应式的，为什么不用响应式 Redis？

A: Gateway 虽然是响应式的，但：
- Sa-Token 的权限接口（`StpInterface`）是同步的
- 在同步方法中使用响应式 Redis 会导致线程阻塞异常
- 业务代码（角色、技能）操作频率低，同步性能足够

### Q: 可以混用同步和响应式 Redis 吗？

A: 不推荐。本项目规范：
- **所有业务代码**：统一使用 `StringRedisTemplate`
- **框架功能**（如 Gateway 限流）：由框架内部处理，业务代码不直接操作

### Q: 如果遇到线程阻塞异常怎么办？

A: 检查是否误用了 `ReactiveStringRedisTemplate`：
1. 确认使用的是 `StringRedisTemplate`
2. 确认没有调用 `block()`、`blockOptional()` 等方法
3. 确认没有使用 `Mono`、`Flux` 等响应式类型

---

## 更新记录

- 2024-XX-XX：创建文档，明确项目统一使用 `StringRedisTemplate`
- 2024-XX-XX：添加使用规范和示例代码

---

**重要提醒**：所有新增的 Redis 操作类都必须遵循此规范，使用 `StringRedisTemplate`（同步版本）。
