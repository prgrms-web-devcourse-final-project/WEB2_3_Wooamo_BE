package com.api.stuv.domain.auth.controller;

import com.api.stuv.domain.auth.jwt.JWTUtil;
import com.api.stuv.domain.auth.service.ReissueService;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class ReissueController {
    private final JWTUtil jwtUtil;
    private final ReissueService reissueService;

    @PostMapping("/api/user/reissue")
    public ResponseEntity<?> reissue(HttpServletRequest request, HttpServletResponse response) {
        String refresh = null;

        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("refresh".equals(cookie.getName())) {
                    refresh =  cookie.getValue();
                }
            }
        }
        if (refresh == null) {

            //response status code
            return new ResponseEntity<>("refresh token null", HttpStatus.BAD_REQUEST);
        }

        //expired check
        try {
            jwtUtil.isExpired(refresh);
        } catch (ExpiredJwtException e) {
            //response status code
            return new ResponseEntity<>("refresh token expired", HttpStatus.BAD_REQUEST);
        }

        // 토큰 재발급
        String newAccessToken = reissueService.reissueAccessToken(refresh);
        String newRefreshToken = reissueService.reissueRefreshToken(refresh);

        // 응답에 토큰 추가
        response.setHeader("access", newAccessToken);
        response.addCookie(createCookie("refresh", newRefreshToken));

        return ResponseEntity.ok().build();
    }

    private Cookie createCookie(String key, String value) {
        Cookie cookie = new Cookie(key, value);
        cookie.setMaxAge(24*60*60);
        cookie.setHttpOnly(false);
        cookie.setPath("/");
        cookie.setSecure(true);
        cookie.setAttribute("SameSite", "None");

        return cookie;
    }
}
