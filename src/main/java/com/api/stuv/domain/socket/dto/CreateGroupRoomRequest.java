package com.api.stuv.domain.socket.dto;

public record CreateGroupRoomRequest(
        Long groupId,
        String groupName,
        Long userId,
        int maxMembers
) {}
