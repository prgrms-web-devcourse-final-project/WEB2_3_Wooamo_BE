package com.api.stuv.domain.user.service;

import com.api.stuv.domain.auth.jwt.JWTUtil;
import com.api.stuv.domain.user.dto.request.KakaoUserRequest;
import com.api.stuv.domain.user.dto.request.UserRequest;
import com.api.stuv.domain.user.entity.User;
import com.api.stuv.domain.user.repository.UserRepository;
import com.api.stuv.global.service.RedisService;
import com.api.stuv.global.util.email.RandomCode;
import com.api.stuv.global.util.email.RandomName;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.time.Duration;

@Service
@RequiredArgsConstructor
public class KakaoService {
    private final RandomCode randomCode;
    private final RandomName randomName;
    private final UserService userService;
    private final JWTUtil jwtUtil;
    private final RedisService redisService;
    private final UserRepository userRepository;


    @Value("${kakao.api-key}")
    private String apiKey;

    @Value("${kakao.redirect-uri}")
    private String redirectUri;

    //카카오 회원 정보가 있을 시 로그인
    public String login(KakaoUserRequest kakaoUserRequest, HttpServletResponse response) {
        String email = kakaoUserRequest.email();
        Long userId = userRepository.findByEmail(email).getId();
        String role = "회원";

        //토큰 생성
        String access = jwtUtil.createJwt("access", userId, email, role, 600000L);
        String refresh = jwtUtil.createJwt("refresh", userId, email, role, 86400000L);

        //refresh 토큰 저장
        redisService.save(refresh, email, Duration.ofDays(1));

        //응답 설정
        response.setHeader("access", access);
        response.addCookie(createCookie("refresh", refresh));
        response.setStatus(HttpStatus.OK.value());

        return "로그인";
    }

    public String getKakaoAccessToken(String code) {
        RestTemplate rt = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("grant_type", "authorization_code");
        params.add("client_id", apiKey);                // 🔑 카카오 REST API 키
        params.add("redirect_uri", redirectUri);        // 🔄 등록된 redirect_uri
        params.add("code", code);                       // 📝 받은 인가 코드

        HttpEntity<MultiValueMap<String, String>> kakaoTokenReq = new HttpEntity<>(params, headers);
        ResponseEntity<String> res = null;
        try {
            res = rt.exchange(
                    "https://kauth.kakao.com/oauth/token",
                    HttpMethod.POST,
                    kakaoTokenReq,
                    String.class
            );
        } catch (HttpClientErrorException e) {
            System.out.println("[kakao Login HTTP API 오류] " + e.getMessage());
            return "Token Error";
        }

        // HTTP 응답 (JSON) -> Access Token 파씽
        String resBody = res.getBody();
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonNode = null;
        try {
            jsonNode = objectMapper.readTree(resBody);
        } catch (JsonProcessingException e) {
            System.out.println("[json 파싱 오류] " + e.getMessage());
        }

        // Access Token 반환
        return jsonNode.get("access_token").asText();
    }

    public KakaoUserRequest getKakaoUser(String accessToken) {
        System.out.println("accessToken: " + accessToken);
        // HTTP Header 생성
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + accessToken);
        headers.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");

        // HTTP 요청 보내기
        HttpEntity<MultiValueMap<String, String>> kakaoUserInfoRequest = new HttpEntity<>(headers);
        RestTemplate rt = new RestTemplate();
        ResponseEntity<String> res = null;
        try {
            res = rt.exchange(
                    "https://kapi.kakao.com/v2/user/me",
                    HttpMethod.POST,
                    kakaoUserInfoRequest,
                    String.class
            );
        } catch (HttpClientErrorException e){
            System.out.println("[kakao Data Access API 오류] " + e.getMessage());
            return null;
        }

        String resBody = res.getBody();
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonNode = null;
        try {
            jsonNode = objectMapper.readTree(resBody);
        } catch (JsonProcessingException e) {
            System.out.println("[json 파싱 오류] " + e.getMessage());
        }

        // 필요한 값 json에서 파싱
        //닉네임을 따로 입력하지 않아 랜덤으로 지정
        String nickname = randomName.getRandomName();
        String email = jsonNode.path("kakao_account").path("email").asText();
        //카카오 유저는 비밀번호가 없어서 랜덤 값으로 지정
        String password = randomCode.getRandomCode();
        Long kakaoId = jsonNode.get("id").asLong();

        KakaoUserRequest kakaoUser = new KakaoUserRequest(email, password, nickname, kakaoId);
        return kakaoUser;
    }

    //카카오 로그인 구현
    //정보가 있으면 로그인, 없으면 회원가입
    public String kakaoLogin(KakaoUserRequest kakaoUser, HttpServletResponse response){
        //String accessToken = getKakaoAccessToken(code);

        User user = userRepository.findBySocialId(kakaoUser.socialId());
        if(user == null){
            userService.registerKakaoUser(kakaoUser);
            return "회원가입";
        }
        else{
            login(kakaoUser, response);
            return "로그인";
        }

    }

    private Cookie createCookie(String key, String value) {
        Cookie cookie = new Cookie(key, value);
        cookie.setMaxAge(24*60*60);
        cookie.setHttpOnly(true);
        //cookie.setPath("/");

        return cookie;
    }

}
