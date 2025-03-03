package com.api.stuv.domain.party.service;

import com.api.stuv.domain.party.dto.response.PartyGroupResponse;
import com.api.stuv.domain.party.dto.response.PartyRewardStatusResponse;
import com.api.stuv.domain.party.entity.QuestStatus;
import com.api.stuv.domain.party.repository.party.PartyGroupRepository;
import com.api.stuv.domain.user.entity.RewardType;
import com.api.stuv.domain.user.repository.UserRepository;
import com.api.stuv.global.exception.ErrorCode;
import com.api.stuv.global.exception.NotFoundException;
import com.api.stuv.global.response.PageResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PartyService {

    private final PartyGroupRepository partyRepository;
    private final UserRepository userRepository;

    public PageResponse<PartyGroupResponse> getPendingPartyGroups(String name, Pageable pageable) {
        return partyRepository.findPendingGroupsByName(name, pageable);
    }

    public List<PartyGroupResponse> getActivePartyGroupsByUserId(Long userId) {
        if (!userRepository.existsById(userId)) throw new NotFoundException(ErrorCode.USER_NOT_FOUND);
        return partyRepository.findActivePartyGroupsByUserId(userId);
    }

    public List<PartyRewardStatusResponse> getCompletePartyStatus(Long userId) {
        if (!userRepository.existsById(userId)) throw new NotFoundException(ErrorCode.USER_NOT_FOUND);
        return partyRepository.findCompletePartuStatusList(userId).stream().map(dto -> {
            Long count = partyRepository.countAllGroupMembers(dto.partyId());
            BigDecimal reward = Optional.ofNullable(dto.bettingPoint()).orElse(BigDecimal.ZERO);

            if (dto.questStatus() != QuestStatus.FAILED) {
                long successCount = Optional.ofNullable(partyRepository.countSuccessGroupMembers(dto.partyId())).orElse(0L);

                BigDecimal failedBettingSum = Optional.ofNullable(partyRepository.sumFailedGroupMemberBettingPoint(dto.partyId()))
                        .orElse(BigDecimal.ZERO);

                BigDecimal serviceSupportPoint = RewardType.PARTY.getValue()
                        .multiply(BigDecimal.valueOf(count));

                if (successCount > 0) {
                    BigDecimal bonus = (failedBettingSum.add(serviceSupportPoint))
                            .divide(BigDecimal.valueOf(successCount), RoundingMode.FLOOR);
                    reward = reward.add(bonus);
                }
            }

            return new PartyRewardStatusResponse(
                    dto.partyId(),
                    dto.partyName(),
                    reward,
                    dto.questStatus().getText()
            );
        }).toList();
    }
}
