package com.api.stuv.domain.user.controller;

import com.api.stuv.domain.user.dto.request.EmailCertificationRequest;
import com.api.stuv.domain.user.dto.request.KakaoUserRequest;
import com.api.stuv.domain.user.dto.request.UserRequest;
import com.api.stuv.domain.user.service.KakaoService;
import com.api.stuv.domain.user.service.UserService;
import com.api.stuv.global.response.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@Tag(name = "User API", description = "유저 인증 관련 API")
@RequestMapping("/api/user")
public class UserController {
    private final UserService userservice;
    private final KakaoService kakaoService;


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
    private ResponseEntity<ApiResponse<String>> kakaoLogin(@RequestParam("code") String code, HttpServletResponse response, HttpServletRequest request){

        return ResponseEntity.ok()
                .body(ApiResponse.success(kakaoService.kakaoLogin(code, response, request)));
    }
}
