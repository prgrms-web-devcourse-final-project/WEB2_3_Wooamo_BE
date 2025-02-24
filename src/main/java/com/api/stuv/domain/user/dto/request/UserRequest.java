package com.api.stuv.domain.user.dto.request;

import com.api.stuv.domain.user.entity.RoleType;
import com.api.stuv.domain.user.entity.User;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public record UserRequest(
        String email,
        String password,
        String nickname
) {
    public static User from(UserRequest userRequest, BCryptPasswordEncoder bCryptPasswordEncoder) {
        return User.builder()
                .email(userRequest.email())
                .password(bCryptPasswordEncoder.encode(userRequest.password()))
                .nickname(userRequest.nickname())
                .costumeId(1L)
                .role(RoleType.USER)
                .build();
    }
}
