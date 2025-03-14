package com.api.stuv.domain.socket.dto;

import com.api.stuv.domain.socket.entity.ChatRoom;

public record ChatRoomInfoResponse(
        String roomId,
        String roomType,
        UserInfo userInfo,
        GroupInfo groupInfo
) {
    public static ChatRoomInfoResponse privateChat(ChatRoom room, UserInfo userInfo) {
        return new ChatRoomInfoResponse(
                room.getRoomId(),
                room.getRoomType(),
                userInfo,
                null
        );
    }

    public static ChatRoomInfoResponse groupChat(ChatRoom room, GroupInfo groupInfo) {
        return new ChatRoomInfoResponse(
                room.getRoomId(),
                room.getRoomType(),
                null,
                groupInfo
        );
    }
}
