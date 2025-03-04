package com.api.stuv.domain.party.repository.member;

import com.api.stuv.domain.admin.dto.MemberDetailDTO;
import com.api.stuv.domain.admin.dto.response.MemberDetailResponse;
import com.api.stuv.domain.friend.entity.FriendStatus;
import com.api.stuv.domain.friend.entity.QFriend;
import com.api.stuv.domain.image.entity.EntityType;
import com.api.stuv.domain.image.entity.QImageFile;
import com.api.stuv.domain.image.service.S3ImageService;
import com.api.stuv.domain.party.dto.MemberDTO;
import com.api.stuv.domain.party.entity.QGroupMember;
import com.api.stuv.domain.party.entity.QQuestConfirm;
import com.api.stuv.domain.party.entity.QuestStatus;
import com.api.stuv.domain.user.entity.QUser;
import com.api.stuv.domain.user.entity.QUserCostume;
import com.api.stuv.domain.user.entity.UserStatus;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
public class GroupMemberRepositoryImpl implements GroupMemberRepositoryCustom {

    private final JPAQueryFactory factory;
    private final QGroupMember gm = QGroupMember.groupMember;
    private final QQuestConfirm qc = QQuestConfirm.questConfirm;
    private final QUser u = QUser.user;
    private final QUserCostume uc = QUserCostume.userCostume;
    private final QImageFile i = QImageFile.imageFile;
    private final QFriend f = QFriend.friend;

    @Override
    public List<MemberDetailDTO> findMemberListWithConfirmedByDate(Long partyId, LocalDate date) {
        return factory.select(Projections.constructor(
                        MemberDetailDTO.class,
                        gm.id,
                        u.nickname,
                        qc.confirmStatus,
                        i.newFilename,
                        uc.costumeId
                ))
                .from(gm)
                .leftJoin(u).on(gm.userId.eq(u.id))
                .leftJoin(uc).on(u.costumeId.eq(uc.id))
                .leftJoin(i).on(uc.costumeId.eq(i.entityId)
                        .and(i.entityType.eq(EntityType.COSTUME)))
                .leftJoin(qc).on(gm.id.eq(qc.memberId))
                .where(gm.groupId.eq(partyId)
                        .and(qc.confirmDate.eq(date)))
                .fetch();
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

    @Override
    public List<MemberDTO> findMemberList(Long partyId, Long userId, Pageable pageable) {
        return factory.select(Projections.constructor(
                        MemberDTO.class,
                        f.id,
                        gm.userId,
                        u.nickname,
                        u.context,
                        i.entityId,
                        i.newFilename,
                        Expressions.cases()
                                .when(f.userId.eq(userId).and(f.status.eq(FriendStatus.PENDING))).then(UserStatus.SENDER.toString())
                                .when(f.friendId.eq(userId).and(f.status.eq(FriendStatus.PENDING))).then(UserStatus.RECEIVER.toString())
                                .when(f.status.eq(FriendStatus.ACCEPTED).and(f.userId.eq(userId).or(f.friendId.eq(userId)))).then(UserStatus.FRIEND.toString())
                                .otherwise(UserStatus.NOT_FRIEND.toString())
                ))
                .from(gm)
                .leftJoin(u).on(gm.userId.eq(u.id))
                .leftJoin(uc).on(u.costumeId.eq(uc.id))
                .leftJoin(i).on(uc.costumeId.eq(i.entityId).and(i.entityType.eq(EntityType.COSTUME)))
                .leftJoin(f).on((f.userId.eq(userId).and(f.friendId.eq(u.id))).or(f.friendId.eq(userId).and(f.userId.eq(u.id))))
                .where(gm.groupId.eq(partyId))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();
    }

    @Override
    public Optional<Long> findIdByGroupIdAndUserId(Long groupId, Long userId) {
        return Optional.ofNullable(
                factory
                        .select(gm.id)
                        .from(gm)
                        .where(gm.groupId.eq(groupId).and(gm.userId.eq(userId)))
                        .fetchOne()
        );
    }
}
