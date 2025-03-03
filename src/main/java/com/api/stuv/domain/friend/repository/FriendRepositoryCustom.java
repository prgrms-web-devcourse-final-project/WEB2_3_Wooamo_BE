package com.api.stuv.domain.friend.repository;

import com.api.stuv.domain.friend.dto.FriendFollowListResponse;
import com.api.stuv.domain.friend.dto.FriendResponse;
import com.api.stuv.global.response.PageResponse;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface FriendRepositoryCustom {
    PageResponse<FriendFollowListResponse> getFriendFollowList(Long receiverId, Pageable pageable, String imageUrl);
    PageResponse<FriendResponse> getFriendList(Long receiverId, Pageable pageable, String imageUrl);
    PageResponse<FriendResponse> searchUser(Long userId, String target, Pageable pageable);
    List<FriendResponse> recommendFriend(Long userId);
    Long getTotalFriendListPage(Long userId);
}
