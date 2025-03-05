package com.api.stuv.domain.friend.repository;

import com.api.stuv.domain.friend.dto.dto.FriendListDTO;
import com.api.stuv.domain.friend.dto.dto.FriendRecommendDTO;
import com.api.stuv.domain.friend.entity.FriendStatus;
import com.api.stuv.domain.image.entity.EntityType;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.JPQLQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import static com.api.stuv.domain.friend.entity.QFriend.friend;
import static com.api.stuv.domain.user.entity.QUser.user;
import static com.api.stuv.domain.user.entity.QUserCostume.userCostume;
import static com.api.stuv.domain.image.entity.QImageFile.imageFile;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;

import java.util.List;

@RequiredArgsConstructor
public class FriendRepositoryImpl implements FriendRepositoryCustom {
    private final JPAQueryFactory jpaQueryFactory;

    // 내게 Follow 요청한 친구 목록 조회
    @Override
    public List<FriendListDTO> getFriendFollowList(Long receiverId, Pageable pageable) {
        return jpaQueryFactory
                .select(Projections.constructor(FriendListDTO.class,
                        friend.id,
                        user.id,
                        user.nickname,
                        user.context,
                        userCostume.costumeId,
                        imageFile.newFilename,
                        friend.status.stringValue()))
                .from(friend).join(user).on(friend.userId.eq(user.id))
                .leftJoin(userCostume).on(user.costumeId.eq(userCostume.id))
                .leftJoin(imageFile).on(userCostume.costumeId.eq(imageFile.entityId).and(imageFile.entityType.eq(EntityType.COSTUME)))
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
                        user.id,
                        user.nickname,
                        user.context,
                        userCostume.costumeId,
                        imageFile.newFilename,
                        friend.status.stringValue()))
                .from(friend).join(user).on(friend.userId.eq(userId).and(friend.friendId.eq(user.id)).or(friend.friendId.eq(userId).and(friend.userId.eq(user.id))))
                .leftJoin(userCostume).on(user.costumeId.eq(userCostume.id))
                .leftJoin(imageFile).on(userCostume.costumeId.eq(imageFile.entityId).and(imageFile.entityType.eq(EntityType.COSTUME)))
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
        return jpaQueryFactory
                .select(Projections.constructor(FriendListDTO.class,
                        friend.id,
                        user.id,
                        user.nickname,
                        user.context,
                        userCostume.costumeId,
                        imageFile.newFilename,
                        Expressions.cases()
                                .when(friend.userId.eq(userId)).then(FriendStatus.PENDING.toString())
                                .otherwise(FriendStatus.NOT_FRIEND.toString())))
                .from(user)
                .leftJoin(friend).on((friend.userId.eq(userId).and(friend.friendId.eq(user.id))).or(friend.friendId.eq(userId).and(friend.userId.eq(user.id))))
                .leftJoin(userCostume).on(user.costumeId.eq(userCostume.id))
                .leftJoin(imageFile).on(userCostume.costumeId.eq(imageFile.entityId).and(imageFile.entityType.eq(EntityType.COSTUME)))
                .where(user.id.ne(userId).and((user.nickname.contains(target)).or(user.context.contains(target)))
                    .and(friend.id.isNull().or(friend.status.eq(FriendStatus.PENDING).and(friend.userId.eq(userId)))))
                .offset(pageable.getOffset()).limit(pageable.getPageSize()).fetch();
    }

    @Override
    public Long getTotalSearchUserPage(Long userId, String target) {
        return jpaQueryFactory.select(user.count()).from(user)
                .leftJoin(friend).on(friend.userId.eq(userId).and(friend.friendId.eq(user.id)).or(friend.friendId.eq(userId).and(friend.userId.eq(user.id))))
                .where(user.id.ne(userId))
                .where(friend.id.isNull().or(friend.status.eq(FriendStatus.PENDING).and(friend.userId.eq(userId))))
                .fetchOne();
    }

    // 친구 추천 ( 내 친구 목록에 없는 친구 )
    @Override
    public List<FriendRecommendDTO> recommendFriend(Long userId) {
        JPQLQuery<Long> subQuery = JPAExpressions
                .select(user.id)
                .from(friend).join(user).on(friend.userId.eq(user.id).and(friend.friendId.eq(userId)).or(friend.friendId.eq(user.id).and(friend.userId.eq(userId))));
        return jpaQueryFactory
                .select(Projections.constructor(FriendRecommendDTO.class,
                        user.id,
                        user.nickname,
                        user.context,
                        userCostume.costumeId,
                        imageFile.newFilename))
                .from(user).leftJoin(userCostume).on(user.costumeId.eq(userCostume.id))
                .leftJoin(imageFile).on(userCostume.costumeId.eq(imageFile.entityId).and(imageFile.entityType.eq(EntityType.COSTUME)))
                .where(user.id.ne(userId).and(user.id.notIn(subQuery)))
                .orderBy(Expressions.numberTemplate(Double.class, "RAND()").asc())
                .limit(3).fetch();
    }
}
