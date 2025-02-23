package com.api.stuv.domain.user.service;

import com.api.stuv.domain.user.dto.request.EmailCertificationRequest;
import com.api.stuv.domain.user.dto.request.KakaoUserRequest;
import com.api.stuv.domain.user.dto.request.UserRequest;
import com.api.stuv.domain.user.entity.RoleType;
import com.api.stuv.domain.user.entity.User;
import com.api.stuv.domain.user.repository.UserRepository;
import com.api.stuv.global.exception.BadRequestException;
import com.api.stuv.global.exception.ErrorCode;
import com.api.stuv.global.exception.NotFoundException;
import com.api.stuv.global.service.RedisService;
import com.api.stuv.global.util.email.RandomCode;
import com.api.stuv.global.util.email.RandomName;
import com.api.stuv.global.util.email.provider.EmailProvider;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Duration;


@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final EmailProvider  emailProvider;
    private final RedisService redisService;
    private final RandomName randomName;

    public void registerUser(UserRequest userRequest) {
        String email = userRequest.email();
        String password = userRequest.password();
        String nickname = userRequest.nickname() != null? userRequest.nickname() : randomName.getRandomName();

        if(!redisService.find(email, String.class).equals("Verified") || redisService.find(email, String.class) == null){
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

    public void registerKakaoUser(KakaoUserRequest kakaoUserRequest) {
        String email = kakaoUserRequest.email();
        String password = kakaoUserRequest.password();
        String nickname = kakaoUserRequest.nickname();
        Long socialId = kakaoUserRequest.socialId();

        Boolean isExist = userRepository.existsByEmail(email);

        if(isExist){
            throw new NotFoundException(ErrorCode.USER_ALREADY_EXIST);
        }

        User user = User.builder()
                .email(email)
                .password(bCryptPasswordEncoder.encode(password))
                .nickname(nickname)
                .socialId(socialId)
                .costumeId(1L)
                .role(RoleType.USER)
                .build();

        userRepository.save(user);
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
            redisService.save(email, "Verified", Duration.ofMinutes(10));
        } else {
            throw new NotFoundException(ErrorCode.WRONG_VERIFICATION_CODE);
        }
    }

    public void checkDuplicateNickname(String nickname){
        System.out.println(nickname);
        if(userRepository.existsByNickname(nickname)){
            throw new BadRequestException(ErrorCode.NICKNAME_ALREADY_EXIST);
        }
    }
}
