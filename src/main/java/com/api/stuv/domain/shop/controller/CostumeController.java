package com.api.stuv.domain.shop.controller;

import com.api.stuv.domain.shop.dto.CostumeResponse;
import com.api.stuv.domain.shop.service.CostumeService;
import com.api.stuv.global.response.ApiResponse;
import com.api.stuv.global.response.PageResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class CostumeController {

    private final CostumeService costumeService;

    @GetMapping(value = "/costume")
    public ResponseEntity<ApiResponse<PageResponse<CostumeResponse>>> listCostumes (
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        return ResponseEntity.ok().body(ApiResponse.success(
                costumeService.getCostumeList(PageRequest.of(page, size))));
    }

    @GetMapping(value = "/costume/{costumeId}")
    public ResponseEntity<ApiResponse<CostumeResponse>> getCostume(
            @PathVariable long costumeId
    ) {
        return ResponseEntity.ok().body(ApiResponse.success(
                costumeService.getCostume(costumeId)));
    }
}
