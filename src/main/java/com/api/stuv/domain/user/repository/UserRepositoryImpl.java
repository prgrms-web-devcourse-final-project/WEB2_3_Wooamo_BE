package com.api.stuv.domain.user.repository;

import com.api.stuv.domain.board.entity.QBoard;
import com.api.stuv.domain.friend.entity.QFriend;
import com.api.stuv.domain.image.entity.EntityType;
import com.api.stuv.domain.image.entity.QImageFile;
import com.api.stuv.domain.image.service.S3ImageService;
import com.api.stuv.domain.user.dto.response.GetCostume;
import com.api.stuv.domain.user.dto.response.UserBoardListResponse;
import com.api.stuv.domain.user.dto.response.UserInformationResponse;
import com.api.stuv.domain.user.dto.response.MyInformationResponse;
import com.api.stuv.domain.user.entity.QUser;
import com.api.stuv.domain.user.entity.QUserCostume;
import com.api.stuv.global.exception.ErrorCode;
import com.api.stuv.global.exception.NotFoundException;
import com.api.stuv.global.util.email.common.TemplateUtils;
import com.querydsl.core.Tuple;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor
public class UserRepositoryImpl implements UserRepositoryCustom{
    private final JPAQueryFactory jpaQueryFactory;
    private final S3ImageService s3ImageService;
    private final QUser u = QUser.user;
    private final QImageFile i = QImageFile.imageFile;
    private final QFriend f = QFriend.friend;
    private final QBoard b = QBoard.board;
    private final QUserCostume uc = QUserCostume.userCostume;


    @Override
    public MyInformationResponse getUserByMyId(Long myId) {
        Tuple informationDetails = jpaQueryFactory
                .select(u.id, u.context, u.blogLink, u.nickname, u.point, i.newFilename, uc.costumeId)
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
                s3ImageService.generateImageFile(
                        EntityType.COSTUME,
                        informationDetails.get(uc.costumeId),
                        informationDetails.get(i.newFilename))
        );
    }


    @Override
   public UserInformationResponse getUserInformation(Long userId, Long myId) {
       Tuple informationDetails = jpaQueryFactory
               .select(u.id, u.context, u.blogLink, u.nickname, f.status, u.costumeId, i.newFilename, uc.costumeId)
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
               s3ImageService.generateImageFile(
                       EntityType.COSTUME,
                       informationDetails.get(uc.costumeId),
                       informationDetails.get(i.newFilename)
               ),
               status
       );
    }

    @Override
    public List<UserBoardListResponse> getUserBoardList(Long userId) {
        List<Tuple> results = jpaQueryFactory
                .select(b.id, b.title, b.context, b.boardType, b.createdAt, i.newFilename)
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
}