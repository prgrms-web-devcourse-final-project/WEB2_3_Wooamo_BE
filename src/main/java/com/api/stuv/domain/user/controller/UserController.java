package com.api.stuv.domain.user.controller;

import com.api.stuv.domain.auth.util.TokenUtil;
import com.api.stuv.domain.user.dto.request.EmailCertificationRequest;
import com.api.stuv.domain.user.dto.request.UserRequest;
import com.api.stuv.domain.user.dto.response.MyInformationResponse;
import com.api.stuv.domain.user.service.KakaoService;
import com.api.stuv.domain.user.service.UserService;
import com.api.stuv.global.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
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
    private final TokenUtil tokenUtil;

    @Operation(summary = "인증메일 전송 API", description = "인증 메일을 전송합니다.")
    @PostMapping("/auth/send")
    public ResponseEntity<ApiResponse<Void>> sendCertificateEmail(@RequestBody UserRequest userRequest){
        userservice.sendCertificateEmail(userRequest.email());

        return ResponseEntity.ok()
                .body(ApiResponse.success());
    }

    @Operation(summary = "메일 인증 API", description = "메일로 보낸 인증 코드를 확인합니다")
    @PostMapping("/auth/check")
    public ResponseEntity<ApiResponse<Void>> checkCertificateEmail(@RequestBody @Valid EmailCertificationRequest emailCertificationRequest){
        userservice.checkCertificateEmail(emailCertificationRequest);

        return ResponseEntity.ok()
                .body(ApiResponse.success());
    }

    @Operation(summary = "회원가입 API", description = "회원가입 API입니다.")
    @PostMapping("/register")
    private ResponseEntity<ApiResponse<Void>> registerUser(@RequestBody UserRequest userRequest){
        userservice.registerUser(userRequest);

        return ResponseEntity.ok()
                .body(ApiResponse.success());
    }

    @Operation(summary = "카카오 로그인 API", description = "카카오 로그인 API 입니다.")
    @GetMapping("/kakaoLogin")
    private ResponseEntity<ApiResponse<String>> kakaoLogin(@RequestParam("code") String code, HttpServletResponse response, HttpServletRequest request){

        return ResponseEntity.ok()
                .body(ApiResponse.success(kakaoService.kakaoLogin(code, response, request)));
    }

    @Operation(summary = "닉네임 중복확인 API", description = "등록하려는 닉네임이 중복인지 확인합니다.")
    @PostMapping("/nickname")
    private ResponseEntity<ApiResponse<Void>> duplicateNickname(@RequestBody @Valid UserRequest userRequest){
        userservice.checkDuplicateNickname(userRequest.nickname());

        return ResponseEntity.ok()
                .body(ApiResponse.success());
    }

    @Operation(summary = "내 정보 가져오기 API", description = "본인의 정보를 가져옵니다.")
    @GetMapping
    private ResponseEntity<ApiResponse<MyInformationResponse>> getMyInformation(){
        Long myId = tokenUtil.getUserId();

        return ResponseEntity.ok()
                .body(ApiResponse.success(userservice.getMyInformation(myId)));
    }

}
