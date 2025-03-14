package com.api.stuv.domain.socket.controller;

import com.api.stuv.domain.auth.util.TokenUtil;
import com.api.stuv.domain.socket.dto.*;
import com.api.stuv.domain.socket.service.ChatMessageService;
import com.api.stuv.domain.socket.service.ChatRoomDetailService;
import com.api.stuv.domain.socket.service.ChatRoomMemberService;
import com.api.stuv.global.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
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
    private final ChatRoomMemberService chatRoomMemberService;
    private final TokenUtil tokenUtil;

    @Operation(summary = "채팅방 메시지 조회", description = "채팅방의 메시지를 페이지네이션 방식으로 조회합니다.")
    @GetMapping("/{roomId}/messages")
    public ResponseEntity<ApiResponse<List<ChatMessageResponse>>> getMessagesByRoomIdPage(
            @PathVariable String roomId,
            @RequestParam(required = false) String lastChatId,
            @RequestParam(defaultValue = "10") int limit) {

        List<ChatMessageResponse> messages = chatMessageService.getMessagesByRoomId(roomId, lastChatId, limit);
        return ResponseEntity.ok(ApiResponse.success(messages));
    }

    @Operation(summary = "채팅방 메시지 범위 조회", description = "마지막 메시지부터 특정 메시지(lastChatId)까지 조회합니다.")
    @GetMapping("/{roomId}/messages/until")
    public ResponseEntity<ApiResponse<List<ChatMessageResponse>>> getMessagesUntilLastChatId(
            @PathVariable String roomId,
            @RequestParam(required = false) String lastChatId) {

        List<ChatMessageResponse> messages = chatMessageService.getMessagesUntilLastChatId(roomId, lastChatId);
        return ResponseEntity.ok(ApiResponse.success(messages));
    }

    @Operation(summary = "채팅방 목록 조회 API", description = "user가 포함된 채팅방 목록을 가져옵니다.")
    @GetMapping("/list")
    public ResponseEntity<ApiResponse<List<ChatRoomResponse>>> getRoomListBySenderId() {
        List<ChatRoomResponse> roomList = chatRoomDetailService.getSortedRoomListBySenderId(tokenUtil.getUserId());
        return ResponseEntity.ok(ApiResponse.success(roomList));
    }

    @Operation(summary = "1:1 채팅방 생성", description = "두 사용자 간의 1:1 채팅방을 생성합니다.")
    @PostMapping("/private")
    public ResponseEntity<ApiResponse<String>> createPrivateRoom(
            @RequestBody CreatePrivateRoomRequest request) {
        String roomId = chatRoomDetailService.createPrivateChatRoom(request.userId1(), request.userId2());
        chatRoomMemberService.updateRoomMembers(String.valueOf(roomId));
        return ResponseEntity.ok(ApiResponse.success(roomId));
    }

    @Operation(summary = "그룹 채팅방 생성", description = "여러 사용자가 참여하는 그룹 채팅방을 생성합니다.")
    @PostMapping("/group")
    public ResponseEntity<ApiResponse<String>> createGroupRoom(
            @RequestBody CreateGroupRoomRequest request) {
        String roomId = chatRoomDetailService.createGroupChatRoom(
                String.valueOf(request.groupId()),request.groupName(), request.userId(), request.maxMembers());
        chatRoomMemberService.updateRoomMembers(String.valueOf(roomId));
        return ResponseEntity.ok(ApiResponse.success(roomId));
    }

    @Operation(summary = "그룹 채팅방에 사용자 추가", description = "기존 그룹 채팅방에 새로운 사용자를 추가합니다.")
    @PostMapping("/group/{roomId}/addUser")
    public ResponseEntity<ApiResponse<Void>> addUserToGroupChat(
            @PathVariable Long roomId,
            @RequestBody AddUserToGroupChatRequest request) {
        chatRoomDetailService.addUserToGroupChat(String.valueOf(roomId), request.newUserId());
        chatRoomMemberService.updateRoomMembers(String.valueOf(roomId));
        return ResponseEntity.ok(ApiResponse.success());
    }

    @Operation(summary = "채팅방 삭제", description = "roomId를 받아 해당 채팅방을 삭제합니다.")
    @DeleteMapping("/group/{roomId}")
    public ResponseEntity<ApiResponse<Void>> deleteChatRoom(@PathVariable Long roomId) {
        chatRoomDetailService.deleteChatRoom(String.valueOf(roomId));
        return ResponseEntity.ok(ApiResponse.success());
    }
}
