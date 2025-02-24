package com.api.stuv.domain.friend.repository;

import com.api.stuv.domain.friend.dto.FriendFollowListResponse;
import com.api.stuv.domain.friend.dto.FriendResponse;
import com.api.stuv.domain.friend.entity.FriendStatus;
import com.api.stuv.domain.friend.entity.QFriend;
import com.api.stuv.domain.image.entity.EntityType;
import com.api.stuv.domain.image.entity.QImageFile;
import com.api.stuv.domain.image.service.S3ImageService;
import com.api.stuv.domain.shop.entity.QCostume;
import com.api.stuv.domain.user.entity.QUser;
import com.api.stuv.domain.user.entity.QUserCostume;
import com.api.stuv.global.response.PageResponse;
import com.api.stuv.global.util.email.common.TemplateUtils;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.List;

@RequiredArgsConstructor
public class FriendRepositoryImpl implements FriendRepositoryCustom {
    private final JPAQueryFactory jpaQueryFactory;
    private final S3ImageService s3ImageService;
    private final QFriend f = QFriend.friend;
    private final QUser u = QUser.user;
    private final QUserCostume uc = QUserCostume.userCostume;
    private final QCostume c = QCostume.costume;
    private final QImageFile i = QImageFile.imageFile;


    @Override
    public PageResponse<FriendFollowListResponse> getFriendFollowList(Long receiverId, Pageable pageable, String imageUrl) {
        JPAQuery<FriendFollowListResponse> query = jpaQueryFactory
                .select(Projections.constructor(FriendFollowListResponse.class,
                        f.id,
                        u.id,
                        TemplateUtils.getImageUrl(imageUrl, u.costumeId),
                        u.nickname,
                        u.context))
                .from(f).leftJoin(u).on(f.userId.eq(u.id))
                .where(f.friendId.eq(receiverId).and(f.status.eq(FriendStatus.PENDING)));
        return PageResponse.applyPage(query, pageable, getTotalFriendFollowListPage(receiverId));
    }

    @Override
    public PageResponse<FriendResponse> getFriendList(Long receiverId, Pageable pageable, String imageUrl) {
        JPAQuery<FriendResponse> query = jpaQueryFactory
                .select(Projections.constructor(FriendResponse.class,
                        u.id,
                        u.nickname,
                        u.context,
                        TemplateUtils.getImageUrl(imageUrl, u.costumeId)))
                .from(f).leftJoin(u).on(f.userId.eq(u.id).or(f.friendId.eq(u.id)))
                .where(f.userId.eq(receiverId).or(f.friendId.eq(receiverId)).and(f.status.eq(FriendStatus.ACCEPTED)).and(u.id.ne(receiverId)));
        return PageResponse.applyPage(query, pageable, getTotalFriendListPage(receiverId));
    }

    @Override
    public PageResponse<FriendResponse> searchUser(Long userId, String target, Pageable pageable) {
        List<FriendResponse> response = jpaQueryFactory
                .select(u.id, u.nickname, i.newFilename, u.context)
                .from(u).leftJoin(uc).on(u.costumeId.eq(uc.id))
                .leftJoin(c).on(uc.costumeId.eq(c.id))
                .leftJoin(i).on(c.id.eq(i.entityId).and(i.entityType.eq(EntityType.COSTUME)))
                .where(u.nickname.contains(target).or(u.context.contains(target)).and(u.id.ne(userId)))
                .offset(pageable.getOffset()).limit(pageable.getPageSize()).fetch()
                .stream().map(tuple -> new FriendResponse(
                        tuple.get(u.id),
                        tuple.get(u.nickname),
                        tuple.get(u.context),
                        s3ImageService.generateImageFile(EntityType.COSTUME, tuple.get(i.id), tuple.get(i.newFilename)))).toList();
        return PageResponse.of(new PageImpl<>(response, pageable, getTotalSearchUserPage(userId, target)));
    }

    @Override
    public List<FriendResponse> recommendFriend(Long userId) {
        List<Long> friendIds = jpaQueryFactory.select(f.friendId).from(f).where(f.userId.eq(userId)).fetch();
        friendIds.addAll(jpaQueryFactory.select(f.userId).from(f).where(f.friendId.eq(userId)).fetch());
        return jpaQueryFactory
                .select(u.id, u.nickname, i.newFilename, u.context)
                .from(u).leftJoin(uc).on(u.costumeId.eq(uc.id))
                .leftJoin(i).on(uc.costumeId.eq(i.entityId).and(i.entityType.eq(EntityType.COSTUME)))
                .where(u.id.notIn(friendIds))
                .orderBy(Expressions.numberTemplate(Double.class, "RAND()").asc())
                .limit(3)
                .fetch().stream().map(tuple -> new FriendResponse(
                        tuple.get(u.id),
                        tuple.get(u.nickname),
                        tuple.get(u.context),
                        s3ImageService.generateImageFile(EntityType.COSTUME, tuple.get(i.id), tuple.get(i.newFilename)))).toList();
    }

    private Long getTotalFriendFollowListPage(Long receiverId) {
        return jpaQueryFactory.select(f.count()).from(f).where(f.friendId.eq(receiverId).and(f.status.eq(FriendStatus.PENDING))).fetchOne();
    }

    private Long getTotalFriendListPage(Long receiverId) {
        return jpaQueryFactory.select(f.count()).from(f).where(f.userId.eq(receiverId).or(f.friendId.eq(receiverId)).and(f.status.eq(FriendStatus.ACCEPTED))).fetchOne();
    }

    private Long getTotalSearchUserPage(Long userId, String target) {
        return jpaQueryFactory.select(u.count()).from(u).where(u.nickname.contains(target).or(u.context.contains(target)).and(u.id.ne(userId))).fetchOne();
    }
}
