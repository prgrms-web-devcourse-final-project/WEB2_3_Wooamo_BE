package com.api.stuv.domain.alert.controller;

import com.api.stuv.domain.alert.service.SseService;
import com.api.stuv.domain.auth.util.TokenUtil;
import com.api.stuv.global.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@RestController
@RequestMapping("/api/sse")
@RequiredArgsConstructor
@Tag(name = "SSE", description = "SSE 관련 API")
public class SseController {
    private final SseService sseService;
    private final TokenUtil tokenUtil;

    @Operation(summary = "SSE 연결 API", description = "SSE 연결을 합니다.")
    @GetMapping("/connect")
    public SseEmitter connect() {
        return sseService.connect(tokenUtil.getUserId());
    }

    @Operation(summary = "SSE 연결 해제 API", description = "SSE 연결을 해제합니다.")
    @GetMapping("/disconnect")
    public ResponseEntity<ApiResponse<Void>> disconnect() {
        sseService.disconnect(tokenUtil.getUserId());
        return ResponseEntity.ok().body(ApiResponse.success());
    }
}
