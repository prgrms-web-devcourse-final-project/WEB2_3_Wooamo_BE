package com.api.stuv.domain.party.service;

import com.api.stuv.domain.party.dto.response.PartyGroupResponse;
import com.api.stuv.domain.party.repository.party.PartyGroupRepository;
import com.api.stuv.global.response.PageResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PartyService {

    private final PartyGroupRepository partyRepository;

    public PageResponse<PartyGroupResponse> getPendingPartyGroups(String name, Pageable pageable) {
        return partyRepository.findPendingGroupsByName(name, pageable);
    }

    public List<PartyGroupResponse> getActivePartyGroupsByUserId(Long userId) {
        return partyRepository.findActivePartyGroupsByUserId(userId);
    }
}
