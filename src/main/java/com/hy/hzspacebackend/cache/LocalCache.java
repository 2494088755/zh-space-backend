package com.hy.hzspacebackend.cache;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

public class LocalCache {
    private static Map<String, CacheEntry> cache = new ConcurrentHashMap<>();

    public static void put(String key, Object value, long timeout, TimeUnit timeUnit) {
        long expiresAt = System.currentTimeMillis() + timeUnit.toMillis(timeout);
        cache.put(key, new CacheEntry(value, expiresAt));
    }

    public static Object get(String key) {
        CacheEntry cacheEntry = cache.get(key);
        if (cacheEntry == null) {
            return null;
        }
        if (cacheEntry.expiresAt < System.currentTimeMillis()) {
            cache.remove(key);
            return null;
        }
        return cacheEntry.value;
    }

    private static class CacheEntry {
        public final Object value;
        public final long expiresAt;

        public CacheEntry(Object value, long expiresAt) {
            this.value = value;
            this.expiresAt = expiresAt;
        }
    }
}
