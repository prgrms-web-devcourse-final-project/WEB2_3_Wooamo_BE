package com.api.stuv.domain.shop.repository;

import com.api.stuv.domain.image.entity.ImageType;
import com.api.stuv.domain.image.entity.QImageFile;
import com.api.stuv.domain.image.service.S3ImageService;
import com.api.stuv.domain.shop.dto.CostumeListResponse;
import com.api.stuv.domain.shop.entity.QCostume;
import com.api.stuv.global.response.PageResponse;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.List;

@RequiredArgsConstructor
public class CostumeRepositoryImpl implements CostumeRepositoryCustom {
    private final JPAQueryFactory jpaQueryFactory;
    private final QCostume qCostume = QCostume.costume;
    private final QImageFile qImageFile = QImageFile.imageFile;
    private final S3ImageService s3ImageService;

    @Override
    public PageResponse<CostumeListResponse> getCostumeList(Pageable pageable) {

        long totalCount = jpaQueryFactory.selectFrom(qCostume).fetch().size();
        List<CostumeListResponse> listResponses = jpaQueryFactory
                .select(qCostume.id,
                        qImageFile.newFilename,
                        qCostume.costumeName,
                        qCostume.point)
                .from(qCostume)
                .leftJoin(qImageFile)
                .on(qImageFile.id.eq(qCostume.imagefileId))
                .fetch()
                .stream()
                .map(tuple -> new CostumeListResponse(
                        tuple.get(qCostume.id),
                        s3ImageService.generateImageFile(ImageType.COSTUME, tuple.get(qCostume.id), tuple.get(qImageFile.newFilename)),
                        tuple.get(qCostume.costumeName),
                        tuple.get(qCostume.point)
                )).toList();

        return PageResponse.of(new PageImpl<>(listResponses, pageable, totalCount));
    }
}
