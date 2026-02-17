package com.zxx.learning.auth.config;

import cn.dev33.satoken.dao.SaTokenDao;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.util.concurrent.TimeUnit;

/**
 * Sa-Token Redis 存储配置
 * 
 * <p>手动实现 SaTokenDao 接口，使用 Redis 存储 token，确保与 gateway 共享 token 数据。</p>
 * 
 * <p><b>技术栈声明</b>：本项目统一使用 StringRedisTemplate（同步版本），不使用 ReactiveStringRedisTemplate。</p>
 * 
 * <p>说明：
 * <ul>
 *     <li>Redis key 前缀：satoken:token:（token 值）和 satoken:token-session:（会话对象）</li>
 *     <li>与 gateway 使用相同的 key 前缀，确保两个服务共享数据</li>
 *     <li>所有 Redis 操作都包含异常处理</li>
 * </ul>
 * </p>
 * 
 * @author zxx
 */
@Slf4j
@Configuration
@RequiredArgsConstructor
public class SaTokenRedisConfig {

    private final StringRedisTemplate stringRedisTemplate;

    @Bean
    public SaTokenDao saTokenDao() {
        return new SaTokenDaoRedisImpl(stringRedisTemplate);
    }

    /**
     * 手动实现 SaTokenDao，使用 Redis 存储
     */
    @RequiredArgsConstructor
    public static class SaTokenDaoRedisImpl implements SaTokenDao {

        private static final String TOKEN_PREFIX = "satoken:token:";
        private static final String TOKEN_SESSION_PREFIX = "satoken:token-session:";

        private final StringRedisTemplate redisTemplate;

        @Override
        public String get(String key) {
            try {
                return redisTemplate.opsForValue().get(TOKEN_PREFIX + key);
            } catch (Exception e) {
                log.error("从 Redis 读取 token 异常, key={}", key, e);
                return null;
            }
        }

        @Override
        public void set(String key, String value, long timeout) {
            try {
                if (timeout > 0) {
                    redisTemplate.opsForValue().set(TOKEN_PREFIX + key, value, timeout, TimeUnit.SECONDS);
                } else {
                    redisTemplate.opsForValue().set(TOKEN_PREFIX + key, value);
                }
                log.debug("向 Redis 写入 token, key={}, timeout={}", key, timeout);
            } catch (Exception e) {
                log.error("向 Redis 写入 token 异常, key={}", key, e);
            }
        }

        @Override
        public void update(String key, String value) {
            try {
                Long expire = redisTemplate.getExpire(TOKEN_PREFIX + key);
                if (expire != null && expire > 0) {
                    redisTemplate.opsForValue().set(TOKEN_PREFIX + key, value, expire, TimeUnit.SECONDS);
                } else {
                    redisTemplate.opsForValue().set(TOKEN_PREFIX + key, value);
                }
                log.debug("更新 Redis token, key={}", key);
            } catch (Exception e) {
                log.error("更新 Redis token 异常, key={}", key, e);
            }
        }

        @Override
        public void delete(String key) {
            try {
                redisTemplate.delete(TOKEN_PREFIX + key);
                log.debug("从 Redis 删除 token, key={}", key);
            } catch (Exception e) {
                log.error("从 Redis 删除 token 异常, key={}", key, e);
            }
        }

        @Override
        public long getTimeout(String key) {
            try {
                Long expire = redisTemplate.getExpire(TOKEN_PREFIX + key);
                return expire != null ? expire : -1;
            } catch (Exception e) {
                log.error("获取 Redis token 过期时间异常, key={}", key, e);
                return -1;
            }
        }

        @Override
        public void updateTimeout(String key, long timeout) {
            try {
                if (timeout > 0) {
                    redisTemplate.expire(TOKEN_PREFIX + key, timeout, TimeUnit.SECONDS);
                    log.debug("更新 Redis token 过期时间, key={}, timeout={}", key, timeout);
                }
            } catch (Exception e) {
                log.error("更新 Redis token 过期时间异常, key={}", key, e);
            }
        }

        @Override
        public Object getObject(String key) {
            try {
                return redisTemplate.opsForValue().get(TOKEN_SESSION_PREFIX + key);
            } catch (Exception e) {
                log.error("从 Redis 读取 token 会话异常, key={}", key, e);
                return null;
            }
        }

        @Override
        public void setObject(String key, Object value, long timeout) {
            try {
                String valueStr = value != null ? value.toString() : "";
                if (timeout > 0) {
                    redisTemplate.opsForValue().set(TOKEN_SESSION_PREFIX + key, valueStr, timeout, TimeUnit.SECONDS);
                } else {
                    redisTemplate.opsForValue().set(TOKEN_SESSION_PREFIX + key, valueStr);
                }
                log.debug("向 Redis 写入 token 会话, key={}, timeout={}", key, timeout);
            } catch (Exception e) {
                log.error("向 Redis 写入 token 会话异常, key={}", key, e);
            }
        }

        @Override
        public void updateObject(String key, Object value) {
            try {
                String valueStr = value != null ? value.toString() : "";
                Long expire = redisTemplate.getExpire(TOKEN_SESSION_PREFIX + key);
                if (expire != null && expire > 0) {
                    redisTemplate.opsForValue().set(TOKEN_SESSION_PREFIX + key, valueStr, expire, TimeUnit.SECONDS);
                } else {
                    redisTemplate.opsForValue().set(TOKEN_SESSION_PREFIX + key, valueStr);
                }
                log.debug("更新 Redis token 会话, key={}", key);
            } catch (Exception e) {
                log.error("更新 Redis token 会话异常, key={}", key, e);
            }
        }

        @Override
        public void deleteObject(String key) {
            try {
                redisTemplate.delete(TOKEN_SESSION_PREFIX + key);
                log.debug("从 Redis 删除 token 会话, key={}", key);
            } catch (Exception e) {
                log.error("从 Redis 删除 token 会话异常, key={}", key, e);
            }
        }

        @Override
        public long getObjectTimeout(String key) {
            try {
                Long expire = redisTemplate.getExpire(TOKEN_SESSION_PREFIX + key);
                return expire != null ? expire : -1;
            } catch (Exception e) {
                log.error("获取 Redis token 会话过期时间异常, key={}", key, e);
                return -1;
            }
        }

        @Override
        public void updateObjectTimeout(String key, long timeout) {
            try {
                if (timeout > 0) {
                    redisTemplate.expire(TOKEN_SESSION_PREFIX + key, timeout, TimeUnit.SECONDS);
                    log.debug("更新 Redis token 会话过期时间, key={}, timeout={}", key, timeout);
                }
            } catch (Exception e) {
                log.error("更新 Redis token 会话过期时间异常, key={}", key, e);
            }
        }

        @Override
        public java.util.List<String> searchData(String prefix, String keyword, int start, int size, boolean sortType) {
            try {
                // 搜索功能：根据前缀和关键词搜索 token
                // 由于 Redis String 操作不支持复杂搜索，这里返回空列表
                // 如果需要搜索功能，可以使用 Redis Scan 命令或维护索引
                log.debug("搜索 token 数据, prefix={}, keyword={}, start={}, size={}, sortType={}", 
                    prefix, keyword, start, size, sortType);
                return new java.util.ArrayList<>();
            } catch (Exception e) {
                log.error("搜索 token 数据异常, prefix={}, keyword={}", prefix, keyword, e);
                return new java.util.ArrayList<>();
            }
        }
    }
}
