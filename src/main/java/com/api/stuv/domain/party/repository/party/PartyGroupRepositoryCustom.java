package com.api.stuv.domain.party.repository.party;

import com.api.stuv.domain.admin.dto.response.AdminPartyAuthDetailResponse;
import com.api.stuv.domain.admin.dto.response.AdminPartyGroupResponse;
import com.api.stuv.domain.party.dto.MemberRewardStatusDTO;
import com.api.stuv.domain.party.dto.response.PartyGroupResponse;
import com.api.stuv.domain.party.entity.PartyStatus;
import com.api.stuv.global.response.PageResponse;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.util.List;

public interface PartyGroupRepositoryCustom {
    PageResponse<PartyGroupResponse> findPendingGroupsByName(String name, Pageable pageable);
    List<PartyGroupResponse> findActivePartyGroupsByUserId(Long userId);
    PageResponse<AdminPartyGroupResponse> findAllPartyGroupsWithApproved(Pageable pageable);
    AdminPartyAuthDetailResponse findPartyGroupById(Long partyId);
    void updatePartyStatusForPartyGroup(Long partyId, PartyStatus partyStatus);
    String findPartyGroupNameByUserId(Long userId);
    List<MemberRewardStatusDTO> findCompletePartuStatusList(Long userId);
    BigDecimal sumFailedGroupMemberBettingPoint(Long partyId);
    Long countSuccessGroupMembers(Long partyId);
    Long countAllGroupMembers(Long partyId);
}