package com.api.stuv.domain.alert.service;

import com.api.stuv.domain.alert.dto.AlertResponse;
import com.api.stuv.domain.alert.entity.Alert;
import com.api.stuv.domain.alert.entity.AlertType;
import com.api.stuv.global.exception.ErrorCode;
import com.api.stuv.global.exception.NotFoundException;
import com.api.stuv.global.service.RedisPublisher;
import com.api.stuv.global.service.RedisService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AlertService {
    private final RedisService redisService;
    private final RedisPublisher redisPublisher;

    // 알림 생성
    public void createAlert(Long targetUserId, Long typeId, AlertType alertType, String title, String nickname) {
        String alertId = UUID.randomUUID().toString();
        Alert alert = new Alert(alertId, typeId, alertType, title, nickname, false, LocalDateTime.now());
        System.out.println("alertResponse = " + alert);
        redisService.saveByAlertId(getKey(targetUserId), alertId, alert);
        redisPublisher.publish(targetUserId, alert);
    }

    // 알림 읽음 처리
    public void readAlert(Long userId, String alertId) {
        Alert alert = redisService.findByAlertId(getKey(userId), alertId);
        if (alert == null) throw new NotFoundException(ErrorCode.ALERT_NOT_FOUND);
        if (alert.getIsRead()) throw new NotFoundException(ErrorCode.ALERT_ALREADY_READ);
        alert.updateIsRead();
        redisService.saveByAlertId(getKey(userId), alertId, alert);
    }

    // 알림 전체 읽음 처리
    public void readAllAlert(Long userId) {
        List<Alert> alerts = redisService.findAll(getKey(userId)).stream().filter(alert -> !alert.getIsRead()).toList();
        if (alerts.isEmpty()) throw new NotFoundException(ErrorCode.ALL_ALERT_ALREADY_READ);
        for (Alert alert : alerts) {
            alert.updateIsRead();
            redisService.saveByAlertId(getKey(userId), alert.getAlertId(), alert);
        }
    }

    // 유저 알림 목록 보기
    public List<AlertResponse> getAlertList(Long userId) {
        return redisService.findAll(getKey(userId)).stream().map(AlertResponse::from).toList();
    }

    private String getKey(Long userId) {
        return "alert:" + userId.toString();
    }
}
