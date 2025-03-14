package com.api.stuv.domain.user.dto;

import com.api.stuv.domain.user.entity.RoleType;

import java.math.BigDecimal;

public record MyInformationDTO(
        Long userId,
        String context,
        String link,
        String nickname,
        BigDecimal point,
        RoleType role,
        String newFilename,
        Long costumeId,
        Long friends
) { }