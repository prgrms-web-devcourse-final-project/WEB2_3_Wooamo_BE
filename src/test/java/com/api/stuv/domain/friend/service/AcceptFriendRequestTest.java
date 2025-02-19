package com.api.stuv.domain.friend.service;

import com.api.stuv.domain.friend.dto.FriendFollowResponse;
import com.api.stuv.domain.friend.entity.Friend;
import com.api.stuv.domain.friend.entity.FriendStatus;
import com.api.stuv.domain.friend.repository.FriendRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AcceptFriendRequestTest {

    @Mock
    private FriendRepository friendRepository;

    @InjectMocks
    private FriendService friendService;

    Long friendId = 1L;
    Long senderId = 1L;
    Long receiverId = 2L;

    @Test
    void successAcceptFriendRequest() {
        // given


        Friend friend = Friend.builder()
                .id(friendId)
                .userId(senderId)
                .friendId(receiverId)
                .status(FriendStatus.PENDING)
                .build();

        when(friendRepository.findById(friendId)).thenReturn(Optional.of(friend));

        // when
        FriendFollowResponse response = friendService.acceptFriend(receiverId, friendId);

        // then
        assertEquals(FriendStatus.ACCEPTED, friend.getStatus()); // 친구 요청 수락 상태로 변경
        assertEquals(receiverId, response.receiverId()); // 친구 요청을 받은 유저가 맞는지 확인
        assertNotNull(response); // response가 null이 아닌지 확인
        assertEquals(friendId, response.friendId()); // 테이블 Pk 값의 일치 여부 확인
    }

    @Test
    void nonExistentAcceptFriendRequest() {
        // given
        Friend friend = Friend.builder()
                .id(friendId)
                .userId(senderId)
                .friendId(receiverId)
                .status(FriendStatus.PENDING)
                .build();

        when(friendRepository.findById(friendId)).thenReturn(Optional.empty());

        // when
        FriendFollowResponse response = friendService.acceptFriend(receiverId, friendId);

        // then
        assertEquals(FriendStatus.ACCEPTED, friend.getStatus()); // 친구 요청 수락 상태로 변경
        assertEquals(receiverId, response.receiverId()); // 친구 요청을 받은 유저가 맞는지 확인
        assertNotNull(response); // response가 null이 아닌지 확인
        assertEquals(friendId, response.friendId()); // 테이블 Pk 값의 일치 여부 확인dId());
    }

    @Test
    void alreadyAcceptAcceptFriendRequest() {
        // given
        Friend friend = Friend.builder()
                .id(friendId)
                .userId(senderId)
                .friendId(receiverId)
                .status(FriendStatus.ACCEPTED)
                .build();

        when(friendRepository.findById(friendId)).thenReturn(Optional.of(friend));

        // when
        FriendFollowResponse response = friendService.acceptFriend(receiverId, friendId);

        // then
        assertEquals(FriendStatus.ACCEPTED, friend.getStatus()); // 친구 요청 수락 상태로 변경
        assertEquals(receiverId, response.receiverId()); // 친구 요청을 받은 유저가 맞는지 확인
        assertNotNull(response); // response가 null이 아닌지 확인
        assertEquals(friendId, response.friendId()); // 테이블 Pk 값의 일치 여부 확인
    }
}