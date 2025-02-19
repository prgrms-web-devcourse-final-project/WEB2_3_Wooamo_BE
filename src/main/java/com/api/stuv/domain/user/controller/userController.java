package com.api.stuv.domain.user.controller;

import com.api.stuv.domain.user.dto.userDTO;
import com.api.stuv.domain.user.service.userService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Tag(name = "User API", description = "유저 인증 관련 API")
@RequestMapping("/api/user")
public class userController {
    private final userService userservice;

    @PostMapping("/register")
    private String registerUser(@RequestBody userDTO userDTO){
        System.out.println(userDTO.getEmail());
        userservice.registerUser(userDTO);

        return "success";
    }
}
