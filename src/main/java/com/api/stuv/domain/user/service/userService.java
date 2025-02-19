package com.api.stuv.domain.user.service;

import com.api.stuv.domain.user.dto.request.UserRequest;
import com.api.stuv.domain.user.entity.RoleType;
import com.api.stuv.domain.user.entity.User;
import com.api.stuv.domain.user.repository.UserRepository;
import com.api.stuv.domain.user.response.GenerateNicknameResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;


@Service
@RequiredArgsConstructor
public class userService {
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final WebClient webClient;
    private static final String NICKNAME_GENERATOR_URI = "https://nickname.hwanmoo.kr/?format=json";

    public void registerUser(UserRequest userRequest) {
        String email = userRequest.email();
        String password = userRequest.password();
        String nickname = userRequest.nickname() != null? userRequest.nickname() : randomName();

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

    public String randomName(){
        GenerateNicknameResponse response = webClient.get()
                .uri(NICKNAME_GENERATOR_URI)
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .retrieve()
                .bodyToMono(GenerateNicknameResponse.class)
                .block();

        String randomNickname = String.join("", response.getWords());
        return randomNickname;
    }
}
