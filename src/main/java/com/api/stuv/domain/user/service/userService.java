package com.api.stuv.domain.user.service;

import com.api.stuv.domain.user.dto.request.UserRequest;
import com.api.stuv.domain.user.entity.RoleType;
import com.api.stuv.domain.user.entity.User;
import com.api.stuv.domain.user.repository.UserRepository;
import com.api.stuv.domain.user.response.GenerateNicknameResponse;
import com.api.stuv.global.service.RedisService;
import com.api.stuv.global.util.email.RandomCode;
import com.api.stuv.global.util.email.provider.EmailProvider;
import jakarta.mail.internet.MimeMessage;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;


@Service
@RequiredArgsConstructor
public class userService {
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final WebClient webClient;
    private final EmailProvider  emailProvider;

    private static final String NICKNAME_GENERATOR_URI = "https://nickname.hwanmoo.kr/?format=json";
    private final RedisService redisService;

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

    public void sendCertificateEmail(String email){
        String verificationCode = RandomCode.getRandomCode();
        emailProvider.sendMail(email, verificationCode);
    }

    @Transactional
    public boolean checkCertificateEmail(String email, String userCode){
        String code = redisService.find(email, String.class);

        if(code == null){
            System.out.println("코드 만료");
            return false;
        }

        if(code.equals(userCode)){
            System.out.println("인증 성공");
            redisService.delete(email);
            return true;
        } else {
            System.out.println("인증 실패");
            return false;
        }

    }

}
