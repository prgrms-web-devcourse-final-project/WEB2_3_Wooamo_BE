package com.api.stuv.domain.alert.entity;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@ToString
@RequiredArgsConstructor
public class Alert {
    private final String alertId;
    private final Long typeId;
    private final AlertType type;
    private final String nickname;
    private Boolean isRead;
    private LocalDateTime createdAt;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private LocalDateTime expiredAt;

    public Alert(String alertId, Long typeId, AlertType type, String nickname, Boolean isRead, LocalDateTime createdAt) {
        this.alertId = alertId;
        this.typeId = typeId;
        this.type = type;
        this.nickname = nickname;
        this.isRead = isRead;
        this.createdAt = createdAt;
    }

    @JsonCreator
    public Alert(
            @JsonProperty("alertId") String alertId,
            @JsonProperty("typeId") Long typeId,
            @JsonProperty("type") AlertType type,
            @JsonProperty("nickname") String nickname,
            @JsonProperty("isRead") Boolean isRead,
            @JsonProperty("createdAt") LocalDateTime createdAt,
            @JsonProperty("expiredAt") LocalDateTime expiredAt
    ) {
        this.alertId = alertId;
        this.typeId = typeId;
        this.type = type;
        this.nickname = nickname;
        this.isRead = isRead;
        this.createdAt = createdAt;
        this.expiredAt = expiredAt;
    }

    public void updateIsRead() {
        this.isRead = true;
        this.expiredAt = LocalDateTime.now().plusDays(1L);
    }
}
