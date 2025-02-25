package com.api.stuv.domain.party.repository.confirm;

import com.api.stuv.domain.image.dto.ImageResponse;
import com.api.stuv.domain.image.entity.EntityType;
import com.api.stuv.domain.image.entity.QImageFile;
import com.api.stuv.domain.image.service.S3ImageService;
import com.api.stuv.domain.party.entity.QQuestConfirm;
import com.api.stuv.global.exception.ErrorCode;
import com.api.stuv.global.exception.NotFoundException;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;

import java.time.LocalDate;

@RequiredArgsConstructor
public class QuestConfirmRepositoryImpl implements QuestConfirmRepositoryCustom {

    private final JPAQueryFactory factory;
    private final S3ImageService s3ImageService;
    private final QQuestConfirm qc = QQuestConfirm.questConfirm;
    private final QImageFile i = QImageFile.imageFile;

    @Override
    public ImageResponse findGroupMemberConfirmImageByDate(Long memberId, LocalDate date) {
        return factory.select(qc.memberId, i.newFilename)
                .from(qc)
                .leftJoin(i).on(qc.memberId.eq(i.entityId)
                        .and(i.entityType.eq(EntityType.CONFIRM)))
                .where(qc.memberId.eq(memberId)
                        .and(qc.confirmDate.eq(date)))
                .fetch()
                .stream()
                .map(tp -> {
                    if (tp.get(i.newFilename) == null) throw new NotFoundException(ErrorCode.CONFIRM_IMAGE_NOT_FOUND);
                    return new ImageResponse(s3ImageService.generateImageFile(EntityType.CONFIRM, tp.get(qc.memberId), tp.get(i.newFilename)));
                })
                .findFirst()
                .orElseThrow(() -> new NotFoundException(ErrorCode.CONFIRM_IMAGE_NOT_FOUND));
    }
}
