package com.api.stuv.domain.admin.controller;

import com.api.stuv.domain.admin.dto.CostumeRequest;
import com.api.stuv.domain.admin.service.AdminService;
import com.api.stuv.domain.party.dto.response.AdminPartyGroupResponse;
import com.api.stuv.global.response.ApiResponse;
import com.api.stuv.global.response.PageResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin")
@Tag(name = "Admin", description = "관리자 API")
public class AdminController {

    private final AdminService adminService;

    @PostMapping(value = "/costume", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "코스튬 등록 API", description = "신규 코스튬을 등록합니다.")
    public ResponseEntity<ApiResponse<Void>> createCostume(
            @RequestPart(value = "contents") @Valid CostumeRequest request,
            @RequestPart(value = "image") MultipartFile file
    ){
        adminService.createCostume(request, file);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success());
    }

    @PutMapping(value = "/costume/{costumeId}")
    @Operation(summary = "코스튬 수정 API", description = "기존 코스튬의 정보를 수정합니다.")
    public ResponseEntity<ApiResponse<Void>> updateCostume(
            @PathVariable("costumeId") long costumeId,
            @RequestBody @Valid CostumeRequest request
    ) {
        adminService.modifyCostume(costumeId, request);
        return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.success());
    }

    @DeleteMapping(value = "/costume/{costumeId}")
    @Operation(summary = "코스튬 삭제 API", description = "기존 코스튬을 삭제합니다.")
    public ResponseEntity<ApiResponse<Void>> deleteCostume(
            @PathVariable("costumeId") long costumeId
    ){
        adminService.deleteCostume(costumeId);
        return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.success());
    }

    @GetMapping(value = "/party")
    @Operation(summary = "승인 여부가 포함된 팟 전체 목록 조회 API", description = "팟 목록을 승인 여부와 같이 조회합니다.")
    public ResponseEntity<ApiResponse<PageResponse<AdminPartyGroupResponse>>> getPartyListWithApprovedStatus (
            @RequestParam(required = false, defaultValue = "0") int page,
            @RequestParam(required = false, defaultValue = "10") int size
    ) {
        return ResponseEntity.ok().body(ApiResponse.success(adminService.getAllPartyGroupsWithApprovedStatus(PageRequest.of(page, size))));
    }
}
