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

    @Operation(summary = "채팅방 목록 API", description = "user가 포함된 채팅방 목록을 가져옵니다.")
    @GetMapping("/{senderId}")
    public ResponseEntity<ApiResponse<List<String>>> getRoomsBySenderId(
            @PathVariable Long senderId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
//        List<ChatRoomResponse> roomList = chatRoomDetailService.getSortedRoomListBySenderId(senderId, PageRequest.of(page, size));

        return ResponseEntity.ok()
                .body(ApiResponse.success((chatRoomDetailService.getRoomIdsBySenderId(
                        senderId, PageRequest.of(page, size)))));
//        return ResponseEntity.ok(ApiResponse.success(roomList));
    }

    @GetMapping("/test/{senderId}")
    public ResponseEntity<ApiResponse<List<ChatRoomResponse>>> getRoomsBySenderId_test(
            @PathVariable Long senderId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        List<ChatRoomResponse> roomList = chatRoomDetailService.getSortedRoomListBySenderId(senderId, PageRequest.of(page, size));

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


}
