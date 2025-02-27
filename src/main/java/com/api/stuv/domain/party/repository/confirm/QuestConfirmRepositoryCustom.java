package com.api.stuv.domain.party.repository.confirm;

import com.api.stuv.domain.party.entity.ConfirmStatus;
import com.querydsl.core.Tuple;

import java.time.LocalDate;

public interface QuestConfirmRepositoryCustom {
    Tuple findGroupMemberConfirmImageByDate(Long partyId, Long memberId, LocalDate date);
    void updateConfirmStatusByDate(Long memberId, ConfirmStatus status, LocalDate date);
    boolean isSuccessStatusDuringPeriod(Long memberId, LocalDate startDate, LocalDate endDate);
}
