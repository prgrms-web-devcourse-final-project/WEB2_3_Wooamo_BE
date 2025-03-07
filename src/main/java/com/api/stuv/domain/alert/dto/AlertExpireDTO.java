package com.api.stuv.domain.alert.dto;

import java.time.LocalDateTime;

public record AlertExpireDTO(
        Long userId,
        LocalDateTime expiredAt
) {}
