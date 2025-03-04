package com.api.stuv.domain.user.repository;

import com.api.stuv.domain.board.entity.QBoard;
import com.api.stuv.domain.friend.entity.QFriend;
import com.api.stuv.domain.friend.repository.FriendRepository;
import com.api.stuv.domain.image.entity.EntityType;
import com.api.stuv.domain.image.entity.QImageFile;
import com.api.stuv.domain.image.service.S3ImageService;
import com.api.stuv.domain.user.dto.UserProfileInfoDTO;
import com.api.stuv.domain.user.dto.response.GetCostume;
import com.api.stuv.domain.user.dto.response.UserBoardListResponse;
import com.api.stuv.domain.user.dto.response.UserInformationResponse;
import com.api.stuv.domain.user.dto.response.MyInformationResponse;
import com.api.stuv.domain.user.entity.QUser;
import com.api.stuv.domain.user.entity.QUserCostume;
import com.api.stuv.global.exception.ErrorCode;
import com.api.stuv.global.exception.NotFoundException;
import com.api.stuv.global.util.common.TemplateUtils;
import com.querydsl.core.Tuple;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
public class UserRepositoryImpl implements UserRepositoryCustom{
    private final JPAQueryFactory jpaQueryFactory;
    private final S3ImageService s3ImageService;
    private final FriendRepository friendRepository;
    private final QUser u = QUser.user;
    private final QImageFile i = QImageFile.imageFile;
    private final QFriend f = QFriend.friend;
    private final QBoard b = QBoard.board;
    private final QUserCostume uc = QUserCostume.userCostume;


    @Override
    public MyInformationResponse getUserByMyId(Long myId, Long friends) {
        Tuple informationDetails = jpaQueryFactory
                .select(u.id, u.context, u.blogLink, u.nickname, u.point, u.role, i.newFilename, uc.costumeId)
                .from(u)
                .leftJoin(uc).on(u.costumeId.eq(uc.id))
                .leftJoin(i).on(uc.costumeId.eq(i.entityId).and(i.entityType.eq(EntityType.COSTUME)))
                .where(u.id.eq(myId))
                .fetchOne();


        return new MyInformationResponse(
                informationDetails.get(u.id),
                informationDetails.get(u.context),
                informationDetails.get(u.blogLink),
                informationDetails.get(u.nickname),
                informationDetails.get(u.point),
                informationDetails.get(u.role),
                informationDetails.get(i.newFilename) == null ? null : s3ImageService.generateImageFile(
                        EntityType.COSTUME,
                        informationDetails.get(uc.costumeId),
                        informationDetails.get(i.newFilename)),
                friends
        );
    }


    @Override
   public UserInformationResponse getUserInformation(Long userId, Long myId, Long friends) {
       Tuple informationDetails = jpaQueryFactory
               .select(u.id, u.context, u.blogLink, u.nickname, f.status, f.id, u.costumeId, i.newFilename, uc.costumeId)
               .from(u)
               .leftJoin(f).on
                       (f.userId.eq(userId).and(f.friendId.eq(myId))
                       .or(f.friendId.eq(userId).and(f.userId.eq(myId))))
               .leftJoin(uc).on(u.costumeId.eq(uc.id))
               .leftJoin(i).on(uc.costumeId.eq(i.entityId).and(i.entityType.eq(EntityType.COSTUME)))
               .where(u.id.eq(userId))
               .fetchOne();

       if(informationDetails == null) {
           throw new NotFoundException(ErrorCode.USER_NOT_FOUND);
       }

       String status = null;
       if(informationDetails.get(f.status) != null) {
           status = informationDetails.get(f.status).toString();
       }

       return new UserInformationResponse(
               informationDetails.get(u.id),
               informationDetails.get(u.context),
               informationDetails.get(u.blogLink),
               informationDetails.get(u.nickname),
               informationDetails.get(i.newFilename) == null ? null : s3ImageService.generateImageFile(
                       EntityType.COSTUME,
                       informationDetails.get(uc.costumeId),
                       informationDetails.get(i.newFilename)
               ),
               status,
               friends,
               informationDetails.get(f.id)
       );
    }

    @Override
    public List<UserBoardListResponse> getUserBoardList(Long userId) {
        List<Tuple> results = jpaQueryFactory
                .select(b.id, b.title, b.context, b.boardType, TemplateUtils.timeFormater(b.createdAt), i.newFilename)
                .from(b)
                .leftJoin(i).on(b.id.eq(i.entityId).and(i.entityType.eq(EntityType.BOARD)))
                .where(b.userId.eq(userId))
                .fetch();

        List<UserBoardListResponse> query = results.stream()
                .map(tuple -> new UserBoardListResponse(
                        tuple.get(b.id),
                        tuple.get(b.title),
                        tuple.get(b.context),
                        tuple.get(b.boardType),
                        tuple.get(TemplateUtils.timeFormater(b.createdAt)),
                        s3ImageService.generateImageFile(
                                EntityType.BOARD,
                                tuple.get(b.id),
                                tuple.get(i.newFilename)
                        )
                ))
                .toList();

        return query;
    }

    @Override
    public List<GetCostume> getUserCostume(Long userId) {
        List<Tuple> list = jpaQueryFactory
                .select(uc.costumeId, i.newFilename)
                .from(uc)
                .leftJoin(i).on(i.entityType.eq(EntityType.COSTUME).and(i.entityId.eq(uc.costumeId)))
                .where(uc.userId.eq(userId))
                .fetch();

        if(list.isEmpty()) {
            throw new NotFoundException(ErrorCode.COSTUME_NOT_FOUND);
        }

        List<GetCostume> query = list.stream()
                .map(tuple -> new GetCostume(
                        tuple.get(uc.costumeId),
                        s3ImageService.generateImageFile(
                                EntityType.COSTUME,
                                tuple.get(uc.costumeId),
                                tuple.get(i.newFilename)
                        )
                ))
                .toList();

        return query;
    }

    @Override
    public List<UserProfileInfoDTO> findUserInfoByIds(List<Long> userIds) {
        return jpaQueryFactory
                .select(Projections.constructor(
                        UserProfileInfoDTO.class,
                        u.id,
                        u.nickname,
                        i.newFilename,
                        i.entityId
                ))
                .from(u)
                .leftJoin(uc).on(u.costumeId.eq(uc.id))
                .leftJoin(i).on(uc.costumeId.eq(i.entityId)
                        .and(i.entityType.eq(EntityType.COSTUME)))
                .where(u.id.in(userIds))
                .fetch();
    }

    @Override
    public String getCostumeInfoByUserId(Long userId) {
        Tuple costumeDetails = jpaQueryFactory
                .select(i.entityId, i.newFilename)
                .from(u)
                .leftJoin(uc).on(u.costumeId.eq(uc.id))
                .leftJoin(i).on(uc.costumeId.eq(i.entityId).and(i.entityType.eq(EntityType.COSTUME)))
                .where(u.id.eq(userId))
                .fetchFirst();

        if (costumeDetails == null || costumeDetails.get(i.newFilename) == null) {
            return null;
        }

        Long entityId = costumeDetails.get(i.entityId);
        String filename = costumeDetails.get(i.newFilename);

        return s3ImageService.generateImageFile(
                EntityType.COSTUME,
                entityId,
                filename
        );
    }

    @Override
    public Long countNewUserByWeekend(LocalDateTime startDate, LocalDateTime endDate) {
        return Optional.ofNullable(jpaQueryFactory
                .select(
                        u.id.count()
                ).from(u)
                .where(u.createdAt.between(startDate, endDate))
                .fetchOne())
                .orElse(0L);
    }
}