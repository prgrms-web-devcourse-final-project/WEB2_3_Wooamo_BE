package com.api.stuv.domain.socket.controller;

import com.api.stuv.domain.socket.dto.ChatMessageResponseDto;
import com.api.stuv.domain.socket.service.ChatMessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class ChatController {
    private final ChatMessageService chatMessageService;

    @GetMapping("/rooms/{senderId}")
    public List<String> getRoomsBySenderId(@PathVariable Long senderId) {
        return chatMessageService.getRoomIdsBySenderId(senderId);
    }

    @GetMapping("/rooms/{roomId}/messages")
    public ResponseEntity<List<ChatMessageResponseDto>> getMessagesByRoomIdPage(
            @PathVariable String roomId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(chatMessageService.getMessagesByRoomIdPagination(roomId, page,size));
    }
}
