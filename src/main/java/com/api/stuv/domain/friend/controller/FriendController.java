package com.api.stuv.domain.friend.controller;

import com.api.stuv.domain.friend.dto.*;
import com.api.stuv.domain.friend.service.FriendService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/friend")
@RequiredArgsConstructor
@Tag(name = "Friend", description = "친구 관련 API")
public class FriendController {
    private final FriendService friendService;

    @Operation(summary = "친구 요청 API", description = "특정 유저에게 친구 요청을 합니다.")
    @PostMapping("/request/{friendId}") // Security 적용 되서 userId는 AuthenticationPrincipal로 받아올 수 있을 때까지 임시로 적용
    public ResponseEntity<FriendFollowResponse> requestFriend(@RequestBody Long userId, @PathVariable Long friendId) {
        return ResponseEntity.ok()
                .body(friendService.requestFriend(userId, friendId));
    }
}
