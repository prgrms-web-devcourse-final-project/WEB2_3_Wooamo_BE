package com.api.stuv.domain.friend.repository;

import com.api.stuv.domain.friend.dto.dto.FriendListDTO;
import com.api.stuv.domain.friend.dto.dto.FriendRecommendDTO;
import com.api.stuv.domain.friend.entity.FriendFollowStatus;
import com.api.stuv.domain.friend.entity.FriendStatus;
import static com.api.stuv.domain.friend.entity.QFriend.friend;
import com.api.stuv.domain.image.entity.EntityType;
import com.api.stuv.domain.image.entity.QImageFile;
import com.api.stuv.domain.user.entity.QUser;
import com.api.stuv.domain.user.entity.QUserCostume;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.JPQLQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;

import java.util.List;

@RequiredArgsConstructor
public class FriendRepositoryImpl implements FriendRepositoryCustom {
    private final JPAQueryFactory jpaQueryFactory;
    private final QUser u = QUser.user;
    private final QUserCostume uc = QUserCostume.userCostume;
    private final QImageFile i = QImageFile.imageFile;

    // 내게 Follow 요청한 친구 목록 조회
    @Override
    public List<FriendListDTO> getFriendFollowList(Long receiverId, Pageable pageable) {
        return jpaQueryFactory
                .select(Projections.constructor(FriendListDTO.class,
                        friend.id,
                        u.id,
                        u.nickname,
                        u.context,
                        uc.costumeId,
                        i.newFilename,
                        friend.status.stringValue()))
                .from(friend).join(u).on(friend.userId.eq(u.id))
                .leftJoin(uc).on(u.costumeId.eq(uc.id))
                .leftJoin(i).on(uc.costumeId.eq(i.entityId).and(i.entityType.eq(EntityType.COSTUME)))
                .where(friend.friendId.eq(receiverId).and(friend.status.eq(FriendStatus.PENDING)))
                .offset(pageable.getOffset()).limit(pageable.getPageSize()).fetch();
    }

    @Override
    public Long getTotalFriendFollowListPage(Long receiverId) {
        return jpaQueryFactory
                .select(friend.count())
                .from(friend)
                .where(friend.friendId.eq(receiverId).and(friend.status.eq(FriendStatus.PENDING)))
                .fetchOne();
    }

    // 친구 목록 조회
    @Override
    public List<FriendListDTO> getFriendList(Long userId, Pageable pageable) {
        return jpaQueryFactory
                .select(Projections.constructor(FriendListDTO.class,
                        friend.id,
                        u.id,
                        u.nickname,
                        u.context,
                        uc.costumeId,
                        i.newFilename,
                        friend.status.stringValue()))
                .from(friend).join(u).on(friend.userId.eq(userId).and(friend.friendId.eq(u.id)).or(friend.friendId.eq(userId).and(friend.userId.eq(u.id))))
                .leftJoin(uc).on(u.costumeId.eq(uc.id))
                .leftJoin(i).on(uc.costumeId.eq(i.entityId).and(i.entityType.eq(EntityType.COSTUME)))
                .where(friend.status.eq(FriendStatus.ACCEPTED))
                .offset(pageable.getOffset()).limit(pageable.getPageSize()).fetch();
    }

    @Override
    public Long getTotalFriendListPage(Long userId) {
        return jpaQueryFactory
                .select(friend.count())
                .from(friend)
                .where((friend.userId.eq(userId).or(friend.friendId.eq(userId))).and(friend.status.eq(FriendStatus.ACCEPTED)))
                .fetchOne();
    }

    // 유저 검색 ( 친구 목록에 없는 유저 + 현재 PENDING 상태인 유저 )
    @Override
    public List<FriendListDTO> searchUser(Long userId, String target, Pageable pageable) {
        JPQLQuery<Long> subQuery = JPAExpressions
                .select(friend.userId.when(userId).then(friend.friendId).otherwise(friend.userId))
                .from(friend).where(friend.userId.eq(userId).or(friend.friendId.eq(userId)).and(friend.status.eq(FriendStatus.ACCEPTED)));

        return jpaQueryFactory
                .select(Projections.constructor(FriendListDTO.class,
                        friend.id,
                        u.id,
                        u.nickname,
                        u.context,
                        uc.costumeId,
                        i.newFilename,
                        Expressions.cases()
                                .when(friend.userId.eq(userId).and(friend.status.eq(FriendStatus.PENDING))).then(FriendFollowStatus.ME.toString())
                                .when(friend.friendId.eq(userId).and(friend.status.eq(FriendStatus.PENDING))).then(FriendFollowStatus.OTHER.toString())
                                .otherwise(FriendFollowStatus.NONE.toString())))
                .from(u)
                .leftJoin(friend).on(friend.userId.eq(userId).and(friend.friendId.eq(u.id)).or(friend.friendId.eq(userId).and(friend.userId.eq(u.id))))
                .leftJoin(uc).on(u.costumeId.eq(uc.id))
                .leftJoin(i).on(uc.costumeId.eq(i.entityId))
                .where(u.id.ne(userId)
                        .and(u.nickname.contains(target).or(u.context.contains(target))))
                .where(u.id.notIn(subQuery))
                .offset(pageable.getOffset()).limit(pageable.getPageSize()).fetch();
    }

    @Override
    public Long getTotalSearchUserPage(Long userId, String target) {
        JPQLQuery<Long> subQuery = JPAExpressions
                .select(friend.userId.when(userId).then(friend.friendId).otherwise(friend.userId))
                .from(friend).where(friend.userId.eq(userId).or(friend.friendId.eq(userId)).and(friend.status.eq(FriendStatus.ACCEPTED)));

        return jpaQueryFactory.select(u.count()).from(u)
                .where(u.id.ne(userId).and(u.nickname.contains(target).or(u.context.contains(target))))
                .where(u.id.notIn(subQuery))
                .fetchOne();
    }

    // 친구 추천 ( 내 친구 목록에 없는 친구 )
    @Override
    public List<FriendRecommendDTO> recommendFriend(Long userId) {
        JPQLQuery<Long> subQuery = JPAExpressions
                .select(u.id)
                .from(friend).join(u).on(friend.userId.eq(u.id).and(friend.friendId.eq(userId)).or(friend.friendId.eq(u.id).and(friend.userId.eq(userId))));

        return jpaQueryFactory
                .select(Projections.constructor(FriendRecommendDTO.class,
                        u.id,
                        u.nickname,
                        u.context,
                        uc.costumeId,
                        i.newFilename))
                .from(u).leftJoin(uc).on(u.costumeId.eq(uc.id))
                .leftJoin(i).on(uc.costumeId.eq(i.entityId).and(i.entityType.eq(EntityType.COSTUME)))
                .where(u.id.ne(userId).and(u.id.notIn(subQuery)))
                .orderBy(Expressions.numberTemplate(Double.class, "RAND()").asc())
                .limit(3).fetch();
    }
}
