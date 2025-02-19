package com.api.stuv.domain.user.service;

import com.api.stuv.domain.user.dto.request.UserRequest;
import com.api.stuv.domain.user.entity.RoleType;
import com.api.stuv.domain.user.entity.User;
import com.api.stuv.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class userService {
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    public void registerUser(UserRequest userRequest) {
        String email = userRequest.email();
        String password = userRequest.password();
        String nickname = userRequest.nickname();

        Boolean isExist = userRepository.existsByEmail(email);

        if(isExist){
            System.out.println("이미 회원가입된 이메일 입니다.");
            return;
        }

        User user = User.builder()
                .email(email)
                .password(bCryptPasswordEncoder.encode(password))
                .nickname(nickname)
                .costumeId(1L)
                .role(RoleType.USER)
                .build();

        userRepository.save(user);
    }
}
