package com.api.stuv.domain.friend.controller;

import com.api.stuv.domain.auth.util.TokenUtil;
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
    private final TokenUtil tokenUtil;

    // TODO: 알람 기능 추가시 알람 생성 로직 추가
    @Operation(summary = "친구 요청 API", description = "특정 유저에게 친구 요청을 합니다.")
    @PostMapping("/request/{friendId}")
    public ResponseEntity<ApiResponse<FriendFollowResponse>> requestFriend(@PathVariable Long friendId) {
        return ResponseEntity.ok()
                .body(ApiResponse.success(friendService.requestFriend(tokenUtil.getUserId(), friendId)));
    }

    @Operation(summary = "친구 수락 API", description = "특정 친구 요청을 수락 합니다.")
    @PatchMapping("/{friendId}")
    public ResponseEntity<ApiResponse<FriendFollowResponse>> acceptFriend(@PathVariable Long friendId) {
        return ResponseEntity.ok()
                .body(ApiResponse.success(friendService.acceptFriend(tokenUtil.getUserId(), friendId)));
    }

    @Operation(summary = "친구 요청 목록 조회 API", description = "특정 유저의 친구 요청 목록을 조회합니다.")
    @GetMapping("/request/{userId}")
    public ResponseEntity<ApiResponse<PageResponse<FriendFollowListResponse>>> getFriendFollowList(
            @RequestParam(required = false, defaultValue = "0") int page,
            @RequestParam(required = false, defaultValue = "10") int size
    ) {
        return ResponseEntity.ok()
                .body(ApiResponse.success(friendService.getFriendFollowList(tokenUtil.getUserId(), PageRequest.of(page, size))));
    }

    @Operation(summary = "친구 목록 조회 API", description = "특정 유저의 친구 목록을 조회합니다.")
    @GetMapping("/{userId}")
    public ResponseEntity<ApiResponse<PageResponse<FriendResponse>>> getFriendList(
            @RequestParam(required = false, defaultValue = "0") int page,
            @RequestParam(required = false, defaultValue = "10") int size
    ) {
        return ResponseEntity.ok()
                .body(ApiResponse.success(friendService.getFriendList(tokenUtil.getUserId(), PageRequest.of(page, size))));
    }

    @Operation(summary = "친구 삭제 API", description = "특정 친구를 삭제합니다.")
    @DeleteMapping("/{friendId}")
    public ResponseEntity<ApiResponse<Void>> deleteFriend(@PathVariable Long friendId) {
        friendService.deleteFriend(tokenUtil.getUserId(), friendId);
        return ResponseEntity.ok().body(ApiResponse.success());
    }
}
