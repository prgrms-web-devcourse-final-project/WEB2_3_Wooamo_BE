package com.api.stuv.domain.user.service;

import com.api.stuv.domain.user.dto.request.EmailCertificationRequest;
import com.api.stuv.domain.user.dto.request.UserRequest;
import com.api.stuv.domain.user.entity.RoleType;
import com.api.stuv.domain.user.entity.User;
import com.api.stuv.domain.user.repository.UserRepository;
import com.api.stuv.domain.user.response.GenerateNicknameResponse;
import com.api.stuv.global.exception.BadRequestException;
import com.api.stuv.global.exception.ErrorCode;
import com.api.stuv.global.exception.NotFoundException;
import com.api.stuv.global.service.RedisService;
import com.api.stuv.global.util.email.RandomCode;
import com.api.stuv.global.util.email.provider.EmailProvider;
import jakarta.transaction.Transactional;
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
    private final EmailProvider  emailProvider;

    private static final String NICKNAME_GENERATOR_URI = "https://nickname.hwanmoo.kr/?format=json";
    private final RedisService redisService;

    public void registerUser(UserRequest userRequest) {
        String email = userRequest.email();
        String password = userRequest.password();
        String nickname = userRequest.nickname() != null? userRequest.nickname() : randomName();

        if(!redisService.find(email, String.class).equals("Verified")){
            throw new BadRequestException(ErrorCode.NOT_VERIFICATION_EMAIL);
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
        Boolean isExist = userRepository.existsByEmail(email);

        if(isExist){
            throw new NotFoundException(ErrorCode.USER_ALREADY_EXIST);
        }

        String verificationCode = RandomCode.getRandomCode();
        emailProvider.sendMail(email, verificationCode);
    }

    @Transactional
    public void checkCertificateEmail(EmailCertificationRequest emailCertificationRequest){
        String userCode = emailCertificationRequest.code();
        String email = emailCertificationRequest.email();

        String code = redisService.find(email, String.class);

        if(code == null){
            //코드 만료
            throw new NotFoundException(ErrorCode.CODE_EXPIRED);
            //return false;
        }
        if(code.equals(userCode)){
            redisService.delete(email);
            redisService.save(email, "Verified", 600000L);
        } else {
            throw new NotFoundException(ErrorCode.WRONG_VERIFICATION_CODE);
        }
    }

}
