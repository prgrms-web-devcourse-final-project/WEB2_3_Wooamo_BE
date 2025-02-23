package com.api.stuv.domain.party.repository;

import com.api.stuv.domain.party.dto.response.PartyGroupResponse;
import com.api.stuv.global.response.PageResponse;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface PartyGroupRepositoryCustom {
    PageResponse<PartyGroupResponse> findPendingGroupsByName(String name, Pageable pageable);
    List<PartyGroupResponse> findActivePartyGroupsByUserId(Long userId);
}