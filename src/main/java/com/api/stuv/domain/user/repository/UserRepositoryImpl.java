package com.api.stuv.domain.user.repository;

import com.api.stuv.domain.image.entity.EntityType;
import com.api.stuv.domain.image.entity.QImageFile;
import com.api.stuv.domain.image.service.S3ImageService;
import com.api.stuv.domain.shop.entity.QCostume;
import com.api.stuv.domain.user.dto.response.MyInformationResponse;
import com.api.stuv.domain.user.entity.QUser;
import com.api.stuv.domain.user.entity.QUserCostume;
import com.querydsl.core.Tuple;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class UserRepositoryImpl implements UserRepositoryCustom{
    private final JPAQueryFactory jpaQueryFactory;
    private final S3ImageService s3ImageService;
    private final QUser u = QUser.user;
    private final QUserCostume uc = QUserCostume.userCostume;
    private final QCostume c = QCostume.costume;
    private final QImageFile i = QImageFile.imageFile;

    // userCostumeId = users 테이블의 costume_Id
    @Override
    public String getUserProfile(Long userCostumeId) {
        Tuple costumeDetails = jpaQueryFactory
                .select(i.id, i.newFilename)
                .from(uc).leftJoin(c).on(uc.costumeId.eq(c.id))
                .leftJoin(i).on(c.imagefileId.eq(i.id))
                .where(uc.id.eq(userCostumeId))
                .fetchOne();

        return ( costumeDetails == null )
                ? null : // TODO: 프로필을 찾지 못한 경우 or 유저가 프로필을 선택하지 않았을 떄의 기본 프로필을 null 대신 써주세요
                s3ImageService.generateImageFile(EntityType.COSTUME, costumeDetails.get(i.id), costumeDetails.get(i.newFilename));
    }

    @Override
    public MyInformationResponse getUserByMyId(Long myId) {
        Tuple informationDetails = jpaQueryFactory
                .select(u.id, u.context, u.blogLink, u.nickname, u.point)
                .from(u)
                .where(u.id.eq(myId))
                .fetchOne();

        return new MyInformationResponse(
                informationDetails.get(u.id),
                informationDetails.get(u.context),
                informationDetails.get(u.blogLink),
                informationDetails.get(u.nickname),
                informationDetails.get(u.point)
        );
    }


}