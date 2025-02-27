package com.api.stuv.domain.timer.controller;

import com.api.stuv.domain.timer.dto.request.AddTimerCatetoryRequest;
import com.api.stuv.domain.timer.dto.request.StudyTimeRequest;
import com.api.stuv.domain.timer.dto.response.AddTimerCatetoryResponse;
import com.api.stuv.domain.timer.dto.response.TimerListResponse;
import com.api.stuv.domain.timer.service.TimerService;
import com.api.stuv.global.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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

    @Transactional
    @Operation(summary = "타이머 카테고리 추가 API", description = "타이머를 저장할 카테고리를 추가합니다")
    @PostMapping("/category")
    public ResponseEntity<ApiResponse<AddTimerCatetoryResponse>> addTimerCategory(@RequestBody @Valid AddTimerCatetoryRequest addTimerCatetoryRequest) {

        return ResponseEntity.ok()
                .body(ApiResponse.success(timerService.addTimerCategory(addTimerCatetoryRequest)));
    }

    @Operation(summary = "타이머 공부시간 저장 API", description = "타이머의 공부시간을 저장합니다.")
    @PostMapping("/{categoryId}")
    public ResponseEntity<ApiResponse<Void>> recordStudyTime(
            @PathVariable Long categoryId,
            @RequestBody StudyTimeRequest request
    ) {
        timerService.recordStudyTime(categoryId, request.convertTimeToSeconds());
        return ResponseEntity.ok().body(ApiResponse.success());
    }
}
