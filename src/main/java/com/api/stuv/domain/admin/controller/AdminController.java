package com.api.stuv.domain.admin.controller;

import com.api.stuv.domain.admin.dto.CostumeRequest;
import com.api.stuv.domain.admin.service.AdminService;
import com.api.stuv.global.response.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminController {

    private final AdminService adminService;

    @PostMapping(value = "/costume", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<Void>> createCostume(
            @RequestPart(value = "contents") @Valid CostumeRequest request,
            @RequestPart(value = "image", required = false) MultipartFile file
    ){
        adminService.createCostume(request, file);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success());
    }

    @PutMapping(value = "/costume/{costumeId}")
    public ResponseEntity<ApiResponse<Void>> updateCostume(
            @PathVariable("costumeId") long costumeId,
            @RequestBody @Valid CostumeRequest request
    ) {
        adminService.modifyCostume(costumeId, request);
        return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.success());
    }

    @DeleteMapping(value = "/costume/{costumeId}")
    public ResponseEntity<ApiResponse<Void>> deleteCostume(
            @PathVariable("costumeId") long costumeId
    ){
        adminService.deleteCostume(costumeId);
        return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.success());
    }
}
