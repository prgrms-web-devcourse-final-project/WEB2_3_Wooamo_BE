package com.api.stuv.domain.friend.service;

import com.api.stuv.domain.friend.dto.response.FriendFollowResponse;
import com.api.stuv.domain.friend.entity.Friend;
import com.api.stuv.domain.friend.entity.FriendStatus;
import com.api.stuv.domain.friend.repository.FriendRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.lang.reflect.Field;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
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
        Friend friend = createFriendWithId(friendId, senderId, receiverId);

        when(friendRepository.findById(friendId)).thenReturn(Optional.of(friend));

        // when
        FriendFollowResponse response = friendService.acceptFriend(receiverId, friendId);

        // then
        assertThat(friend.getStatus()).isEqualTo(FriendStatus.ACCEPTED);
        assertThat(response).isNotNull();
        assertThat(response.receiverId()).isEqualTo(receiverId);
        assertThat(response.friendId()).isEqualTo(friendId);
    }

    public Friend createFriendWithId(Long id, Long userId, Long friendId) {
        Friend friend = Friend.init(userId, friendId);
        setId(friend, id);
        return friend;
    }

    private void setId(Friend friend, Long id) {
        try {
            Field field = Friend.class.getDeclaredField("id");
            field.setAccessible(true);
            field.set(friend, id);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }
}