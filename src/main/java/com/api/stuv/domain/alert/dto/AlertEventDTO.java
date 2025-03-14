package com.api.stuv.domain.alert.dto;

import com.api.stuv.domain.alert.entity.AlertType;

public record AlertEventDTO (
        Long targetUserId,
        Long typeId,
        AlertType alertType,
        String title,
        String nickname
) {}
