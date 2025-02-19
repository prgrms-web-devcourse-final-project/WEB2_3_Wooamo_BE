package com.api.stuv.domain.friend.service;

import com.api.stuv.domain.friend.dto.FriendResponse.*;
import com.api.stuv.domain.friend.entity.Friend;
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

    @Transactional
    public RequestFriend requestFriend() {
        // 두 유저가 존재하지 않을 경우
        if ( userRepository.isDuplicateIds(Arrays.asList(1L, 3L)) != 2 ) throw new IllegalArgumentException("존재하지 않는 유저입니다."); // 나중에 Error Code 수정

        // 이미 친구 요청을 보냈을 경우
        if (friendRepository.isFriendshipDuplicate(1L, 3L) > 0) throw new IllegalArgumentException("이미 친구 요청을 보냈습니다."); // 나중에 Error Code 수정

        return RequestFriend.from(friendRepository.save(Friend.create(1L, 3L)).getFriendId());
    }
}
