package com.api.stuv.domain.socket.controller;

import com.api.stuv.domain.socket.dto.ChatMessageResponse;
import com.api.stuv.domain.socket.service.ChatMessageService;
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

    @Operation(summary = "멤버 요청 API", description = "같은 방 멤버를 요청합니다.")
    @GetMapping("/{senderId}")
    public ResponseEntity<ApiResponse<List<String>>> getRoomsBySenderId(@PathVariable Long senderId) {
        return ResponseEntity.ok()
                .body(ApiResponse.success((chatMessageService.getRoomIdsBySenderId(senderId))));
    }

    @Operation(summary = "메세지 요청 API", description = "채팅방 메세지를 요청을 합니다.")
    @GetMapping("/{roomId}/messages")
    public ResponseEntity<ApiResponse<List<ChatMessageResponse>>> getMessagesByRoomIdPage(
            @PathVariable String roomId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        return ResponseEntity.ok()
                .body(ApiResponse.success(chatMessageService.getMessagesByRoomIdPagination(
                roomId,
                PageRequest.of(page, size, Sort.by(Sort.Direction.ASC, "createdAt")))));
    }
}
