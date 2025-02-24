package com.api.stuv.domain.auth.controller;

import com.api.stuv.domain.auth.util.TokenUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@ResponseBody
@RequiredArgsConstructor
public class MainController {

    private final TokenUtil tokenUtil;

    @Value("${kakao.redirect-uri}")
    private String redirectUri;

    @Value("${kakao.api-key}")
    private String kakaoApiKey;

    @GetMapping("/")
    public String mainP(){

        Long userId = tokenUtil.getUserId();
        String email = tokenUtil.getEmail();
        String role = tokenUtil.getRole();

        return "main controller " + email + " " + role + " " + userId;
    }
}
