package com.api.stuv.domain.shop.repository;

import com.api.stuv.domain.image.dto.ImageDTO;
import com.api.stuv.domain.image.entity.EntityType;
import com.api.stuv.domain.image.entity.QImageFile;
import com.api.stuv.domain.shop.dto.costume.CostumeDTO;
import com.api.stuv.domain.shop.entity.QCostume;
import com.api.stuv.domain.user.entity.QUserCostume;
import com.querydsl.core.Tuple;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static com.api.stuv.domain.image.entity.QImageFile.imageFile;
import static com.api.stuv.domain.shop.entity.QCostume.costume;

@RequiredArgsConstructor
public class CostumeRepositoryImpl implements CostumeRepositoryCustom {
    private final JPAQueryFactory jpaQueryFactory;
    private final QCostume qCostume = QCostume.costume;
    private final QImageFile qImageFile = QImageFile.imageFile;
    private final QUserCostume uc = QUserCostume.userCostume;

    @Override
    public List<CostumeDTO> getCostumeList() {
        return jpaQueryFactory
                .select(Projections.constructor(CostumeDTO.class,
                        costume.id,
                        imageFile.newFilename,
                        costume.costumeName,
                        costume.point
                        ))
                .from(costume)
                .leftJoin(imageFile)
                .on(imageFile.entityId.eq(costume.id).and(imageFile.entityType.eq(EntityType.COSTUME)))
                .fetch();
    }

    @Override
    public Optional<CostumeDTO> getCostume(Long costumeId) {
        Tuple result = jpaQueryFactory
                .select(
                        costume.costumeName,
                        costume.point,
                        imageFile.newFilename
                )
                .from(costume)
                .leftJoin(imageFile)
                .on(imageFile.entityId.eq(costume.id).and(imageFile.entityType.eq(EntityType.COSTUME)))
                .where(costume.id.eq(costumeId))
                .fetchOne();

        return Optional.of(new CostumeDTO(
                null,
                Objects.requireNonNull(result).get(imageFile.newFilename),
                result.get(costume.costumeName),
                result.get(costume.point)
        ));
    }

    @Override
    public ImageDTO findCostumeByBestSales() {
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
