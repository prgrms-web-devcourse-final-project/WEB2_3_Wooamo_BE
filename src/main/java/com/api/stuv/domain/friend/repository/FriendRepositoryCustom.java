package com.api.stuv.domain.friend.repository;

import com.api.stuv.domain.friend.dto.FriendRequestListResponse;
import com.api.stuv.global.response.PageResponse;
import org.springframework.data.domain.Pageable;

public interface FriendRepositoryCustom {
    PageResponse<FriendRequestListResponse> getFriendRequestList(Long receiverId, Pageable pageable, String imageUrl);
}
