package com.api.stuv.domain.friend.service;

import com.api.stuv.domain.friend.dto.*;
import com.api.stuv.domain.friend.entity.Friend;
import com.api.stuv.domain.friend.entity.FriendStatus;
import com.api.stuv.domain.friend.repository.FriendRepository;
import com.api.stuv.domain.user.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
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
        if ( userRepository.isDuplicateIds(Arrays.asList(userId, friendId)) != 2 ) throw new IllegalArgumentException("존재하지 않는 유저입니다."); // 나중에 Error Code 수정

        // 이미 친구 요청을 보냈을 경우
        if (friendRepository.isFriendshipDuplicate(userId, friendId) > 0) throw new IllegalArgumentException("이미 친구 요청을 보냈습니다."); // 나중에 Error Code 수정

        return FriendFollowResponse.from(friendRepository.save(Friend.create(userId, friendId)));
    }

    @Transactional
    public FriendFollowResponse acceptFriend(Long friendId) {
        Friend friend = friendRepository.findById(friendId).orElseThrow(() -> new IllegalArgumentException("존재하지 않는 친구 요청입니다.")); // 나중에 Error Code 수정

        if ( friend.getStatus().equals(FriendStatus.ACCEPTED) ) throw new IllegalArgumentException("이미 수락된 친구요청입니다."); // 나중에 Error Code 수정

        friend.accept(); // 친구 요청 수락

        return FriendFollowResponse.from(friend);
    }
}
