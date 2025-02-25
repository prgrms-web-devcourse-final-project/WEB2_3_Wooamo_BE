package com.api.stuv.domain.admin.dto.response;

import com.api.stuv.domain.admin.dto.MemberDetailDTO;

import java.time.LocalDate;
import java.util.List;

public record AdminPartyAuthDetailResponse(
        String name,
        String context,
        LocalDate startDate,
        LocalDate endDate,
        List<MemberDetailDTO> members
) {
    public static AdminPartyAuthDetailResponse from(AdminPartyAuthDetailResponse response, List<MemberDetailDTO> members) {
        return new AdminPartyAuthDetailResponse(
                response.name,
                response.context,
                response.startDate,
                response.endDate,
                members
        );
    }
}
