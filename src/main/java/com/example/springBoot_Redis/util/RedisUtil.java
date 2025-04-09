package com.example.springBoot_Redis.util;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class RedisUtil {

    @Setter(onMethod_ = @Autowired)
    private RedisTemplate<String, Object> redisTemplate;

    @Setter(onMethod_ = @Autowired)
    private ObjectMapper objectMapper;

    /**
     * 設置單一鍵值對到 Redis 中
     *
     * @param key   Redis 中的鍵
     * @param value 對應的值
     */
    public <T> void set(String key, T value) {
        redisTemplate.opsForValue().set(key, value);  // 這裡會根據值的型別進行序列化
    }

    /**
     * 設定一個帶有過期時間的鍵值對到 Redis 中。
     *
     * @param key   Redis 中的鍵
     * @param value 與鍵對應的值
     * @param timeout 設定的過期時間（該鍵將在此時間後過期）
     * @param unit   過期時間的單位（例如：秒、分鐘等）
     */
    public <T> void set(String key, T value, long timeout, TimeUnit unit) {
        redisTemplate.opsForValue().set(key, value, timeout, unit);
    }


    /**
     * 從 Redis 中根據鍵取得值
     *
     * @param key Redis 中的鍵
     * @return 返回與鍵對應的值
     */
    public <T> T get(String key, Class<T> type) {
        if (!isKeyExist(key) || type == null) {
            return null;
        }
        Object value = redisTemplate.opsForValue().get(key);
        try {
            return type.cast(value);
        } catch (ClassCastException e) {
            log.warn("Unable to cast value of Redis key [{}] to type {}. Actual type is {}.", key, type.getName(), value.getClass().getName(), e);
            return null;
        }
    }

    public <T> List<T> getList(String key, Class<T> elementType) {
        String json = (String) redisTemplate.opsForValue().get(key);
        if (json == null) return Collections.emptyList();

        try {
            return objectMapper.readValue(json, objectMapper.getTypeFactory().constructCollectionType(List.class, elementType));
        } catch (Exception e) {
            log.warn("Failed to deserialize list for key [{}]: {}", key, e.getMessage());
            return Collections.emptyList();
        }
    }

    /**
     * 刪除 Redis 中的指定鍵
     *
     * @param key Redis 中的鍵
     */
    public void deleteKey(String key) {
        redisTemplate.delete(key);
    }

    /**
     * 獲取 Redis 中指定鍵的過期時間
     *
     * @param key Redis 中的鍵
     * @return 鍵的過期時間（秒為單位），如果鍵沒有設置過期時間，則返回 -1
     */
    public long getExpireTime(String key) {
        Long expireTime = redisTemplate.getExpire(key);
        return (expireTime != null) ? expireTime : -1L;  // 如果為 null，則返回 -1L
    }

    /**
     * 檢查 Redis 中指定的鍵是否存在
     *
     * @param key Redis 中的鍵
     * @return 如果鍵存在，返回 true；否則返回 false
     */
    public boolean isKeyExist(String key) {
        return redisTemplate.hasKey(key);
    }
}
