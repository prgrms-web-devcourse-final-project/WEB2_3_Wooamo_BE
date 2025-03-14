package com.api.stuv.domain.party.repository.confirm;

import com.api.stuv.domain.image.entity.EntityType;
import com.api.stuv.domain.image.entity.QImageFile;
import com.api.stuv.domain.party.entity.ConfirmStatus;
import com.api.stuv.domain.party.entity.QGroupMember;
import com.api.stuv.domain.party.entity.QQuestConfirm;
import com.querydsl.core.Tuple;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Optional;

@RequiredArgsConstructor
public class QuestConfirmRepositoryImpl implements QuestConfirmRepositoryCustom {

    private final JPAQueryFactory factory;
    private final QQuestConfirm qc = QQuestConfirm.questConfirm;
    private final QGroupMember gm = QGroupMember.groupMember;
    private final QImageFile i = QImageFile.imageFile;

    @Override
    public Tuple findGroupMemberConfirmImageByDate(Long partyId, Long memberId, LocalDate date) {
        return factory.select(qc.id, i.newFilename)
                .from(qc)
                .join(gm).on(gm.id.eq(qc.memberId))
                .join(i).on(qc.id.eq(i.entityId)
                        .and(i.entityType.eq(EntityType.CONFIRM)))
                .where(qc.memberId.eq(memberId)
                        .and(gm.groupId.eq(partyId))
                        .and(qc.confirmDate.eq(date)))
                .fetchFirst();
    }

    @Override
    public void updateConfirmStatusByDate(Long memberId, ConfirmStatus status, LocalDate date) {
        factory.update(qc)
                .set(qc.confirmStatus, status)
                .where(qc.memberId.eq(memberId)
                        .and(qc.confirmDate.eq(date)))
                .execute();
    }

    @Override
    public boolean isSuccessStatusDuringPeriod(Long memberId, LocalDate startDate, LocalDate endDate) {
        return Optional.ofNullable(
                factory.select(qc.confirmDate.count()) // 성공한 인증 개수 조회
                        .from(qc)
                        .where(qc.memberId.eq(memberId)
                                .and(qc.confirmDate.between(startDate, endDate))
                                .and(qc.confirmStatus.eq(ConfirmStatus.SUCCESS))) // 성공한 경우만 카운트
                        .fetchOne()
        ).orElse(0L).equals(ChronoUnit.DAYS.between(startDate, endDate) + 1);
    }
}
