package com.api.stuv.domain.alert.dto;

import com.api.stuv.domain.alert.entity.Alert;
import com.api.stuv.global.util.common.TemplateUtils;

public record AlertResponse(
        String alertId,
        Long typeId,
        String type,
        String nickname,
        Boolean isRead,
        String createdAt
) {
    public static AlertResponse from(Alert alert) {
        return new AlertResponse(
                alert.getAlertId(),
                alert.getTypeId(),
                alert.getType().name(),
                alert.getNickname(),
                alert.getIsRead(),
                alert.getCreatedAt().format(TemplateUtils.dateTimeFormatter)
        );
    }
}
