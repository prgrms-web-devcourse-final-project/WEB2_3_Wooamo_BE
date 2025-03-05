package com.api.stuv.domain.party.repository.party;

import com.api.stuv.domain.admin.dto.response.AdminPartyAuthDetailResponse;
import com.api.stuv.domain.admin.dto.response.AdminPartyGroupResponse;
import com.api.stuv.domain.admin.dto.response.EventPartyResponse;
import com.api.stuv.domain.party.dto.MemberRewardStatusDTO;
import com.api.stuv.domain.party.dto.response.EventBannerResponse;
import com.api.stuv.domain.party.dto.response.PartyDetailResponse;
import com.api.stuv.domain.party.dto.response.PartyGroupResponse;
import com.api.stuv.domain.party.entity.PartyStatus;
import com.api.stuv.global.response.PageResponse;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface PartyGroupRepositoryCustom {
    PageResponse<PartyGroupResponse> findPendingGroupsByName(String name, Pageable pageable);
    List<PartyGroupResponse> findActivePartyGroupsByUserId(Long userId);
    PageResponse<AdminPartyGroupResponse> findAllPartyGroupsWithApproved(Pageable pageable);
    AdminPartyAuthDetailResponse findPartyGroupById(Long partyId);
    void updatePartyStatusForPartyGroup(Long partyId, PartyStatus partyStatus);
    List<MemberRewardStatusDTO> findCompletePartyStatusList(Long userId);
    Optional<PartyDetailResponse> findDetailByUserId(Long partyId, Long userId);
    List<EventBannerResponse> findEventBannerList();
    Optional<MemberRewardStatusDTO> findCompleteParty(Long partyId, Long userId);
    List<EventPartyResponse> findEventPartyList(Pageable pageable);
    Long countEventParty();
    Long findGroupIdByRoomName(String roomName);
}