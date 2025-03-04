package com.api.stuv.domain.user.repository;

import com.api.stuv.domain.friend.repository.FriendRepository;
import com.api.stuv.domain.image.entity.EntityType;
import com.api.stuv.domain.user.dto.*;
import com.api.stuv.global.exception.ErrorCode;
import com.api.stuv.global.exception.NotFoundException;
import com.api.stuv.global.util.common.TemplateUtils;
import com.querydsl.core.Tuple;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static com.api.stuv.domain.board.entity.QBoard.board;
import static com.api.stuv.domain.friend.entity.QFriend.friend;
import static com.api.stuv.domain.image.entity.QImageFile.imageFile;
import static com.api.stuv.domain.user.entity.QUser.user;
import static com.api.stuv.domain.user.entity.QUserCostume.userCostume;

@RequiredArgsConstructor
public class UserRepositoryImpl implements UserRepositoryCustom{
    private final JPAQueryFactory jpaQueryFactory;
    private final FriendRepository friendRepository;

    @Override
    public MyInformationDTO getUserByMyId(Long myId, Long friends) {
        Tuple informationDetails = jpaQueryFactory
                .select(user.id, user.context, user.blogLink, user.nickname, user.point, user.role, imageFile.newFilename, userCostume.costumeId)
                .from(user)
                .leftJoin(userCostume).on(user.costumeId.eq(userCostume.id))
                .leftJoin(imageFile).on(userCostume.costumeId.eq(imageFile.entityId).and(imageFile.entityType.eq(EntityType.COSTUME)))
                .where(user.id.eq(myId))
                .fetchOne();

        return new MyInformationDTO(
                informationDetails.get(user.id),
                informationDetails.get(user.context),
                informationDetails.get(user.blogLink),
                informationDetails.get(user.nickname),
                informationDetails.get(user.point),
                informationDetails.get(user.role),
                informationDetails.get(imageFile.newFilename),
                informationDetails.get(userCostume.costumeId),
                friends
        );
    }


    @Override
   public UserInformationDTO getUserInformation(Long userId, Long myId, Long friends) {
       Tuple informationDetails = jpaQueryFactory
               .select(user.id, user.context, user.blogLink, user.nickname, friend.status, friend.id, user.costumeId, imageFile.newFilename, userCostume.costumeId)
               .from(user)
               .leftJoin(friend).on
                       (friend.userId.eq(userId).and(friend.friendId.eq(myId))
                       .or(friend.friendId.eq(userId).and(friend.userId.eq(myId))))
               .leftJoin(userCostume).on(user.costumeId.eq(userCostume.id))
               .leftJoin(imageFile).on(userCostume.costumeId.eq(imageFile.entityId).and(imageFile.entityType.eq(EntityType.COSTUME)))
               .where(user.id.eq(userId))
               .fetchOne();

       if(informationDetails == null) {
           throw new NotFoundException(ErrorCode.USER_NOT_FOUND);
       }

       String status = null;
       if(informationDetails.get(friend.status) != null) {
           status = informationDetails.get(friend.status).toString();
       }

       return new UserInformationDTO(
               informationDetails.get(user.id),
               informationDetails.get(user.context),
               informationDetails.get(user.blogLink),
               informationDetails.get(user.nickname),
               informationDetails.get(imageFile.newFilename),
               informationDetails.get(userCostume.costumeId),
               status,
               friends,
               informationDetails.get(friend.id)
       );
    }

    @Override
    public List<UserBoardListDTO> getUserBoardList(Long userId) {

        return jpaQueryFactory
                .select(Projections.constructor(UserBoardListDTO.class,
                                board.id,
                                board.title,
                                board.context,
                                board.boardType,
                                TemplateUtils.timeFormater(board.createdAt),
                                imageFile.newFilename))
                .from(board)
                .leftJoin(imageFile).on(board.id.eq(imageFile.entityId).and(imageFile.entityType.eq(EntityType.BOARD)))
                .where(board.userId.eq(userId))
                .fetch();
    }

    @Override
    public List<GetCostumeDTO> getUserCostume(Long userId) {
        return jpaQueryFactory
                .select(Projections.constructor(GetCostumeDTO.class,
                                userCostume.costumeId,
                                imageFile.newFilename))
                .from(userCostume)
                .leftJoin(imageFile).on(imageFile.entityType.eq(EntityType.COSTUME).and(imageFile.entityId.eq(userCostume.costumeId)))
                .where(userCostume.userId.eq(userId))
                .fetch();
    }

    @Override
    public List<UserProfileInfoDTO> findUserInfoByIds(List<Long> userIds) {
        return jpaQueryFactory
                .select(Projections.constructor(
                        UserProfileInfoDTO.class,
                        user.id,
                        user.nickname,
                        imageFile.newFilename,
                        imageFile.entityId
                ))
                .from(user)
                .leftJoin(userCostume).on(user.costumeId.eq(userCostume.id))
                .leftJoin(imageFile).on(userCostume.costumeId.eq(imageFile.entityId)
                        .and(imageFile.entityType.eq(EntityType.COSTUME)))
                .where(user.id.in(userIds))
                .fetch();
    }

    @Override
    public ImageUrlDTO getCostumeInfoByUserId(Long userId) {
        Tuple costumeDetails = jpaQueryFactory
                .select(imageFile.entityId, imageFile.newFilename)
                .from(user)
                .leftJoin(userCostume).on(user.costumeId.eq(userCostume.id))
                .leftJoin(imageFile).on(userCostume.costumeId.eq(imageFile.entityId).and(imageFile.entityType.eq(EntityType.COSTUME)))
                .where(user.id.eq(userId))
                .fetchFirst();

        if (costumeDetails == null || costumeDetails.get(imageFile.newFilename) == null) {
            return null;
        }

        Long entityId = costumeDetails.get(imageFile.entityId);
        String filename = costumeDetails.get(imageFile.newFilename);

        return new ImageUrlDTO(entityId, filename);
    }

    @Override
    public Long countNewUserByWeekend(LocalDateTime startDate, LocalDateTime endDate) {
        return Optional.ofNullable(jpaQueryFactory
                .select(
                        user.id.count()
                ).from(user)
                .where(user.createdAt.between(startDate, endDate))
                .fetchOne())
                .orElse(0L);
    }
}