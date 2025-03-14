package com.api.stuv.domain.socket.dto;

public record GroupInfo(
        String groupId,
        String groupName,
        int totalMembers
) {
}
