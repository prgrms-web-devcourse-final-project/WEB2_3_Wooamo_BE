package com.api.stuv.domain.user.repository;

import com.api.stuv.domain.friend.entity.QFriend;
import com.api.stuv.domain.image.entity.EntityType;
import com.api.stuv.domain.image.entity.QImageFile;
import com.api.stuv.domain.image.service.S3ImageService;
import com.api.stuv.domain.user.dto.response.UserInformationResponse;
import com.api.stuv.domain.user.dto.response.MyInformationResponse;
import com.api.stuv.domain.user.entity.QUser;
import com.api.stuv.global.exception.ErrorCode;
import com.api.stuv.global.exception.NotFoundException;
import com.querydsl.core.Tuple;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class UserRepositoryImpl implements UserRepositoryCustom{
    private final JPAQueryFactory jpaQueryFactory;
    private final S3ImageService s3ImageService;
    private final QUser u = QUser.user;
    private final QImageFile i = QImageFile.imageFile;
    private final QFriend f = QFriend.friend;

    @Override
    public MyInformationResponse getUserByMyId(Long myId) {
        Tuple informationDetails = jpaQueryFactory
                .select(u.id, u.context, u.blogLink, u.nickname, u.point, i.newFilename)
                .from(u)
                .leftJoin(i).on(u.costumeId.eq(i.entityId).and(i.entityType.eq(EntityType.COSTUME)))
                .where(u.id.eq(myId).and(u.costumeId.eq(i.entityId)))
                .fetchOne();

        return new MyInformationResponse(
                informationDetails.get(u.id),
                informationDetails.get(u.context),
                informationDetails.get(u.blogLink),
                informationDetails.get(u.nickname),
                informationDetails.get(u.point),
                s3ImageService.generateImageFile(
                        EntityType.COSTUME,
                        informationDetails.get(u.costumeId),
                        informationDetails.get(i.newFilename))
        );
    }

  @Override
   public UserInformationResponse getUserInformation(Long userId, Long myId) {
       Tuple informationDetails = jpaQueryFactory
               .select(u.id, u.context, u.blogLink, u.nickname, f.status, u.costumeId, i.newFilename)
               .from(u)
               .leftJoin(f).on
                       (f.userId.eq(userId).and(f.friendId.eq(myId))
                       .or(f.friendId.eq(userId).and(f.userId.eq(myId))))
               .leftJoin(i).on(u.costumeId.eq(i.entityId).and(i.entityType.eq(EntityType.COSTUME)))
               .where(u.id.eq(userId).and(u.costumeId.eq(i.entityId)))
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
                       informationDetails.get(u.costumeId),
                       informationDetails.get(i.newFilename)
               ),
               status
       );
    }


}