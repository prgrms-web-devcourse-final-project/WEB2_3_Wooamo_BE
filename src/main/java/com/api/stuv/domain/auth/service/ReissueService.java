package com.api.stuv.domain.auth.service;

import com.api.stuv.domain.auth.jwt.JWTUtil;
import com.api.stuv.global.service.RedisService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
@RequiredArgsConstructor
public class ReissueService {
    private final JWTUtil jwtUtil;
    private final RedisService redisService;

    public String reissueAccessToken(String refreshToken) {
        validateRefreshToken(refreshToken);

        String username = jwtUtil.getUsername(refreshToken);
        String role = jwtUtil.getRole(refreshToken);

        return jwtUtil.createJwt("access", username, role, 600_000L); // 10분
    }

    public String reissueRefreshToken(String refreshToken) {
        validateRefreshToken(refreshToken);

        String username = jwtUtil.getUsername(refreshToken);
        String role = jwtUtil.getRole(refreshToken);

        String newRefreshToken = jwtUtil.createJwt("refresh", username, role, 86400000L);
        redisService.delete(refreshToken);
        redisService.save(username, newRefreshToken, Duration.ofDays(1));

        return  newRefreshToken;// 24시간
    }

    private void validateRefreshToken(String refreshToken) {
        if (refreshToken == null) {
            throw new IllegalArgumentException("Not exist refresh token"); // 커스텀 예외
        }

        if (jwtUtil.isExpired(refreshToken)) {
            throw new IllegalArgumentException("refresh token is expired"); // 커스텀 예외
        }

        if (!"refresh".equals(jwtUtil.getCategory(refreshToken))) {
            throw new IllegalArgumentException("Invalid refresh token category");
        }

    }
}