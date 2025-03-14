package com.api.stuv.domain.admin.controller;

import com.api.stuv.domain.admin.dto.request.ConfirmRequest;
import com.api.stuv.domain.admin.dto.request.CostumeRequest;
import com.api.stuv.domain.admin.dto.request.EventPartyRequest;
import com.api.stuv.domain.admin.dto.response.*;
import com.api.stuv.domain.image.dto.ImageResponse;
import com.api.stuv.domain.admin.service.AdminService;
import com.api.stuv.global.exception.ValidationException;
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

import java.time.LocalDate;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin")
@Tag(name = "Admin", description = "관리자 API")
public class AdminController {

    private final AdminService adminService;

    @GetMapping
    @Operation(summary = "주간 서비스 이용 정보 조회 API", description = "주간 서비스 이용 정보를 볼 수 있습니다.")
    public ResponseEntity<ApiResponse<WeeklyInfoResponse>> getAdminPartyGroups() {
        return ResponseEntity.ok()
                .body(ApiResponse.success(
                        adminService.weeklyInfo()
                ));
    }

    @GetMapping(value = "/payment")
    @Operation(summary = "최근 매출 내역 조회 API", description = "최근 5개 까지의 매출 내역을 조회할 수 있습니다.")
    public ResponseEntity<ApiResponse<List<PointSalesResponse>>> getAdminPointSales() {
        return ResponseEntity.ok()
                .body(ApiResponse.success(
                        adminService.getPointSalesList()
                ));
    }

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

    @GetMapping(value = "/party/{partyId}")
    @Operation(summary = "팟 인증 상세 조회 API", description = "팟 인증 상세를 조회합니다.")
    public ResponseEntity<ApiResponse<AdminPartyAuthDetailResponse>> getPartyAuthDetailWithMembers (
            @PathVariable Long partyId,
            @RequestParam(required = false) LocalDate date
    ) {
        return ResponseEntity.ok().body(ApiResponse.success(adminService.getPartyAuthDetailWithMembers(partyId, date)));
    }

    @GetMapping(value = "/party/{partyId}/{memberId}")
    @Operation(summary = "날짜별 회원 팟 인증 사진 조회 API", description = "날짜별 회원 팟 인증 사진을 조회합니다.")
    public ResponseEntity<ApiResponse<ImageResponse>> getGroupMemberConfirmImageByDate (
            @PathVariable Long partyId,
            @PathVariable Long memberId,
            @RequestParam LocalDate date
    ) {
        return ResponseEntity.ok().body(ApiResponse.success(adminService.getGroupMemberConfirmImageByDate(partyId, memberId, date)));
    }

    @PatchMapping(value = "/party/{partyId}/{memberId}")
    @Operation(summary = "팟 날짜별 인증 이미지에 대한 승인 여부 처리 API", description = "팟 날짜별 인증 이미지에 대한 승인 여부를 처리합니다.")
    public ResponseEntity<ApiResponse<Void>> changeGroupMemberConfirmedStatusByDate(
            @PathVariable Long partyId,
            @PathVariable Long memberId,
            @RequestBody ConfirmRequest request
    ) {
        if (request.date() == null || request.auth() == null) throw new ValidationException();
        adminService.changeGroupMemberConfirmedStatusByDate(partyId, memberId, request);

        return ResponseEntity.ok().body(ApiResponse.success());
    }

    @GetMapping(value = "/event")
    @Operation(summary = "관리자 이벤트 배너 조회 API", description = "이벤트 배너의 목록을 조회할 수 있습니다.")
    public ResponseEntity<ApiResponse<PageResponse<EventPartyResponse>>> eventListWithBanner (
            @RequestParam(required = false, defaultValue = "0") int page,
            @RequestParam(required = false, defaultValue = "10") int size
    ) {
        return ResponseEntity.ok()
                .body(ApiResponse.success(
                        adminService.getEventList(PageRequest.of(page, size))
                ));
    }

    @PostMapping(value = "/event", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "관리자 이벤트 팟 등록 API", description = "이벤트 배너와 팟을 등록할 수 있습니다.")
    public ResponseEntity<ApiResponse<Void>> createEventListWithBanner (
            @RequestPart EventPartyRequest contents,
            @RequestPart MultipartFile image
            ) {
        adminService.createEventParty(contents, image);
        return ResponseEntity.ok()
                .body(ApiResponse.success());
    }


}
