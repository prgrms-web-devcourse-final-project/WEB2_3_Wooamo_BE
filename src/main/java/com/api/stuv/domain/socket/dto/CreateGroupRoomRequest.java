package com.api.stuv.domain.socket.dto;

public record CreateGroupRoomRequest(
        String groupName,
        Long userId,
        int maxMembers
) {}
