package com.api.stuv.domain.friend.controller;

import com.api.stuv.domain.friend.dto.*;
import com.api.stuv.domain.friend.service.FriendService;
import com.api.stuv.global.response.ApiResponse;
import com.api.stuv.global.response.PageResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
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
    public ResponseEntity<ApiResponse<FriendFollowResponse>> requestFriend(@RequestBody Long userId, @PathVariable Long friendId) {
        return ResponseEntity.ok()
                .body(ApiResponse.success(friendService.requestFriend(userId, friendId)));
    }

    @Operation(summary = "친구 수락 API", description = "특정 친구 요청을 수락 합니다.")
    @PatchMapping("/{friendId}") // Security 적용 되서 userId는 AuthenticationPrincipal로 받아올 수 있을 때까지 임시로 적용
    public ResponseEntity<ApiResponse<FriendFollowResponse>> acceptFriend(@RequestBody Long userId, @PathVariable Long friendId) {
        return ResponseEntity.ok()
                .body(ApiResponse.success(friendService.acceptFriend(userId, friendId)));
    }

    @Operation(summary = "친구 요청 목록 조회 API", description = "특정 유저의 친구 요청 목록을 조회합니다.")
    @GetMapping("/request/{userId}")
    public ResponseEntity<ApiResponse<PageResponse<FriendRequestListResponse>>> getFriendRequestList(
            @PathVariable Long userId,
            @RequestParam(required = false, defaultValue = "0") Integer page,
            @RequestParam(required = false, defaultValue = "10") Integer size
    ) {
        return ResponseEntity.ok()
                .body(ApiResponse.success(friendService.getFriendRequestList(userId, PageRequest.of(page, size))));
    }
}
