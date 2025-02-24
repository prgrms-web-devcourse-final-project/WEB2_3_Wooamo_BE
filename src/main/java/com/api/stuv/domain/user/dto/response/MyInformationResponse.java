package com.api.stuv.domain.user.dto.response;

import java.math.BigDecimal;

public record MyInformationResponse(
        Long userId,
        String context,
        String link,
        String nickname,
        BigDecimal point
        //TODO: 후에 프로필 이미지 추가해주세요!
) {
}
