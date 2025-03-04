package com.api.stuv.domain.admin.dto.response;

import java.time.LocalDate;
import java.util.List;

public record AdminPartyAuthDetailResponse(
        String name,
        String context,
        LocalDate startDate,
        LocalDate endDate,
        List<MemberDetailResponse> members
) {
    public static AdminPartyAuthDetailResponse from(AdminPartyAuthDetailResponse response, List<MemberDetailResponse> members) {
        return new AdminPartyAuthDetailResponse(
                response.name,
                response.context,
                response.startDate,
                response.endDate,
                members
        );
    }
}
