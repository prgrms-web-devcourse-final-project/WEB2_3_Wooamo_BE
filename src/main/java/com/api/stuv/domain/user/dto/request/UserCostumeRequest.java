package com.api.stuv.domain.user.dto.request;

import com.api.stuv.domain.user.entity.UserCostume;

public record UserCostumeRequest(
        Long userId,
        Long costumeId
) {
    public static UserCostume createUserCostumeRequeset(Long userId, Long costumeId) {
        return UserCostume.builder()
                .costumeId(costumeId)
                .userId(userId)
                .build();
    }
}
