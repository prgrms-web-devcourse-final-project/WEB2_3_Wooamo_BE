package com.api.stuv.domain.user.controller;

import com.api.stuv.domain.user.dto.request.UserRequest;
import com.api.stuv.domain.user.service.userService;
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
    private String registerUser(@RequestBody UserRequest userRequest){
        System.out.println(userRequest.email());
        userservice.registerUser(userRequest);

        return "success";
    }

    @PostMapping("/auth/send")
    public String sendCertificateEmail(@RequestBody UserRequest userRequest){
        userservice.sendCertificateEmail(userRequest.email());

        return "send certificate email";
    }

    @PostMapping("/auth/check/{userCode}")
    public String checkCertificateEmail(
            @RequestBody UserRequest userRequest,
            @PathVariable String userCode
    ){
        System.out.println(userCode);
        userservice.checkCertificateEmail(userRequest.email(), userCode);

        return "check certificate email success";
    }
}
