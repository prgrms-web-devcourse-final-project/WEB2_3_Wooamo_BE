package com.api.stuv.domain.party.service;

import com.api.stuv.domain.party.dto.response.PartyGroupResponse;
import com.api.stuv.domain.party.repository.PartyGroupRepository;
import com.api.stuv.global.response.PageResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PartyService {

    private final PartyGroupRepository partyRepository;

    public PageResponse<PartyGroupResponse> getPendingPartyGroupsWithSearch(String name, Pageable pageable) {
        return partyRepository.getPendingPartyGroupsWithSearch(name, pageable);
    }
}
