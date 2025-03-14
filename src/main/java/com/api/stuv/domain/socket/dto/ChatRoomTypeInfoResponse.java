package com.api.stuv.domain.socket.dto;

public record ChatRoomTypeInfoResponse(
        UserInfoWithContext userInfo,
        GroupInfo groupInfo
) {
    public static ChatRoomTypeInfoResponse privateChat(UserInfoWithContext userInfo) {
        return new ChatRoomTypeInfoResponse(
                userInfo,
                null
        );
    }

    public static ChatRoomTypeInfoResponse groupChat(GroupInfo groupInfo) {
        return new ChatRoomTypeInfoResponse(
                null,
                groupInfo
        );
    }
}
