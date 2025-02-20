package com.api.stuv.global.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Repository;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

@Repository
@RequiredArgsConstructor
public class RedisService {
    private final RedisTemplate<String, Object> template;
    private final ObjectMapper objectMapper;

    public void save(String key, Object value, Duration timeout) {
        template.opsForValue().set(key, value, timeout);
    }
    public void saveToken(String email, String refreshToken, Duration timeout) {
        template.opsForValue().set(refreshToken, email, timeout);
    }

    public <T> T find(String key, Class<T> clazz) {
        Object rawData = template.opsForValue().get(key);
        if (rawData == null) {
            return null;
        }

        return objectMapper.convertValue(rawData, clazz);
    }

    public void delete(String key) {
        template.delete(key);
    }
}
