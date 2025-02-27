package com.api.stuv.domain.timer.controller;

import com.api.stuv.domain.timer.dto.response.TimerListResponse;
import com.api.stuv.domain.timer.service.TimerService;
import com.api.stuv.global.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Tag(name = "Timer API", description = "타이머 관련 API")
@RequestMapping("/api/timer")
public class TimerController {
    private final TimerService timerService;

    @Operation(summary = "타이머 목록 조회 API", description = "타이머 목록을 조회합니다")
    @GetMapping("")
    public ResponseEntity<ApiResponse<List<TimerListResponse>>> getTimerList() {

        return ResponseEntity.ok()
                .body(ApiResponse.success(timerService.getTimerList()));
    }
}
