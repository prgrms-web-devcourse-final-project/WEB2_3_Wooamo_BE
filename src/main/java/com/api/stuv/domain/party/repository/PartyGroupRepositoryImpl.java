package com.api.stuv.domain.party.repository;

import com.api.stuv.domain.party.dto.response.AdminPartyGroupResponse;
import com.api.stuv.domain.party.dto.response.PartyGroupResponse;
import com.api.stuv.domain.party.entity.PartyStatus;
import com.api.stuv.domain.party.entity.QGroupMember;
import com.api.stuv.domain.party.entity.QPartyGroup;
import com.api.stuv.global.response.PageResponse;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.CaseBuilder;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.List;

@RequiredArgsConstructor
public class PartyGroupRepositoryImpl implements PartyGroupRepositoryCustom {

    private static final Logger log = LoggerFactory.getLogger(PartyGroupRepositoryImpl.class);
    private final JPAQueryFactory factory;
    private final QPartyGroup pg = QPartyGroup.partyGroup;
    private final QGroupMember gm = QGroupMember.groupMember;

    @Override
    public PageResponse<PartyGroupResponse> findPendingGroupsByName(String name, Pageable pageable) {
        JPAQuery<PartyGroupResponse> query = factory
                .select(Projections.constructor(PartyGroupResponse.class,
                        pg.id,
                        pg.name,
                        pg.usersCount,
                        gm.id.count(),
                        pg.startDate,
                        Expressions.nullExpression(LocalDate.class)))
                .from(pg)
                .join(gm).on(pg.id.eq(gm.groupId))
                .where(pg.startDate.gt(LocalDate.now())
                        .and(name != null ? pg.name.contains(name) : null))
                .orderBy(pg.startDate.desc())
                .groupBy(pg.id, pg.name, pg.usersCount, pg.startDate);

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
                        pg.usersCount,
                        gm.id.count(),
                        Expressions.nullExpression(LocalDate.class),
                        pg.endDate))
                .from(pg)
                .join(gm).on(pg.id.eq(gm.groupId))
                .where(pg.startDate.loe(LocalDate.now())
                        .and(pg.endDate.goe(LocalDate.now()))
                        .and(pg.id.eq(userId)))
                .orderBy(pg.endDate.desc())
                .groupBy(pg.id, pg.name, pg.usersCount, pg.endDate)
                .fetch();
    }

    @Override
    public PageResponse<AdminPartyGroupResponse> findAllPartyGroupsWithApproved(Pageable pageable) {
        JPAQuery<AdminPartyGroupResponse> query = factory
                .select(Projections.constructor(AdminPartyGroupResponse.class,
                        pg.id,
                        pg.name,
                        pg.usersCount,
                        gm.id.count(),
                        pg.startDate,
                        pg.endDate,
                        new CaseBuilder()
                                .when(pg.status.eq(PartyStatus.APPROVED)).then(PartyStatus.APPROVED.getText())
                                .otherwise(PartyStatus.PENDING.getText())))
                .from(pg)
                .join(gm).on(pg.id.eq(gm.groupId))
                .orderBy(pg.createdAt.desc())
                .groupBy(pg.id, pg.name, pg.usersCount, pg.startDate, pg.endDate, pg.status);

        return PageResponse.applyPage(query, pageable, countAllPartyGroupsWithApproved());
    }

    private Long countAllPartyGroupsWithApproved() {
        return factory.select(pg.countDistinct())
                .from(pg)
                .join(gm).on(pg.id.eq(gm.groupId))
                .fetchOne();
    }
}
