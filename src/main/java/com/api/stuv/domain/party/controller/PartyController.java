package com.api.stuv.domain.party.controller;

import com.api.stuv.domain.auth.util.TokenUtil;
import com.api.stuv.domain.party.dto.request.PartyCreateRequest;
import com.api.stuv.domain.party.dto.request.PartyJoinRequest;
import com.api.stuv.domain.party.dto.response.*;
import com.api.stuv.domain.party.service.PartyService;
import com.api.stuv.global.response.ApiResponse;
import com.api.stuv.global.response.PageResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/party")
@Tag(name = "Party", description = "팟 API")
public class PartyController {

    private final PartyService partyService;
    private final TokenUtil tokenUtil;

    @GetMapping
    @Operation(summary = "시작 예정인 팟 목록 조회 및 검색 API", description = "팟시작 예정인 팟 목록을 조회 및 검색 합니다.")
    public ResponseEntity<ApiResponse<PageResponse<PartyGroupResponse>>> getPendingPartyListWithSearch(
            @RequestParam(required = false) String name,
            @RequestParam(required = false, defaultValue = "0") int page,
            @RequestParam(required = false, defaultValue = "10") int size
    ) {
        return ResponseEntity.ok()
                .body(ApiResponse.success(
                        partyService.getPendingPartyGroups(name, PageRequest.of(page, size))
                ));
    }

    @GetMapping("/active")
    @Operation(summary = "현재 사용자가 진행중인 팟 목록 조회 API", description = "현재 사용자가 진행중인 팟 목록을 조회합니다.")
    public ResponseEntity<ApiResponse<List<PartyGroupResponse>>> getActivePartyList() {
        return ResponseEntity.ok()
                .body(ApiResponse.success(
                        partyService.getActivePartyGroupsByUserId(tokenUtil.getUserId())
                ));
    }

    @GetMapping("/complete")
    @Operation(summary = "현재 사용자의 마감된 팟 보상 획득 여부 목록 조회 API", description = "마감된 팟에 대한 보상 획득 여부를 조회합니다.")
    public ResponseEntity<ApiResponse<List<PartyRewardStatusResponse>>> getCompletePartyList() {
        return ResponseEntity.ok()
                .body(ApiResponse.success(
                        partyService.getCompletePartyStatus(tokenUtil.getUserId())
                ));
    }

    @GetMapping("/{partyId}")
    @Operation(summary = "팟 상세 조회 API", description = "팟의 상세한 내용과 현재 회원의 참가 여부를 조회합니다.")
    public ResponseEntity<ApiResponse<PartyDetailResponse>> getPartyDetail(@PathVariable Long partyId) {
        return ResponseEntity.ok()
                .body(ApiResponse.success(
                        partyService.getPartyDetailInfo(partyId, tokenUtil.getUserId())
                ));
    }

    @PostMapping
    @Operation(summary = "팟 생성 API", description = "팟을 생성하면서 방장이 팟에 참가합니다.")
    public ResponseEntity<ApiResponse<PartyIdResponse>> createParty(@RequestBody PartyCreateRequest request) {
        return ResponseEntity.ok()
                .body(ApiResponse.success(
                        partyService.createParty(request, tokenUtil.getUserId())
                ));
    }

    @PostMapping("/{partyId}")
    @Operation(summary = "팟 참가 API", description = "회원이 팟에 참가합니다.")
    public ResponseEntity<ApiResponse<Void>> joinParty(@PathVariable Long partyId, @RequestBody PartyJoinRequest request) {
        partyService.joinParty(request.bettingPoint(), partyId, tokenUtil.getUserId());
        return ResponseEntity.ok()
                .body(ApiResponse.success());
    }

    @GetMapping("/event")
    @Operation(summary = "이벤트 배너 조회 API", description = "이벤트 팟의 배너를 조회할 수 있습니다.")
    public ResponseEntity<ApiResponse<List<EventBannerResponse>>> getEventBanner() {
        return ResponseEntity.ok()
                .body(ApiResponse.success(
                        partyService.getEventList()
                ));
    }
}



