package com.api.stuv.domain.friend.service;

import com.api.stuv.domain.alert.dto.AlertEventDTO;
import com.api.stuv.domain.alert.entity.AlertType;
import com.api.stuv.domain.friend.dto.response.FriendFollowResponse;
import com.api.stuv.domain.friend.dto.response.FriendResponse;
import com.api.stuv.domain.friend.entity.Friend;
import com.api.stuv.domain.friend.entity.FriendStatus;
import com.api.stuv.domain.friend.repository.FriendRepository;
import com.api.stuv.domain.image.entity.EntityType;
import com.api.stuv.domain.image.service.S3ImageService;
import com.api.stuv.domain.user.entity.User;
import com.api.stuv.domain.user.repository.UserRepository;
import com.api.stuv.global.exception.*;
import com.api.stuv.global.response.PageResponse;
import org.springframework.context.ApplicationEventPublisher;
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
    private final ApplicationEventPublisher eventPublisher;
    private final S3ImageService s3ImageService;

    @Transactional
    public FriendFollowResponse requestFriend(Long userId, Long receiverId) {
        User sender = userRepository.findById(userId).orElseThrow(() -> new NotFoundException(ErrorCode.USER_NOT_FOUND));
        if ( userId.equals(receiverId) ) throw new BadRequestException(ErrorCode.FRIEND_REQUEST_SELF);
        if ( userRepository.isDuplicateIds(Arrays.asList(userId, receiverId)) != 2 ) throw new NotFoundException(ErrorCode.USER_NOT_FOUND);

        FriendStatus status = friendRepository.isFriendshipDuplicate(userId, receiverId);
        if ( status != null ) {
            if (status.equals(FriendStatus.PENDING)) { throw new DuplicateException(ErrorCode.FRIEND_REQUEST_ALREADY_EXIST); }
            else if (status.equals(FriendStatus.ACCEPTED)) { throw new DuplicateException(ErrorCode.FRIEND_REQUEST_ALREADY_ACCEPTED); }
        }
        eventPublisher.publishEvent(new AlertEventDTO(receiverId, null, AlertType.FOLLOW, null, sender.getNickname()));
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

    @Transactional(readOnly = true)
    public PageResponse<FriendResponse> getFriendFollowList(Long userId, Pageable pageable) {
        List<FriendResponse> frinedList = friendRepository.getFriendFollowList(userId, pageable).stream().map( dto -> new FriendResponse(
                dto.friendId(),
                null,
                dto.userId(),
                dto.nickname(),
                dto.context(),
                getCostume(dto.costumeId(), dto.newFilename()),
                null)).toList();
        return PageResponse.applyPage(frinedList, pageable, friendRepository.getTotalFriendFollowListPage(userId));
    }

    @Transactional(readOnly = true)
    public PageResponse<FriendResponse> getFriendList(Long userId, Pageable pageable) {
        List<FriendResponse> frinedList = friendRepository.getFriendList(userId, pageable).stream().map( dto -> new FriendResponse(
                dto.friendId(),
                dto.userId(),
                null,
                dto.nickname(),
                dto.context(),
                getCostume(dto.costumeId(), dto.newFilename()),
                null)).toList();
        return PageResponse.applyPage(frinedList, pageable, friendRepository.getTotalFriendListPage(userId));
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
        List<FriendResponse> userList = friendRepository.searchUser(userId, target, pageable).stream().map(dto -> new FriendResponse(
                null,
                dto.userId(),
                null,
                dto.nickname(),
                dto.context(),
                getCostume(dto.costumeId(), dto.newFilename()),
                dto.status())).toList();
        return PageResponse.applyPage(userList, pageable, friendRepository.getTotalSearchUserPage(userId, target));
    }

    @Transactional(readOnly = true)
    public List<FriendResponse> randomRecommendFriend(Long userId) {
        return friendRepository.recommendFriend(userId).stream().map(dto -> new FriendResponse(
                null,
                dto.userId(),
                null,
                dto.nickname(),
                dto.context(),
                getCostume(dto.costumeId(), dto.newFilename()),
                null)).toList();
    }

    private String getCostume(Long costumeId, String newFilename) {
        return s3ImageService.generateImageFile(EntityType.COSTUME, costumeId, newFilename);
    }
}