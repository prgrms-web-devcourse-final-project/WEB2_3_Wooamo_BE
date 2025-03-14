package com.api.stuv.domain.party.repository.party;

import com.api.stuv.domain.admin.dto.response.AdminPartyAuthDetailResponse;
import com.api.stuv.domain.admin.dto.response.AdminPartyGroupResponse;
import com.api.stuv.domain.admin.dto.response.EventPartyResponse;
import com.api.stuv.domain.image.entity.EntityType;
import com.api.stuv.domain.image.entity.QImageFile;
import com.api.stuv.domain.party.dto.MemberRewardStatusDTO;
import com.api.stuv.domain.party.dto.response.EventBannerResponse;
import com.api.stuv.domain.party.dto.response.PartyDetailResponse;
import com.api.stuv.domain.party.dto.response.PartyGroupResponse;
import com.api.stuv.domain.party.entity.*;
import com.api.stuv.global.response.PageResponse;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
public class PartyGroupRepositoryImpl implements PartyGroupRepositoryCustom {

    private final JPAQueryFactory factory;
    private final QPartyGroup pg = QPartyGroup.partyGroup;
    private final QGroupMember gm = QGroupMember.groupMember;
    private final QImageFile i = QImageFile.imageFile;
    private final QQuestConfirm qc = QQuestConfirm.questConfirm;

    @Override
    public PageResponse<PartyGroupResponse> findPendingGroupsByName(String name, Pageable pageable) {
        JPAQuery<PartyGroupResponse> query = factory
                .select(Projections.constructor(PartyGroupResponse.class,
                        pg.id,
                        pg.name,
                        pg.recruitCap,
                        gm.id.count(),
                        pg.startDate,
                        Expressions.nullExpression(LocalDate.class)))
                .from(pg)
                .join(gm).on(pg.id.eq(gm.groupId))
                .where(pg.startDate.gt(LocalDate.now())
                        .and(name != null ? pg.name.contains(name) : null))
                .orderBy(pg.startDate.desc(), pg.createdAt.desc())
                .groupBy(pg.id, pg.name, pg.recruitCap, pg.startDate);

        return PageResponse.applyPage(query, pageable, countPendingGroupsByName(name));
    }

    private Long countPendingGroupsByName(String name) {
        return factory.select(pg.countDistinct())
                .from(pg)
                .join(gm)
                .on(pg.id.eq(gm.groupId))
                .where(pg.startDate.gt(LocalDate.now())
                        .and(name != null ? pg.name.contains(name) : null))
                .fetchOne();
    }

    @Override
    public List<PartyGroupResponse> findActivePartyGroupsByUserId(Long userId) {
        return factory.select(Projections.constructor(PartyGroupResponse.class,
                        pg.id,
                        pg.name,
                        pg.recruitCap,
                        gm.id.count(),
                        Expressions.nullExpression(LocalDate.class),
                        pg.endDate))
                .from(pg)
                .leftJoin(gm).on(pg.id.eq(gm.groupId))
                .where(pg.startDate.loe(LocalDate.now())
                        .and(pg.endDate.goe(LocalDate.now()))
                        .and(pg.id.in(
                                factory.select(gm.groupId)
                                        .from(gm)
                                        .where(gm.userId.eq(userId))
                        )))
                .orderBy(pg.endDate.asc(), pg.createdAt.asc())
                .groupBy(pg.id, pg.name, pg.recruitCap, pg.endDate)
                .fetch();
    }

    @Override
    public PageResponse<AdminPartyGroupResponse> findAllPartyGroupsWithApproved(Pageable pageable) {
        JPAQuery<AdminPartyGroupResponse> query = factory
                .select(Projections.constructor(AdminPartyGroupResponse.class,
                        pg.id,
                        pg.name,
                        pg.recruitCap,
                        gm.id.count().coalesce(0L),
                        pg.startDate,
                        pg.endDate,
                        Expressions.cases()
                                .when(pg.status.eq(PartyStatus.APPROVED)).then(PartyStatus.APPROVED.getText())
                                .otherwise(PartyStatus.PENDING.getText())))
                .from(pg)
                .leftJoin(gm).on(pg.id.eq(gm.groupId))
                .orderBy(pg.startDate.asc(), pg.createdAt.asc())
                .groupBy(pg.id, pg.name, pg.recruitCap, pg.startDate, pg.endDate, pg.status);

        return PageResponse.applyPage(query, pageable, countAllPartyGroupsWithApproved());
    }

    private Long countAllPartyGroupsWithApproved() {
        return factory.select(pg.countDistinct())
                .from(pg)
                .join(gm).on(pg.id.eq(gm.groupId))
                .fetchOne();
    }

