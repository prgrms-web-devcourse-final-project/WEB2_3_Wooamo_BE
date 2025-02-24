package com.api.stuv.domain.user.dto.response;

import com.api.stuv.domain.friend.entity.FriendStatus;

public record UserInformationResponse(
        Long id,
        String context,
        String link,
        String nickname,
        String status
) {
}
