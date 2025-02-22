package com.api.stuv.domain.party.repository;

import com.api.stuv.domain.party.dto.response.PartyGroupResponse;
import com.api.stuv.global.response.PageResponse;
import org.springframework.data.domain.Pageable;

public interface PartyGroupRepositoryCustom {
    PageResponse<PartyGroupResponse> getPendingPartyGroupsWithSearch(String name, Pageable pageable);
}