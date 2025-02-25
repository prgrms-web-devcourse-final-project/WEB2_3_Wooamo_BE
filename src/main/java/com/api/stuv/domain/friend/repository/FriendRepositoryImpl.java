package com.api.stuv.domain.friend.repository;

import com.api.stuv.domain.friend.dto.FriendFollowListResponse;
import com.api.stuv.domain.friend.dto.FriendResponse;
import com.api.stuv.domain.friend.entity.FriendStatus;
import com.api.stuv.domain.friend.entity.QFriend;
import com.api.stuv.domain.image.entity.EntityType;
import com.api.stuv.domain.image.entity.QImageFile;
import com.api.stuv.domain.image.service.S3ImageService;
import com.api.stuv.domain.user.entity.QUser;
import com.api.stuv.global.response.PageResponse;
import com.querydsl.core.types.dsl.Expressions;
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
    private final QImageFile i = QImageFile.imageFile;

    @Override
    public PageResponse<FriendFollowListResponse> getFriendFollowList(Long receiverId, Pageable pageable, String imageUrl) {
        List<FriendFollowListResponse> response = jpaQueryFactory
                .select(f.id,
                        u.id,
                        u.costumeId,
                        i.newFilename,
                        u.nickname,
                        u.context)
                .from(f).join(u).on(f.userId.eq(u.id))
                .join(i).on(u.costumeId.eq(i.entityId).and(i.entityType.eq(EntityType.COSTUME)))
                .where(f.friendId.eq(receiverId).and(f.status.eq(FriendStatus.PENDING)))
                .offset(pageable.getOffset()).limit(pageable.getPageSize()).fetch()
                .stream().map( tuple -> new FriendFollowListResponse(
                        tuple.get(f.id),
                        tuple.get(u.id),
                        s3ImageService.generateImageFile(EntityType.COSTUME, tuple.get(u.costumeId), tuple.get(i.newFilename)),
                        tuple.get(u.nickname),
                        tuple.get(u.context))).toList();
        return PageResponse.of(new PageImpl<>(response, pageable, getTotalFriendFollowListPage(receiverId)));
    }

    @Override
    public PageResponse<FriendResponse> getFriendList(Long userId, Pageable pageable, String imageUrl) {
        List<FriendResponse> response = jpaQueryFactory
                .select(f.id, u.id, u.nickname, u.context, u.costumeId, i.newFilename)
                .from(f).join(u).on(f.userId.eq(userId).and(f.friendId.eq(u.id))
                        .or(f.friendId.eq(userId).and(f.userId.eq(u.id))))
                .join(i).on(u.costumeId.eq(i.entityId).and(i.entityType.eq(EntityType.COSTUME)))
                .where(f.status.eq(FriendStatus.ACCEPTED))
                .offset(pageable.getOffset()).limit(pageable.getPageSize()).fetch()
                .stream().map( tuple -> new FriendResponse(
                        tuple.get(f.id),
                        tuple.get(u.id),
                        tuple.get(u.nickname),
                        tuple.get(u.context),
                        s3ImageService.generateImageFile(EntityType.COSTUME, tuple.get(u.costumeId), tuple.get(i.newFilename)))).toList();
        return PageResponse.of(new PageImpl<>(response, pageable, getTotalFriendListPage(userId)));
    }

    @Override
    public PageResponse<FriendResponse> searchUser(Long userId, String target, Pageable pageable) {
        List<Long> friendIds = getFriendAndMyIdList(userId);
        List<FriendResponse> response = jpaQueryFactory
                .select(u.id, u.nickname, u.costumeId, i.newFilename, u.context)
                .from(u).join(i).on(u.costumeId.eq(i.entityId).and(i.entityType.eq(EntityType.COSTUME)))
                .where((u.nickname.contains(target).or(u.context.contains(target))).and(u.id.notIn(friendIds)))
                .offset(pageable.getOffset()).limit(pageable.getPageSize()).fetch()
                .stream().map(tuple -> new FriendResponse(
                        null,
                        tuple.get(u.id),
                        tuple.get(u.nickname),
                        tuple.get(u.context),
                        s3ImageService.generateImageFile(EntityType.COSTUME, tuple.get(u.costumeId), tuple.get(i.newFilename)))).toList();
        return PageResponse.of(new PageImpl<>(response, pageable, getTotalSearchUserPage(userId, target, friendIds)));
    }

    @Override
    public List<FriendResponse> recommendFriend(Long userId) {
        List<Long> friendIds = getFriendAndMyIdList(userId);
        return jpaQueryFactory
                .select(u.id, u.nickname, u.costumeId, i.newFilename, u.context)
                .from(u).join(i).on(u.costumeId.eq(i.entityId).and(i.entityType.eq(EntityType.COSTUME)))
                .where(u.id.notIn(friendIds))
                .orderBy(Expressions.numberTemplate(Double.class, "RAND()").asc())
                .limit(3)
                .fetch().stream().map(tuple -> new FriendResponse(
                        null,
                        tuple.get(u.id),
                        tuple.get(u.nickname),
                        tuple.get(u.context),
                        s3ImageService.generateImageFile(EntityType.COSTUME, tuple.get(u.costumeId), tuple.get(i.newFilename)))).toList();
    }

    public List<Long> getFriendAndMyIdList(Long userId) {
        List<Long> friendIds = jpaQueryFactory
                .select(u.id)
                .from(f).join(u).on(f.userId.eq(u.id).and(f.friendId.eq(userId))
                        .or(f.friendId.eq(u.id).and(f.userId.eq(userId))))
                .fetch();
        friendIds.add(userId);
        return friendIds;
    }

    private Long getTotalFriendFollowListPage(Long receiverId) {
        return jpaQueryFactory.select(f.count()).from(f).where(f.friendId.eq(receiverId).and(f.status.eq(FriendStatus.PENDING))).fetchOne();
    }

    private Long getTotalFriendListPage(Long userId) {
        return jpaQueryFactory.select(f.count()).from(f).where((f.userId.eq(userId).or(f.friendId.eq(userId))).and(f.status.eq(FriendStatus.ACCEPTED))).fetchOne();
    }

    private Long getTotalSearchUserPage(Long userId, String target, List<Long> friendIds) {
        return jpaQueryFactory.select(u.count()).from(u).where(u.nickname.contains(target).or(u.context.contains(target)).and(u.id.ne(userId)).and(u.id.notIn(friendIds))).fetchOne();
    }
}
