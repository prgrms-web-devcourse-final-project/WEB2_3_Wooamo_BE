package com.api.stuv.domain.friend.repository;

import com.api.stuv.domain.friend.dto.FriendRequestListResponse;
import com.api.stuv.domain.friend.entity.FriendStatus;
import com.api.stuv.domain.friend.entity.QFriend;
import com.api.stuv.domain.user.entity.QUser;
import com.api.stuv.global.response.PageResponse;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.NumberPath;
import com.querydsl.core.types.dsl.StringTemplate;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.List;

@RequiredArgsConstructor
public class FriendRepositoryImpl implements FriendRepositoryCustom {
    private final JPAQueryFactory jpaQueryFactory;
    private final QFriend f = QFriend.friend;
    private final QUser u = QUser.user;

    @Override
    public PageResponse<FriendRequestListResponse> getFriendRequestList(Long receiverId, Pageable pageable, String imageUrl) {
        JPAQuery<FriendRequestListResponse> query = jpaQueryFactory
                .select(Projections.constructor(FriendRequestListResponse.class,
                        f.id,
                        u.id.as("senderId"),
                        getImageUrl(imageUrl, u.costumeId).as("profile"),
                        u.nickname,
                        u.context))
                .from(f).leftJoin(u).on(f.userId.eq(u.id))
                .where(f.friendId.eq(receiverId).and(f.status.eq(FriendStatus.PENDING)));
        return applyPage(query, pageable, getTotalPage(receiverId));
    }

    private <T> PageResponse<T> applyPage(JPAQuery<T> query, Pageable pageable, Long count) {
        List<T> content = query.offset(pageable.getOffset()).limit(pageable.getPageSize()).fetch();
        return PageResponse.of(new PageImpl<>(content, pageable, count));
    }

    private Long getTotalPage(Long receiverId) {
        return jpaQueryFactory.select(f.count()).from(f).where(f.friendId.eq(receiverId).and(f.status.eq(FriendStatus.PENDING))).fetchOne();
    }

    private StringTemplate getImageUrl(String imageUrl, NumberPath<Long> id) {
        return Expressions.stringTemplate("CONCAT({0}, {1})", imageUrl, id);
    }
}
