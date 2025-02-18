package com.api.stuv.domain.alert.entity;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.TimeToLive;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.concurrent.TimeUnit;

@Getter
@ToString
@RedisHash("alerts") // Redis에서 Hash 형태로 저장
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Alert implements Serializable {

    @Id
    private Long id;

    private AlertType type; // 알림 유형 (예: BOARD, FRIEND 등)

    private Long typeId; // 연관된 데이터 ID (게시판 ID, 친구 요청 ID 등)

    private Boolean isRead; // 읽음 여부

    private LocalDateTime createdAt; // 생성 시간

    @TimeToLive
    private Long expiration; // TTL (초 단위)

    @Builder
    public Alert(Long id, AlertType type, Long typeId, Boolean isRead, LocalDateTime createdAt) {
        this.id = id;
        this.type = type;
        this.typeId = typeId;
        this.isRead = isRead;
        this.createdAt = createdAt;
        this.expiration = isRead ? TimeUnit.DAYS.toHours(24) : null; // 읽었으면 24시간 후 삭제, 안 읽었으면 유지
    }

    // 읽음 처리 후 TTL 설정 메서드
    public void markAsRead() {
        this.isRead = true;
        this.expiration = TimeUnit.DAYS.toSeconds(1); // 24시간 후 삭제
    }
}
