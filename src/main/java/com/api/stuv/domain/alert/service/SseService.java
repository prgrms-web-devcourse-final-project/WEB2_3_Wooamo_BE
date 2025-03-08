package com.api.stuv.domain.alert.service;

import com.api.stuv.domain.alert.dto.AlertResponse;
import com.api.stuv.domain.alert.entity.Alert;
import com.api.stuv.global.exception.ErrorCode;
import com.api.stuv.global.exception.SseErrorException;
import com.api.stuv.global.util.common.TemplateUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@RequiredArgsConstructor
public class SseService {
    private static final Long DEFAULT_ALERT_TIME_OUT = 1000L * 60 * 10; // 10분마다 재 연결
    private final Map<Long, SseEmitter> emitters = new ConcurrentHashMap<>();
    private final StringRedisTemplate redisTemplate;
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

    public SseEmitter connect(Long userId) {
        if (emitters.containsKey(userId)) {
            log.warn("[SSE] {} user already connected | current user count : {}", userId, emitters.size());
            return emitters.get(userId);
        }
        SseEmitter emitter = new SseEmitter(DEFAULT_ALERT_TIME_OUT);
        sseConfig(userId, emitter);

        try {
            emitter.send(SseEmitter.event().data("connected"));
            log.info("[SSE] {} user enter | current user count : {}", userId, emitters.size());
        } catch (IOException e) {
            log.error("[SSE] Error sending initial event to client: {} | Error : {}", userId, e.getMessage());
        }

        sendStoredAlert(userId, emitter);

        emitters.put(userId, emitter);
        log.info("[SSE] {} 유저의 객체가 return 됐습니다 : {}", userId, emitter);
        return emitter;
    }

    public void sendOrStoreAlert(Long userId, String message) {
        SseEmitter emitter = emitters.get(userId);
        if (emitter != null) sendAlert(userId, emitter, message);
        else storeAlert(userId, message);
    }

    public void sendAlert(Long userId, SseEmitter emitter, String message) {
        try {
            AlertResponse alertResponse = AlertResponse.from(TemplateUtils.jsonParseToObject(message, Alert.class));
            emitter.send(SseEmitter.event().data(alertResponse));
        } catch (IOException e) {
            log.error("[SSE] Error sending alert to client: {} | Error: {}", userId, e.getMessage());
            storeAlert(userId, message);
            emitter.completeWithError(e);
            throw new SseErrorException(ErrorCode.SSE_CONNECTION_CLOSED);
        } catch (Exception e) {
            log.error("[SSE] Error sending alert to client: {} | Error: {}", userId, e.getMessage());
            storeAlert(userId, message);
            emitter.completeWithError(e);
            throw new SseErrorException(e.getMessage());
        }
    }

    private void storeAlert(Long userId, String message) {
        String key = "offLineAlert:" + userId;
        redisTemplate.opsForList().leftPush(key, message);
    }

    private void sendStoredAlert(Long userId, SseEmitter emitter) {
        String key = "offLineAlert:" + userId;
        while (true) {
            String message = redisTemplate.opsForList().rightPop(key);
            if (message == null) break;
            sendAlert(userId, emitter, message);
        }
    }

    public void disconnect(Long userId) {
        SseEmitter emitter = emitters.get(userId);
        if (emitter != null) {
            try {
                emitter.send(SseEmitter.event().data("disconnected"));
                emitter.complete();
            } catch (IOException e) {
                log.error("[SSE] Error sending disconnect event to client: {}", userId, e);
            }
        }
    }

    @Scheduled(fixedRate = 1000 * 60)
    private void logAlertConnection() {
        StringBuilder keys = new StringBuilder();
        for (Long key : emitters.keySet()) {
            keys.append(key).append(", ");
        }
        log.info("[SSE] Emitter connected for client: {} | current emitters: {}", keys, emitters.size());
    }

    private void sseConfig(Long userId, SseEmitter emitter) {
        // 정상 종료시 emitters에서 제거
        emitter.onCompletion(() -> {
            emitters.remove(userId);
            log.info("[SSE] Emitter completed for client: {} | current emitters: {}", userId, emitters.size());
        });

        // 타임 아웃시 emitters에서 제거
        emitter.onTimeout(() -> {
            emitters.remove(userId);
            log.warn("[SSE] Emitter timed out for client: {} | current emitters: {}", userId, emitters.size());
        });

        // 에러 발생시 emitters에서 제거
        emitter.onError((e) -> {
            emitters.remove(userId);
            log.error("[SSE] Emitter error for client: {} | current emitters: {}", userId, emitters.size());
            throw new SseErrorException(e.getMessage());
        });

        scheduler.scheduleAtFixedRate(() -> {
            try {
                emitter.send(SseEmitter.event().data("ping"));
            } catch (IOException e) {
                log.error("[SSE] Error sending ping to client: {} | Error: {}", userId, e.getMessage());
                emitter.completeWithError(e);
                throw new SseErrorException(ErrorCode.SSE_CONNECTION_CLOSED);
            }
        }, 0, 30, TimeUnit.SECONDS);
    }
}