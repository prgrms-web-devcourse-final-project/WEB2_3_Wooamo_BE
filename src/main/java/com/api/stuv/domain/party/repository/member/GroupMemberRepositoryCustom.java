package com.api.stuv.domain.party.repository.member;

import com.api.stuv.domain.admin.dto.MemberDetailDTO;
import com.api.stuv.domain.party.entity.QuestStatus;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public interface GroupMemberRepositoryCustom {
    List<MemberDetailDTO> findMemberListWithConfirmedByDate(Long partyId, LocalDate date);
    void updateQuestStatusForMember(Long partyId, Long memberId, QuestStatus questStatus);
    boolean isMemberNotProgressByGroupId(Long partyId);
    BigDecimal sumFailedGroupMemberBettingPoint(Long partyId);
    Long countSuccessGroupMembers(Long partyId);
    Long countAllGroupMembers(Long partyId);
}
