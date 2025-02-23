package com.api.stuv.domain.friend.repository;

import com.api.stuv.domain.friend.dto.FriendFollowListResponse;
import com.api.stuv.domain.friend.dto.FriendResponse;
import com.api.stuv.domain.friend.entity.FriendStatus;
import com.api.stuv.domain.friend.entity.QFriend;
import com.api.stuv.domain.user.entity.QUser;
import com.api.stuv.global.response.PageResponse;
import com.api.stuv.global.util.email.common.TemplateUtils;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;

@RequiredArgsConstructor
public class FriendRepositoryImpl implements FriendRepositoryCustom {
    private final JPAQueryFactory jpaQueryFactory;
    private final QFriend f = QFriend.friend;
    private final QUser u = QUser.user;

    @Override
    public PageResponse<FriendFollowListResponse> getFriendFollowList(Long receiverId, Pageable pageable, String imageUrl) {
        JPAQuery<FriendFollowListResponse> query = jpaQueryFactory
                .select(Projections.constructor(FriendFollowListResponse.class,
                        f.id.as("friendId"),
                        u.id.as("senderId"),
                        TemplateUtils.getImageUrl(imageUrl, u.costumeId).as("profile"),
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
                        u.id.as("userId"),
                        u.nickname,
                        u.context,
                        TemplateUtils.getImageUrl(imageUrl, u.costumeId).as("profile")))
                .from(f).leftJoin(u).on(f.userId.eq(u.id).or(f.friendId.eq(u.id)))
                .where(f.userId.eq(receiverId).or(f.friendId.eq(receiverId)).and(f.status.eq(FriendStatus.ACCEPTED)).and(u.id.ne(receiverId)));
        return PageResponse.applyPage(query, pageable, getTotalFriendListPage(receiverId));
    }

    private Long getTotalFriendFollowListPage(Long receiverId) {
        return jpaQueryFactory.select(f.count()).from(f).where(f.friendId.eq(receiverId).and(f.status.eq(FriendStatus.PENDING))).fetchOne();
    }

    private Long getTotalFriendListPage(Long receiverId) {
        return jpaQueryFactory.select(f.count()).from(f).where(f.userId.eq(receiverId).or(f.friendId.eq(receiverId)).and(f.status.eq(FriendStatus.ACCEPTED))).fetchOne();
    }
}
