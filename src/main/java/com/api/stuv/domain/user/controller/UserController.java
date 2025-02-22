package com.api.stuv.domain.user.controller;

import com.api.stuv.domain.user.dto.request.EmailCertificationRequest;
import com.api.stuv.domain.user.dto.request.UserRequest;
import com.api.stuv.domain.user.service.KakaoLoginService;
import com.api.stuv.domain.user.service.userService;
import com.api.stuv.global.response.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

@RestController
@RequiredArgsConstructor
@Tag(name = "User API", description = "유저 인증 관련 API")
@RequestMapping("/api/user")
public class UserController {
    private final userService userservice;
    private final KakaoLoginService  kakaoLoginService;


    @PostMapping("/auth/send")
    public ResponseEntity<ApiResponse<Void>> sendCertificateEmail(@RequestBody UserRequest userRequest){
        userservice.sendCertificateEmail(userRequest.email());

        return ResponseEntity.ok()
                .body(ApiResponse.success());
    }

    @PostMapping("/auth/check")
    public ResponseEntity<ApiResponse<Void>> checkCertificateEmail(@RequestBody @Valid EmailCertificationRequest emailCertificationRequest){
        userservice.checkCertificateEmail(emailCertificationRequest);

        return ResponseEntity.ok()
                .body(ApiResponse.success());
    }

    @PostMapping("/register")
    private ResponseEntity<ApiResponse<Void>> registerUser(@RequestBody UserRequest userRequest){
        userservice.registerUser(userRequest);

        return ResponseEntity.ok()
                .body(ApiResponse.success());
    }

    @GetMapping("/kakaoLogin")
    private String  kakaoLogin(@RequestParam("code") String code){
        System.out.println("code:"+code);
        return kakaoLoginService.getKakaoAccessToken(code);
    }

}
