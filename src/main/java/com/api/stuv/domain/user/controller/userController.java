package com.api.stuv.domain.user.controller;

import com.api.stuv.domain.user.dto.request.EmailCertificationRequest;
import com.api.stuv.domain.user.dto.request.UserRequest;
import com.api.stuv.domain.user.service.userService;
import com.api.stuv.global.response.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@Tag(name = "User API", description = "유저 인증 관련 API")
@RequestMapping("/api/user")
public class userController {
    private final userService userservice;

    @PostMapping("/register")
    private ResponseEntity<ApiResponse<Void>> registerUser(@RequestBody UserRequest userRequest){
        userservice.registerUser(userRequest);

        return ResponseEntity.ok()
                .body(ApiResponse.success());
    }

    @PostMapping("/auth/send")
    public ResponseEntity<ApiResponse<Void>> sendCertificateEmail(@RequestBody UserRequest userRequest){
        userservice.sendCertificateEmail(userRequest.email());

        return ResponseEntity.ok()
                .body(ApiResponse.success());
    }

    @PostMapping("/auth/check")
    public ResponseEntity<ApiResponse<Void>> checkCertificateEmail(
            @RequestBody EmailCertificationRequest emailCertificationRequest
    ){
        userservice.checkCertificateEmail(emailCertificationRequest);

        return ResponseEntity.ok()
                .body(ApiResponse.success());
    }
}
