package com.api.stuv.domain.alert.controller;

import com.api.stuv.domain.alert.dto.AlertResponse;
import com.api.stuv.domain.alert.service.AlertService;
import com.api.stuv.domain.auth.util.TokenUtil;
import com.api.stuv.global.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/alert")
@RequiredArgsConstructor
@Tag(name = "Alert", description = "알림 관련 API")
public class AlertController {
    private final AlertService alertService;
    private final TokenUtil tokenUtil;

    @Operation(summary = "알림 목록 조회 API", description = "알림 목록을 조회 합니다.")
    @GetMapping("")
    public ResponseEntity<ApiResponse<List<AlertResponse>>> listAlertAll() {
        return ResponseEntity.ok()
                .body(ApiResponse.success(alertService.getAlertList(tokenUtil.getUserId())));
    }

    @Operation(summary = "알림 전체 읽음 처리 API", description = "알림을 전체 읽음 처리 합니다.")
    @PatchMapping("")
    public ResponseEntity<ApiResponse<Void>> readAllAlert() {
        alertService.readAllAlert(tokenUtil.getUserId());
        return ResponseEntity.ok().body(ApiResponse.success());
    }

    @Operation(summary = "특정 알림 읽음 처리 API", description = "특정 알림을 읽음 처리 합니다.")
    @PatchMapping("/{alertId}")
    public ResponseEntity<ApiResponse<Void>> readAlert(@PathVariable String alertId) {
        alertService.readAlert(tokenUtil.getUserId(), alertId);
        return ResponseEntity.ok().body(ApiResponse.success());
    }
}
