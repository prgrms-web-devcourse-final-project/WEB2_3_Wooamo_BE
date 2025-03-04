package com.api.stuv.domain.friend.repository;

import com.api.stuv.domain.friend.dto.dto.FriendListDTO;
import com.api.stuv.domain.friend.dto.response.FriendResponse;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface FriendRepositoryCustom {
    List<FriendListDTO> getFriendFollowList(Long receiverId, Pageable pageable);
    List<FriendListDTO> getFriendList(Long receiverId, Pageable pageable);
    List<FriendListDTO> searchUser(Long userId, String target, Pageable pageable);
    List<FriendResponse> recommendFriend(Long userId);
    Long getTotalFriendFollowListPage(Long receiverId);
    Long getTotalFriendListPage(Long userId);
    Long getTotalSearchUserPage(Long userId, String target);
}
