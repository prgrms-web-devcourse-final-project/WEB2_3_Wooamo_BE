package com.api.stuv.domain.friend.service;

import com.api.stuv.domain.friend.dto.FriendFollowListResponse;
import com.api.stuv.domain.friend.dto.FriendFollowResponse;
import com.api.stuv.domain.friend.dto.FriendResponse;
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
import java.util.List;

@Service
@RequiredArgsConstructor
public class FriendService {

    private final UserRepository userRepository;
    private final FriendRepository friendRepository;

    // TODO: 알림 기능 추가시 알림 생성 로직 추가
    @Transactional
    public FriendFollowResponse requestFriend(Long userId, Long receiverId) {
        if ( userId.equals(receiverId) ) throw new BadRequestException(ErrorCode.FRIEND_REQUEST_SELF);
        if ( userRepository.isDuplicateIds(Arrays.asList(userId, receiverId)) != 2 ) throw new NotFoundException(ErrorCode.USER_NOT_FOUND);

        FriendStatus status = friendRepository.isFriendshipDuplicate(userId, receiverId);
        if ( status != null )
            if (status.equals(FriendStatus.PENDING)) throw new DuplicateException(ErrorCode.FRIEND_REQUEST_ALREADY_EXIST);
            else if (status.equals(FriendStatus.ACCEPTED)) throw new DuplicateException(ErrorCode.FRIEND_REQUEST_ALREADY_ACCEPTED);

        return FriendFollowResponse.from(friendRepository.save(Friend.init(userId, receiverId)));
    }

    @Transactional
    public FriendFollowResponse acceptFriend(Long userId, Long friendId) {
        Friend friend = friendRepository.findById(friendId).orElseThrow(() -> new NotFoundException(ErrorCode.FRIEND_REQUEST_NOT_FOUND));

        if ( !friend.getFriendId().equals(userId) ) throw new AccessDeniedException(ErrorCode.FRIEND_REQUEST_NOT_AUTHORIZED);
        if ( friend.getStatus().equals(FriendStatus.ACCEPTED) ) throw new DuplicateException(ErrorCode.FRIEND_REQUEST_ALREADY_ACCEPTED);

        friend.accept();

        return FriendFollowResponse.from(friend);
    }

    // TODO: profile image 경로 맞는지 확인
    @Transactional(readOnly = true)
    public PageResponse<FriendFollowListResponse> getFriendFollowList(Long userId, Pageable pageable) {
        return friendRepository.getFriendFollowList(userId, pageable, "http://localhost:8080/api/v1/costume/");
    }

    @Transactional(readOnly = true)
    public PageResponse<FriendResponse> getFriendList(Long userId, Pageable pageable) {
        return friendRepository.getFriendList(userId, pageable, "http://localhost:8080/api/v1/costume/");
    }

    @Transactional
    public void deleteFriend(Long userId, Long friendId) {
        Friend friend = friendRepository.findById(friendId).orElseThrow(() -> new NotFoundException(ErrorCode.FRIEND_NOT_FOUND));
        if ( friend.getStatus() == null ) throw new NotFoundException(ErrorCode.FRIEND_NOT_FOUND);
        if ( !(friend.getUserId().equals(userId) || friend.getFriendId().equals(userId)) ) throw new AccessDeniedException(ErrorCode.FRIEND_DELETE_NOT_AUTHORIZED);
        friendRepository.delete(friend);
    }

    @Transactional(readOnly = true)
    public PageResponse<FriendResponse> searchUser(Long userId, String target, Pageable pageable) {
        return friendRepository.searchUser(userId, target, pageable);
    }

    @Transactional(readOnly = true)
    public List<FriendResponse> randomRecommendFriend(Long userId) {
        return friendRepository.recommendFriend(userId);
    }
}
