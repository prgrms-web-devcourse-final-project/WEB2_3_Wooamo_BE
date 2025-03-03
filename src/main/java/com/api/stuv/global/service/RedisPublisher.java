package com.api.stuv.global.service;

import com.api.stuv.domain.alert.entity.Alert;
import com.api.stuv.global.exception.SseErrorException;
import com.api.stuv.global.util.common.TemplateUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class RedisPublisher {
    private final RedisTemplate<String, Object> redisTemplate;
    private final ChannelTopic topic;

    public void publish(Long userId, Alert message) {
        try {
            Map<String, Object> messageData = new HashMap<>();
            messageData.put("userId", userId);
            messageData.put("message", message);
            String messageJson = TemplateUtils.jsonParseToString(messageData);
            redisTemplate.convertAndSend(topic.getTopic(), messageJson);
        } catch (Exception e) {
            log.error("Exception occurred while publishing Message : {}", e.getMessage());
            throw new SseErrorException(e.getMessage());
        }
    }

}
