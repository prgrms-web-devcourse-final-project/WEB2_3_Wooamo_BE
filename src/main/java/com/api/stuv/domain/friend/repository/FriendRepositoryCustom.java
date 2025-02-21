package com.api.stuv.domain.friend.repository;

import com.api.stuv.domain.friend.dto.FriendFollowListResponse;
import com.api.stuv.global.response.PageResponse;
import org.springframework.data.domain.Pageable;

public interface FriendRepositoryCustom {
    PageResponse<FriendFollowListResponse> getFriendFollowList(Long receiverId, Pageable pageable, String imageUrl);
}
