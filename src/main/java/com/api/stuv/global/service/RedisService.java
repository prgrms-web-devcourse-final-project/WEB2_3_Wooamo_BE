package com.api.stuv.global.service;

import com.api.stuv.domain.alert.dto.AlertExpireDTO;
import com.api.stuv.domain.alert.entity.Alert;

import com.api.stuv.global.util.common.TemplateUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.Cursor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ScanOptions;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Repository;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;


@Slf4j
@Repository
@RequiredArgsConstructor
public class RedisService {
    private final RedisTemplate<String, Object> template;
    private final StringRedisTemplate stringTemplate;
    private final ObjectMapper objectMapper;

    public void save(String key, Object value, Duration timeout) {
        if (value instanceof String str) {
            stringTemplate.opsForValue().set(key, str, timeout);
        } else {
            template.opsForValue().set(key, value, timeout);
        }
    }

    public <T> T find(String key, Class<T> clazz) {
        if (clazz == String.class) {
            String value = stringTemplate.opsForValue().get(key);
            return clazz.cast(value);
        }

        Object rawData = template.opsForValue().get(key);
        if (rawData == null) {
            return null;
        }

        return objectMapper.convertValue(rawData, clazz);
    }

    public void delete(String key) {
        template.delete(key);
    }

    public Boolean exists(String key) {
        return Boolean.TRUE.equals(template.hasKey(key));
    }

    public <T> void saveByAlertId(String key, String alertId, T value) {
        String rawData = TemplateUtils.jsonParseToString(value);
        template.opsForHash().put(key, alertId, rawData);
    }

    public List<Alert> findAll(String key) {
        Map<Object, Object> rawData = template.opsForHash().entries(key);
        return Arrays.asList(TemplateUtils.jsonParseToObject(rawData.values().toString(), Alert[].class));
    }

    public Alert findByAlertId(String key, String alertId) {
        String rawData = (String) template.opsForHash().get(key, alertId);
        if (rawData == null) return null;
        return TemplateUtils.jsonParseToObject(rawData, Alert.class);
    }

    @Scheduled(fixedRate = 10 * 60 * 1000)
    public void deleteAlertSchedule() {
        log.info("== Redis 의 expire alert 삭제 시작 ==");
        AtomicInteger count = new AtomicInteger();
        // Cursor 사용하여 alert 으로 시작하는 key 를 100개씩 조회 후 expiredAt 이 현재 시간보다 이전인 데이터 삭제
        try (Cursor<Map.Entry<Object, Object>> cursor = template.opsForHash().scan("alert:expire", ScanOptions.scanOptions().count(100).build())) {
            cursor.forEachRemaining(hash -> {
                String key = hash.getKey().toString();
                AlertExpireDTO dto = TemplateUtils.jsonParseToObject(hash.getValue().toString(), AlertExpireDTO.class);
                if (dto.expiredAt().isBefore(LocalDateTime.now().plusDays(1))) {
                    template.opsForHash().delete("alert:expire", key);
                    template.opsForHash().delete("alert:" + dto.userId(), key);
                    count.getAndIncrement();
                }
            });
        }
        log.info("== Redis 의 expire alert 총 {} 개 삭제 완료 ==", count.get());
    }
}
