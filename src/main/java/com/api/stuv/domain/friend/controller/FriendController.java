package com.api.stuv.domain.friend.controller;

import com.api.stuv.domain.auth.util.TokenUtil;
import com.api.stuv.domain.friend.dto.response.FriendFollowResponse;
import com.api.stuv.domain.friend.dto.response.FriendResponse;
import com.api.stuv.domain.friend.service.FriendService;
import com.api.stuv.global.response.ApiResponse;
import com.api.stuv.global.response.PageResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/api/friend")
@RequiredArgsConstructor
@Tag(name = "Friend", description = "친구 관련 API")
public class FriendController {
    private final FriendService friendService;
    private final TokenUtil tokenUtil;

    // TODO: tempUserId -> tokenUtil.getUserId() 바꾸기
    @Operation(summary = "친구 요청 API", description = "특정 유저에게 친구 요청을 합니다.")
    @PostMapping("/request/{receiverId}")
    public ResponseEntity<ApiResponse<FriendFollowResponse>> requestFriend(@PathVariable Long receiverId, @RequestParam Long tempUserId) {
        return ResponseEntity.ok()
                .body(ApiResponse.success(friendService.requestFriend(tempUserId, receiverId)));
    }

    // TODO: tempUserId -> tokenUtil.getUserId() 바꾸기
    @Operation(summary = "친구 수락 API", description = "특정 친구 요청을 수락 합니다.")
    @PatchMapping("/{friendId}")
    public ResponseEntity<ApiResponse<FriendFollowResponse>> acceptFriend(@PathVariable Long friendId, @RequestParam Long tempUserId) {
        return ResponseEntity.ok()
                .body(ApiResponse.success(friendService.acceptFriend(tempUserId, friendId)));
    }

    // TODO: tempUserId -> tokenUtil.getUserId() 바꾸기
    @Operation(summary = "친구 요청 목록 조회 API", description = "특정 유저의 친구 요청 목록을 조회합니다.")
    @GetMapping("/request")
    public ResponseEntity<ApiResponse<PageResponse<FriendResponse>>> getFriendFollowList(
            @RequestParam(required = false, defaultValue = "0") int page,
            @RequestParam(required = false, defaultValue = "10") int size, @RequestParam Long tempUserId
    ) {
        return ResponseEntity.ok()
                .body(ApiResponse.success(friendService.getFriendFollowList(tempUserId, PageRequest.of(page, size))));
    }

    // TODO: tempUserId -> tokenUtil.getUserId() 바꾸기
    @Operation(summary = "친구 목록 조회 API", description = "특정 유저의 친구 목록을 조회합니다.")
    @GetMapping("")
    public ResponseEntity<ApiResponse<PageResponse<FriendResponse>>> getFriendList(
            @RequestParam(required = false, defaultValue = "0") int page,
            @RequestParam(required = false, defaultValue = "10") int size, @RequestParam Long tempUserId
    ) {
        return ResponseEntity.ok()
                .body(ApiResponse.success(friendService.getFriendList(tempUserId, PageRequest.of(page, size))));
    }

    // TODO: tempUserId -> tokenUtil.getUserId() 바꾸기
    @Operation(summary = "친구 삭제 API", description = "특정 친구를 삭제합니다.")
    @DeleteMapping("/{friendId}")
    public ResponseEntity<ApiResponse<Void>> deleteFriend(@PathVariable Long friendId, @RequestParam Long tempUserId) {
        friendService.deleteFriend(tempUserId, friendId);
        return ResponseEntity.ok().body(ApiResponse.success());
    }

    // TODO: tempUserId -> tokenUtil.getUserId() 바꾸기
    @Operation(summary = "유저 검색 API", description = "특정 유저를 검색합니다.")
    @GetMapping("/search")
    public ResponseEntity<ApiResponse<PageResponse<FriendResponse>>> searchUser(
            @RequestParam String query,
            @RequestParam(required = false, defaultValue = "0") int page,
            @RequestParam(required = false, defaultValue = "10") int size, @RequestParam Long tempUserId
    ) {
        return ResponseEntity.ok()
                .body(ApiResponse.success(friendService.searchUser(tempUserId, query, PageRequest.of(page, size))));
    }

    // TODO: tempUserId -> tokenUtil.getUserId() 바꾸기
    @Operation(summary = "친구 추천 API", description = "특정 유저에게 친구를 추천합니다.")
    @GetMapping("/recommend")
    public ResponseEntity<ApiResponse<List<FriendResponse>>> recommendFriend(@RequestParam Long tempUserId) {
        return ResponseEntity.ok()
                .body(ApiResponse.success(friendService.randomRecommendFriend(tempUserId)));
    }
}
