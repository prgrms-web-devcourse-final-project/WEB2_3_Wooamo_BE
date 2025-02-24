package com.api.stuv.domain.friend.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record FriendResponse(
        Long friendId,
        Long userId,
        String nickname,
        String context,
        String profile
) {}
