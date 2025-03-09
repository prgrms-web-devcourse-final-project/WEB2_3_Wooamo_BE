package com.api.stuv.domain.friend.repository;

import com.api.stuv.domain.friend.dto.dto.FriendListDTO;
import com.api.stuv.domain.friend.dto.dto.FriendRecommendDTO;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface FriendRepositoryCustom {
    List<FriendListDTO> getFriendFollowList(Long receiverId);
    List<FriendListDTO> getFriendList(Long receiverId);
    List<FriendListDTO> searchUser(Long userId, String target, Pageable pageable);
    List<FriendRecommendDTO> recommendFriend(Long userId);
    Long getTotalFriendListPage(Long userId);
    Long getTotalSearchUserPage(Long userId, String target);
}
