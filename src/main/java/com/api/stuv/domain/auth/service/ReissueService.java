package com.api.stuv.domain.auth.service;

import com.api.stuv.domain.auth.jwt.JWTUtil;
import com.api.stuv.global.exception.ErrorCode;
import com.api.stuv.global.exception.NotFoundException;
import com.api.stuv.global.exception.ValidationException;
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

        String email = jwtUtil.getUsername(refreshToken);
        String role = jwtUtil.getRole(refreshToken);
        Long userId = jwtUtil.getUserId(refreshToken);

        return jwtUtil.createJwt("access",userId, email, role, 600_000L); // 10분
    }

    public String reissueRefreshToken(String refreshToken) {
        validateRefreshToken(refreshToken);

        String email = jwtUtil.getUsername(refreshToken);
        String role = jwtUtil.getRole(refreshToken);
        Long userId = jwtUtil.getUserId(refreshToken);

        String newRefreshToken = jwtUtil.createJwt("refresh", userId, email, role, 86400000L);
        redisService.delete(refreshToken);
        redisService.save(newRefreshToken, email, Duration.ofDays(1));

        return  newRefreshToken;// 24시간
    }

    private void validateRefreshToken(String refreshToken) {
        if (refreshToken == null) {
            throw new NotFoundException(ErrorCode.EMPTY_REFRESH_TOKEN); // 커스텀 예외
        }

        if (jwtUtil.isExpired(refreshToken)) {
            throw new NotFoundException(ErrorCode.EXPIRED_REFRESH_TOKEN); // 커스텀 예외
        }

        if (!"refresh".equals(jwtUtil.getCategory(refreshToken))) {
            throw new NotFoundException(ErrorCode.EMPTY_REFRESH_TOKEN);
        }

        if(!redisService.exists(refreshToken)) {
            throw new NotFoundException(ErrorCode.EMPTY_REFRESH_TOKEN);
        }
    }
}