    @Override
    public AdminPartyAuthDetailResponse findPartyGroupById(Long partyId) {
        return factory.select(Projections.constructor(AdminPartyAuthDetailResponse.class,
                        pg.name,
                        pg.context,
                        pg.startDate,
                        pg.endDate,
                        Expressions.nullExpression(List.class)))
                .from(pg)
                .where(pg.id.eq(partyId))
                .fetchOne();
    }

    @Override
    public void updatePartyStatusForPartyGroup(Long partyId, PartyStatus partyStatus) {
        factory.update(pg)
                .set(pg.status, partyStatus)
                .where(pg.id.eq(partyId))
                .execute();
    }

    @Override
    public List<MemberRewardStatusDTO> findCompletePartyStatusList(Long userId) {
        return factory
                .select(Projections.constructor(
                        MemberRewardStatusDTO.class,
                        pg.id,
                        pg.name,
                        gm.bettingPoint,
                        gm.questStatus
                ))
                .from(pg)
                .join(gm).on(pg.id.eq(gm.groupId))
                .where(gm.questStatus.ne(QuestStatus.PROGRESS)
                        .and(gm.userId.eq(userId)))
                .fetch();
    }

    @Override
    public Optional<MemberRewardStatusDTO> findCompleteParty(Long partyId, Long userId) {
        return Optional.ofNullable(
                factory
                        .select(Projections.constructor(
                                MemberRewardStatusDTO.class,
                                pg.id,
                                pg.name,
                                gm.bettingPoint,
                                gm.questStatus
                        ))
                        .from(pg)
                        .join(gm).on(pg.id.eq(gm.groupId))
                        .where(gm.questStatus.ne(QuestStatus.PROGRESS)
                                .and(gm.userId.eq(userId))
                                .and(pg.id.eq(partyId)))
                        .fetchOne()
        );
    }

    @Override
    public Optional<PartyDetailResponse> findDetailByUserId(Long partyId, Long userId) {
        LocalDate today = LocalDate.now();
        return Optional.ofNullable(
                factory
                        .select(Projections.constructor(
                                PartyDetailResponse.class,
                                pg.id,
                                pg.name,
                                pg.context,
                                pg.recruitCap,
                                gm.id.count(),
                                pg.startDate,
                                pg.endDate,
                                pg.bettingPoint,
                                factory
                                        .selectOne()
                                        .from(gm)
                                        .where(gm.groupId.eq(partyId)
                                                .and(gm.userId.eq(userId)))
                                        .exists(),
                                factory
                                        .selectOne()
                                        .from(qc)
                                        .leftJoin(gm).on(qc.memberId.eq(gm.id)
                                                .and(gm.userId.eq(userId)))
                                        .where(qc.confirmDate.eq(today))
                                        .exists()
                        ))
                        .from(pg)
                        .leftJoin(gm).on(pg.id.eq(gm.groupId))
                        .where(pg.id.eq(partyId))
                        .groupBy(pg.id, pg.name, pg.recruitCap, pg.startDate, pg.endDate, pg.bettingPoint)
                        .fetchOne()
        );
    }

    @Override
    public List<EventBannerResponse> findEventBannerList() {
        LocalDate today = LocalDate.now();
        return factory
                .select(Projections.constructor(
                        EventBannerResponse.class,
                        i.newFilename,
                        pg.id
                ))
                .from(pg)
                .leftJoin(i).on(pg.id.eq(i.entityId))
                .where(i.entityType.eq(EntityType.EVENT)
                        .and(pg.isEvent.eq(true))
                        .and(pg.startDate.gt(today)))
                .fetch();
    }

    @Override
    public List<EventPartyResponse> findEventPartyList(Pageable pageable) {
        return factory
                .select(Projections.constructor(
                        EventPartyResponse.class,
                        pg.id,
                        i.newFilename,
                        pg.name,
                        pg.bettingPoint
                ))
                .from(pg)
                .leftJoin(i).on(pg.id.eq(i.entityId)
                        .and(i.entityType.eq(EntityType.EVENT)))
                .where(pg.isEvent.eq(true))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();
    }

    @Override
    public Long countEventParty() {
        return Optional.ofNullable(
                factory
                        .select(pg.id.count())
                        .from(pg)
                        .where(pg.isEvent.eq(true))
                        .fetchOne()
        ).orElse(0L);
    }

    @Override
    public Long findGroupIdByRoomName(String roomName) {
        return factory.select(pg.id)
                .from(pg)
                .where(pg.name.eq(roomName))
                .fetchOne();
    }
}
