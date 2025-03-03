package com.api.stuv.domain.party.repository.member;

import com.api.stuv.domain.admin.dto.MemberDetailDTO;
import com.api.stuv.domain.image.entity.EntityType;
import com.api.stuv.domain.image.entity.QImageFile;
import com.api.stuv.domain.image.service.S3ImageService;
import com.api.stuv.domain.party.entity.QGroupMember;
import com.api.stuv.domain.party.entity.QQuestConfirm;
import com.api.stuv.domain.party.entity.QuestStatus;
import com.api.stuv.domain.user.entity.QUser;
import com.api.stuv.domain.user.entity.QUserCostume;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
public class GroupMemberRepositoryImpl implements GroupMemberRepositoryCustom {

    private final JPAQueryFactory factory;
    private final S3ImageService s3ImageService;
    private final QGroupMember gm = QGroupMember.groupMember;
    private final QQuestConfirm qc = QQuestConfirm.questConfirm;
    private final QUser u = QUser.user;
    private final QUserCostume uc = QUserCostume.userCostume;
    private final QImageFile i = QImageFile.imageFile;

    @Override
    public List<MemberDetailDTO> findMemberListWithConfirmedByDate(Long partyId, LocalDate date) {
        return factory.select(
                        gm.id,
                        u.nickname,
                        qc.confirmStatus,
                        i.newFilename,
                        uc.costumeId
                )
                .from(gm)
                .leftJoin(u).on(gm.userId.eq(u.id))
                .leftJoin(uc).on(u.costumeId.eq(uc.id))
                .leftJoin(i).on(uc.costumeId.eq(i.entityId)
                        .and(i.entityType.eq(EntityType.COSTUME)))
                .leftJoin(qc).on(gm.id.eq(qc.memberId))
                .where(gm.groupId.eq(partyId)
                        .and(qc.isNotNull().and(qc.confirmDate.eq(date))))
                .fetch()
                .stream()
                .map(tp -> new MemberDetailDTO(
                        tp.get(gm.id),
                        s3ImageService.generateImageFile(EntityType.COSTUME, tp.get(uc.costumeId), tp.get(i.newFilename)),
                        tp.get(u.nickname),
                        tp.get(qc.confirmStatus.stringValue())
                )).toList();
    }

    @Override
    public void updateQuestStatusForMember(Long partyId, Long memberId, QuestStatus questStatus) {
        factory.update(gm)
                .set(gm.questStatus, questStatus)
                .where(gm.groupId.eq(partyId)
                        .and(gm.id.eq(memberId)))
                .execute();
    }

    @Override
    public boolean isMemberNotProgressByGroupId(Long partyId) {
        return Optional.ofNullable(
                factory.select(gm.count())
                        .from(gm)
                        .where(gm.groupId.eq(partyId)
                                .and(gm.questStatus.eq(QuestStatus.PROGRESS)))
                        .fetchOne()
        ).orElse(0L) == 0;
    }

    @Override
    public Long countAllGroupMembers(Long partyId) {
        return factory
                .select(gm.id.count())
                .from(gm)
                .where(gm.groupId.eq(partyId))
                .fetchOne();
    }

    @Override
    public BigDecimal sumFailedGroupMemberBettingPoint(Long partyId) {
        return factory
                .select(gm.bettingPoint.sum().coalesce(BigDecimal.ZERO))
                .from(gm)
                .where(gm.groupId.eq(partyId)
                        .and(gm.questStatus.eq(QuestStatus.FAILED)))
                .fetchOne();
    }

    @Override
    public Long countSuccessGroupMembers(Long partyId) {
        return factory
                .select(gm.id.count().coalesce(0L))
                .from(gm)
                .where(gm.groupId.eq(partyId)
                        .and(gm.questStatus.in(QuestStatus.SUCCESS, QuestStatus.COMPLETED)))
                .fetchOne();
    }
}
