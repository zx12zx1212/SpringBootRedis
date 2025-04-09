package com.example.springBoot_Redis.cache;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

import com.example.springBoot_Redis.util.RedisUtil;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class CacheProxy {

    @Setter(onMethod_ = @Autowired)
    private RedisUtil redisUtil;

    private static final long DEFAULT_CACHE_TIME = 1; // 預設緩存 1 小時
    private static final TimeUnit DEFAULT_TIME_UNIT = TimeUnit.HOURS;

    /**
     * 嘗試從緩存中獲取資料，如果緩存中不存在則調用提供的查詢方法查詢資料並將其存入緩存。
     * 鍵值會根據類別名稱和方法名稱自動生成。
     *
     * @param clazz      要查詢的類別
     * @param methodName 要查詢的方法名稱
     * @param query      查詢資料的方法
     * @param cacheTime  緩存過期時間（秒為單位）
     * @param unit       過期時間的單位（如：秒、分鐘等）
     * @param <T>        緩存的資料類型
     * @return 查詢到的資料，若查詢結果為 null 則返回 null
     */
    public <T> Optional<T> getOneCache(Class<?> clazz, String methodName, Class<T> type, Supplier<Optional<T>> query, long cacheTime, TimeUnit unit, Object... params) {
        String key = generateCacheKey(clazz, methodName);

        T cachedData = redisUtil.get(key, type);
        if (cachedData != null) {
            return Optional.of(cachedData);
        }

        Optional<T> result = query.get();
        if (result.isPresent()) {
            redisUtil.set(key, result, cacheTime, unit);
        }

        return result;
    }

    public <T> Optional<T> getOneCache(Class<?> clazz, String methodName, Class<T> type, Supplier<Optional<T>> query) {
        return getOneCache(clazz, methodName, type, query, DEFAULT_CACHE_TIME, DEFAULT_TIME_UNIT);
    }

    public <T> List<T> getListCache(Class<?> clazz, String methodName, Class<T> type, Supplier<List<T>> query, long cacheTime, TimeUnit unit) {
        String key = generateCacheKey(clazz, methodName);

        List<T> cachedData = redisUtil.getList(key, type);
        if (cachedData != null && !cachedData.isEmpty()) {
            return cachedData;
        }

        List<T> result = query.get();
        if (result != null && !result.isEmpty()) {
            // 設定快取
            redisUtil.set(key, result, cacheTime, unit);
        }

        return result;
    }

    public <T> List<T> getListCache(Class<?> clazz, String methodName, Class<T> type, Supplier<List<T>> query) {
        return getListCache(clazz, methodName, type, query, DEFAULT_CACHE_TIME, DEFAULT_TIME_UNIT);
    }

    /**
     * 根據類別名稱和方法名稱生成 Redis 鍵
     *
     * @param clazz      要查詢的類別
     * @param methodName 要查詢的方法名稱
     * @return 自動生成的 Redis 鍵
     */
    private String generateCacheKey(Class<?> clazz, String methodName) {
        return clazz.getSimpleName() + ":" + methodName;
    }
}
