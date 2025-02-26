package com.api.stuv.domain.party.repository.confirm;

import com.api.stuv.domain.image.dto.ImageResponse;

import java.time.LocalDate;

public interface QuestConfirmRepositoryCustom {
    ImageResponse findGroupMemberConfirmImageByDate(Long memberId, LocalDate date);
}
