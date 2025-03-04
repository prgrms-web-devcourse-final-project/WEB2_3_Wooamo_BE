package com.api.stuv.domain.shop.repository;

import com.api.stuv.domain.admin.exception.CostumeNotFound;
import com.api.stuv.domain.image.dto.ImageDTO;
import com.api.stuv.domain.image.entity.EntityType;
import com.api.stuv.domain.image.entity.QImageFile;
import com.api.stuv.domain.image.service.S3ImageService;
import com.api.stuv.domain.shop.dto.CostumeResponse;
import com.api.stuv.domain.shop.entity.QCostume;
import com.api.stuv.domain.user.entity.QUserCostume;
import com.api.stuv.global.response.PageResponse;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
public class CostumeRepositoryImpl implements CostumeRepositoryCustom {
    private final JPAQueryFactory jpaQueryFactory;
    private final QCostume qCostume = QCostume.costume;
    private final QImageFile qImageFile = QImageFile.imageFile;
    private final QUserCostume uc = QUserCostume.userCostume;
    private final S3ImageService s3ImageService;

    @Override
    public PageResponse<CostumeResponse> getCostumeList(Pageable pageable) {
        long totalCount = jpaQueryFactory.select(qCostume.count()).from(qCostume).fetchOne();
        List<CostumeResponse> listResponses = jpaQueryFactory
                .select(qCostume.id,
                        qImageFile.newFilename,
                        qCostume.costumeName,
                        qCostume.point)
                .from(qCostume)
                .leftJoin(qImageFile)
                .on(qImageFile.entityId.eq(qCostume.id).and(qImageFile.entityType.eq(EntityType.COSTUME)))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch()
                .stream()
                .map(tuple -> new CostumeResponse(
                        tuple.get(qCostume.id),
                        s3ImageService.generateImageFile(EntityType.COSTUME, tuple.get(qCostume.id), tuple.get(qImageFile.newFilename)),
                        tuple.get(qCostume.costumeName),
                        tuple.get(qCostume.point)
                )).toList();

        return PageResponse.of(new PageImpl<>(listResponses, pageable, totalCount));
    }

    @Override
    public CostumeResponse getCostume(Long costumeId) {
        return Optional.ofNullable(
                jpaQueryFactory
                        .select(
                                qCostume.costumeName,
                                qCostume.point,
                                qImageFile.newFilename
                        )
                        .from(qCostume)
                        .leftJoin(qImageFile)
                        .on(qImageFile.entityId.eq(qCostume.id).and(qImageFile.entityType.eq(EntityType.COSTUME)))
                        .where(qCostume.id.eq(costumeId))
                        .fetchOne())
                .map(response -> new CostumeResponse(
                        null,
                        s3ImageService.generateImageFile(
                                EntityType.COSTUME,
                                costumeId,
                                response.get(qImageFile.newFilename)
                        ),
                        response.get(qCostume.costumeName),
                        response.get(qCostume.point)
                )).orElseThrow(CostumeNotFound::new);
    }

    @Override
    public ImageDTO findCostumeByBestSales() {
        jpaQueryFactory
                .select(Projections.constructor(
                        ImageDTO.class,
                        qCostume.id,
                        qImageFile.newFilename
                ))
                .from(qCostume)
                .leftJoin(uc).on(qCostume.id.eq(uc.costumeId))
                .leftJoin(qImageFile).on(qCostume.id.eq(qImageFile.entityId)
                        .and(qImageFile.entityType.eq(EntityType.COSTUME)))
                .where(qCostume.id.ne(1L))
                .groupBy(qCostume.id, qImageFile.newFilename)
                .orderBy(uc.count().desc())
                .fetch()
                .forEach(System.out::println);

        return jpaQueryFactory
                .select(Projections.constructor(
                        ImageDTO.class,
                        qCostume.id,
                        qImageFile.newFilename
                ))
                .from(qCostume)
                .leftJoin(uc).on(qCostume.id.eq(uc.costumeId))
                .leftJoin(qImageFile).on(qCostume.id.eq(qImageFile.entityId)
                        .and(qImageFile.entityType.eq(EntityType.COSTUME)))
                .where(qCostume.id.ne(1L))
                .groupBy(qCostume.id, qImageFile.newFilename)
                .orderBy(uc.count().desc())
                .fetchFirst();
    }
}
