package com.api.stuv.domain.friend.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record FriendResponse(
        Long friendId,
        Long userId,
        Long senderId,
        String nickname,
        String context,
        String profile,
        String status
) {}
