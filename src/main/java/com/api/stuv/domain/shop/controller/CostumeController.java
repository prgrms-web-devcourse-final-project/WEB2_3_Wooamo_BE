package com.api.stuv.domain.shop.controller;

import com.api.stuv.domain.shop.dto.CostumeResponse;
import com.api.stuv.domain.shop.service.CostumeService;
import com.api.stuv.global.response.ApiResponse;
import com.api.stuv.global.response.PageResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

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
}
