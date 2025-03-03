package com.api.stuv.global.service;

import com.api.stuv.domain.alert.service.SseService;
import com.api.stuv.global.util.common.TemplateUtils;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class RedisSubscriber implements MessageListener {

    private final SseService sseService;
    private final ObjectMapper objectMapper;

    @Override
    public void onMessage(Message message, byte[] pattern) {
        try {
            JsonNode jsonNode = objectMapper.readTree(new String(message.getBody()));
            if (jsonNode.isTextual()) jsonNode = objectMapper.readTree(jsonNode.asText());
            Long userId = jsonNode.get("userId").asLong();
            String messageData = TemplateUtils.jsonParseToString(jsonNode.get("message"));
            sseService.sendOrStoreAlert(userId, messageData);
        } catch (Exception e) {
            log.error("Exception occurred while sending message : {}", e.getMessage());
        }
    }
}
