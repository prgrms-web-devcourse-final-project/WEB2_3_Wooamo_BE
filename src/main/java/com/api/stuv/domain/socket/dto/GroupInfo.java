package com.api.stuv.domain.socket.dto;

public record GroupInfo(
        Long groupId,
        String groupName,
        int totalMembers
) {
}
