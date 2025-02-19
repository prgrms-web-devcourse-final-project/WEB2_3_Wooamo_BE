package com.api.stuv.domain.friend.controller;

import com.api.stuv.domain.friend.dto.FriendResponse.*;
import com.api.stuv.domain.friend.service.FriendService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/api/friend")
@RequiredArgsConstructor
@Tag(name = "Friend", description = "친구 관련 API")
public class FriendController {
    private final FriendService friendService;

    @Operation(summary = "친구 요청 API", description = "특정 유저에게 친구 요청을 합니다.")
    @PostMapping("/request/{friendId}/{userId}") // Security 적용 되서 userId는 AuthenticationPrincipal로 받아올 수 있을 때까지 임시로 적용
    public ResponseEntity<RequestFriend> requestFriend(@PathVariable Long userId, @PathVariable Long friendId) {
        return ResponseEntity.ok()
                .body(friendService.requestFriend(userId, friendId));
    }
}
