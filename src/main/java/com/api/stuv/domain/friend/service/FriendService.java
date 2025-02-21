package com.api.stuv.domain.friend.service;

import com.api.stuv.domain.friend.dto.*;
import com.api.stuv.domain.friend.entity.Friend;
import com.api.stuv.domain.friend.entity.FriendStatus;
import com.api.stuv.domain.friend.repository.FriendRepository;
import com.api.stuv.domain.user.repository.UserRepository;
import com.api.stuv.global.exception.*;
import com.api.stuv.global.response.PageResponse;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Arrays;

@Service
@RequiredArgsConstructor
public class FriendService {

    private final UserRepository userRepository;
    private final FriendRepository friendRepository;

    // TODO: 알림 기능 추가시 알림 생성 로직 추가
    @Transactional
    public FriendFollowResponse requestFriend(Long userId, Long friendId) {
        // 두 유저가 존재하지 않을 경우
        if ( userRepository.isDuplicateIds(Arrays.asList(userId, friendId)) != 2 ) throw new NotFoundException(ErrorCode.USER_NOT_FOUND);

        // 이미 친구 요청을 보냈을 경우
        if (friendRepository.isFriendshipDuplicate(userId, friendId) > 0) throw new DuplicateException(ErrorCode.FRIEND_REQUEST_ALREADY_EXIST);

        return FriendFollowResponse.from(friendRepository.save(Friend.create(userId, friendId)));
    }

    @Transactional
    public FriendFollowResponse acceptFriend(Long userId, Long friendId) {
        Friend friend = friendRepository.findById(friendId).orElseThrow(() -> new NotFoundException(ErrorCode.FRIEND_REQUEST_NOT_FOUND));

        if ( !friend.getFriendId().equals(userId) ) throw new AccessDeniedException(ErrorCode.FRIEND_REQUEST_NOT_AUTHORIZED);
        if ( friend.getStatus().equals(FriendStatus.ACCEPTED) ) throw new DuplicateException(ErrorCode.FRIEND_REQUEST_ALREADY_ACCEPTED);

        friend.accept(); // 친구 요청 수락

        return FriendFollowResponse.from(friend);
    }

    // TODO: profile image 경로 맞는지 확인
    @Transactional(readOnly = true)
    public PageResponse<FriendFollowListResponse> getFriendFollowList(Long userId, Pageable pageable) {
        return friendRepository.getFriendFollowList(userId, pageable, "http://localhost:8080/api/v1/costume/");
    }
}
