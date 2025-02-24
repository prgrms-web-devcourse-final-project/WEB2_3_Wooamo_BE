package com.api.stuv.domain.user.dto.request;

import com.api.stuv.domain.user.entity.RoleType;
import com.api.stuv.domain.user.entity.User;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public record KakaoUserRequest(
        String email,
        String password,
        String nickname,
        Long socialId
) {
    public static User kakaoFrom(KakaoUserRequest kakaoUserRequest, Long socialId, BCryptPasswordEncoder bCryptPasswordEncoder){
        return User.builder()
                .email(kakaoUserRequest.email())
                .password(bCryptPasswordEncoder.encode(kakaoUserRequest.password()))
                .nickname(kakaoUserRequest.nickname())
                .socialId(socialId)
                .costumeId(1L)
                .context("")
                .blogLink("")
                .role(RoleType.USER)
                .build();
    }
}
