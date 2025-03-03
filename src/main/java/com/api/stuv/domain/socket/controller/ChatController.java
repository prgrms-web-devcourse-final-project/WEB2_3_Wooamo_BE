package com.api.stuv.domain.socket.controller;

import com.api.stuv.domain.socket.dto.ChatMessageResponse;
import com.api.stuv.domain.socket.dto.ChatRoomResponse;
import com.api.stuv.domain.socket.service.ChatMessageService;
import com.api.stuv.domain.socket.service.ChatRoomDetailService;
import com.api.stuv.global.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/rooms")
@RequiredArgsConstructor
@Tag(name = "ChatRoom", description = "채팅방 관련 API")
public class ChatController {
    private final ChatMessageService chatMessageService;
    private final ChatRoomDetailService chatRoomDetailService;

    @Operation(summary = "채팅방 목록 리스트 API", description = "user가 포함된 채팅방 목록을 가져옵니다.")
    @GetMapping("/list/{senderId}")
    public ResponseEntity<ApiResponse<List<ChatRoomResponse>>> getRoomListBySenderId(
            @PathVariable Long senderId) {
        List<ChatRoomResponse> roomList = chatRoomDetailService.getSortedRoomListBySenderId(senderId);

        return ResponseEntity.ok(ApiResponse.success(roomList));
    }

    @Operation(summary = "메세지 요청 API", description = "채팅방 메세지를 요청을 합니다.")
    @GetMapping("/{roomId}/messages")
    public ResponseEntity<ApiResponse<List<ChatMessageResponse>>> getMessagesByRoomIdPage(
            @PathVariable String roomId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        return ResponseEntity.ok()
                .body(ApiResponse.success(chatMessageService.getMessagesByRoomIdPagination(
                roomId, PageRequest.of(page, size))));
    }

    @Operation(summary = "1:1 채팅방 생성", description = "두 사용자 간의 1:1 채팅방을 생성합니다.")
    @PostMapping("/private")
    public ResponseEntity<ApiResponse<String>> createPrivateRoom(
            @RequestParam Long userId1,
            @RequestParam Long userId2) {
        String roomId = chatRoomDetailService.createPrivateChatRoom(userId1, userId2);
        return ResponseEntity.ok(ApiResponse.success(roomId));
    }

    @Operation(summary = "그룹 채팅방 생성", description = "여러 사용자가 참여하는 그룹 채팅방을 생성합니다.")
    @PostMapping("/group")
    public ResponseEntity<ApiResponse<String>> createGroupRoom(
            @RequestParam String groupName,
            @RequestParam Long userId,
            @RequestParam(defaultValue = "100") int maxMembers) {
        String roomId = chatRoomDetailService.createGroupChatRoom(groupName, userId, maxMembers);
        return ResponseEntity.ok(ApiResponse.success(roomId));
    }

    @Operation(summary = "그룹 채팅방에 사용자 추가", description = "기존 그룹 채팅방에 새로운 사용자를 추가합니다.")
    @PostMapping("/group/{roomId}/addUser")
    public ResponseEntity<ApiResponse<Void>> addUserToGroupChat(
            @PathVariable String roomId,
            @RequestParam Long newUserId) {
        chatRoomDetailService.addUserToGroupChat(roomId, newUserId);
        return ResponseEntity.ok(ApiResponse.success());
    }

    @Operation(summary = "채팅방 삭제", description = "roomId를 받아 해당 채팅방을 삭제합니다.")
    @DeleteMapping("/group/{roomId}")
    public ResponseEntity<ApiResponse<Void>> deleteChatRoom(@PathVariable String roomId) {
        chatRoomDetailService.deleteChatRoom(roomId);
        return ResponseEntity.ok(ApiResponse.success());
    }
}
