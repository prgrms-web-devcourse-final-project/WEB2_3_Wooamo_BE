package com.api.stuv.domain.party.service;

import com.api.stuv.domain.image.entity.EntityType;
import com.api.stuv.domain.image.service.ImageService;
import com.api.stuv.domain.image.service.S3ImageService;
import com.api.stuv.domain.party.dto.MemberRewardStatusDTO;
import com.api.stuv.domain.party.dto.response.MemberResponse;
import com.api.stuv.domain.party.dto.request.PartyCreateRequest;
import com.api.stuv.domain.party.dto.response.*;
import com.api.stuv.domain.party.entity.GroupMember;
import com.api.stuv.domain.party.entity.PartyGroup;
import com.api.stuv.domain.party.entity.QuestConfirm;
import com.api.stuv.domain.party.entity.QuestStatus;
import com.api.stuv.domain.party.repository.confirm.QuestConfirmRepository;
import com.api.stuv.domain.party.repository.member.GroupMemberRepository;
import com.api.stuv.domain.party.repository.party.PartyGroupRepository;
import com.api.stuv.domain.user.entity.HistoryType;
import com.api.stuv.domain.user.entity.PointHistory;
import com.api.stuv.domain.user.entity.RewardType;
import com.api.stuv.domain.user.entity.User;
import com.api.stuv.domain.user.repository.PointHistoryRepository;
import com.api.stuv.domain.user.repository.UserRepository;
import com.api.stuv.global.exception.BusinessException;
import com.api.stuv.global.exception.ErrorCode;
import com.api.stuv.global.exception.NotFoundException;
import com.api.stuv.global.response.PageResponse;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PartyService {

    private final PartyGroupRepository partyRepository;
    private final UserRepository userRepository;
    private final GroupMemberRepository memberRepository;
    private final S3ImageService s3ImageService;
    private final QuestConfirmRepository confirmRepository;
    private final ImageService imageService;
    private final PointHistoryRepository historyRepository;

    public PageResponse<PartyGroupResponse> getPendingPartyGroups(String name, Pageable pageable) {
        return partyRepository.findPendingGroupsByName(name, pageable);
    }

    public List<PartyGroupResponse> getActivePartyGroupsByUserId(Long userId) {
        if (!userRepository.existsById(userId)) throw new NotFoundException(ErrorCode.USER_NOT_FOUND);
        return partyRepository.findActivePartyGroupsByUserId(userId);
    }

    public List<PartyRewardStatusResponse> getCompletePartyStatus(Long userId) {
        if (!userRepository.existsById(userId)) throw new NotFoundException(ErrorCode.USER_NOT_FOUND);
        return partyRepository.findCompletePartyStatusList(userId).stream().map(dto -> {
            Long count = memberRepository.countAllGroupMembers(dto.partyId());
            BigDecimal reward = Optional.ofNullable(dto.bettingPoint()).orElse(BigDecimal.ZERO);

            if (dto.questStatus() != QuestStatus.FAILED) {
                long successCount = Optional.ofNullable(memberRepository.countSuccessGroupMembers(dto.partyId())).orElse(0L);

                BigDecimal failedBettingSum = Optional.ofNullable(memberRepository.sumFailedGroupMemberBettingPoint(dto.partyId()))
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

    public PartyDetailResponse getPartyDetailInfo(Long partyId, Long userId) {
        return partyRepository.findDetailByUserId(partyId, userId)
                .orElseThrow(() -> new NotFoundException(ErrorCode.PARTY_NOT_FOUND));
    }

    public List<EventBannerResponse> getEventList() {
        return partyRepository.findEventBannerList()
                .stream()
                .map(dto -> new EventBannerResponse(
                        s3ImageService.generateImageFile(EntityType.EVENT, dto.partyId(), dto.image()),
                        dto.partyId()
                ))
                .toList();
    }

    public PageResponse<MemberResponse> getPartyMemberList(Long partyId, Long userId, Pageable pageable) {
        if (!userRepository.existsById(userId)) throw new NotFoundException(ErrorCode.USER_NOT_FOUND);
        if (!partyRepository.existsById(partyId)) throw new NotFoundException(ErrorCode.PARTY_NOT_FOUND);
        return PageResponse.of(new PageImpl<>(
                memberRepository.findMemberList(partyId, userId, pageable)
                        .stream()
                        .map(dto -> new MemberResponse(
                                dto.friendId(),
                                dto.userId(),
                                dto.nickname(),
                                s3ImageService.generateImageFile(EntityType.COSTUME, dto.imageId(), dto.image()),
                                dto.context(),
                                dto.status()
                        )).toList(),
                pageable,
                memberRepository.countAllGroupMembers(partyId)
        ));
    }

    @Transactional
    public PartyIdResponse createParty(PartyCreateRequest dto, Long userId) {
        LocalDate today = LocalDate.now();
        if (today.isAfter(dto.startDate()) || today.isEqual(dto.startDate()) || dto.endDate().isBefore(dto.startDate())) throw new BusinessException(ErrorCode.PARTY_INVALID_DATE);
        if (isBettingPointInvalid(dto.bettingPointCap(), dto.userBettingPoint())) throw new BusinessException(ErrorCode.BETTING_AMOUNT_TOO_LOW);

        User user = userRepository.findById(userId).orElseThrow(() -> new NotFoundException(ErrorCode.USER_NOT_FOUND));

        PartyGroup party = PartyGroup.create(dto);
        partyRepository.save(party);

        user.subtractPoint(dto.userBettingPoint());
        GroupMember member = GroupMember.join(party.getId(), userId, dto.userBettingPoint());

        memberRepository.save(member);
        userRepository.save(user);

        return new PartyIdResponse(party.getId());
    }

    @Transactional
    public void joinParty(BigDecimal bettingPoint, Long partyId, Long userId) {
        PartyDetailResponse party = partyRepository.findDetailByUserId(partyId, userId).orElseThrow(() -> new NotFoundException(ErrorCode.PARTY_NOT_FOUND));
        if (party.isJoined()) throw new BusinessException(ErrorCode.ALREADY_JOINED_PARTY);
        if (isFullParty(party.recruitCap(), party.recruitCnt())) throw new BusinessException(ErrorCode.CANNOT_JOIN_FULL_PARTY);
        if (isBettingPointInvalid(party.bettingPointCap(), bettingPoint)) throw new BusinessException(ErrorCode.BETTING_AMOUNT_TOO_LOW);
        User user = userRepository.findById(userId).orElseThrow(() -> new NotFoundException(ErrorCode.USER_NOT_FOUND));

        user.subtractPoint(bettingPoint);
        GroupMember member = GroupMember.join(partyId, userId, bettingPoint);

        memberRepository.save(member);
        userRepository.save(user);
    }

    @Transactional
    public PointResponse getReward(Long partyId, Long userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new NotFoundException(ErrorCode.USER_NOT_FOUND));
        MemberRewardStatusDTO dto = partyRepository.findCompleteParty(partyId, userId).orElseThrow(() -> new NotFoundException(ErrorCode.PARTY_NOT_FOUND));
        Long memberId = memberRepository.findIdByGroupIdAndUserId(partyId, userId).orElseThrow(() -> new NotFoundException(ErrorCode.MEMBER_NOT_FOUND));

        String reason = dto.partyId() + ":팟보상:" + dto.partyName();

        Long count = memberRepository.countAllGroupMembers(dto.partyId());
        BigDecimal reward = Optional.ofNullable(dto.bettingPoint()).orElse(BigDecimal.ZERO);

        if (dto.questStatus() == QuestStatus.FAILED) throw new BusinessException(ErrorCode.MISSION_FAILED);

        long successCount = Optional.ofNullable(memberRepository.countSuccessGroupMembers(dto.partyId())).orElse(0L);

        BigDecimal failedBettingSum = Optional.ofNullable(memberRepository.sumFailedGroupMemberBettingPoint(dto.partyId()))
                .orElse(BigDecimal.ZERO);

        BigDecimal serviceSupportPoint = RewardType.PARTY.getValue()
                .multiply(BigDecimal.valueOf(count));

        if (successCount > 0) {
            BigDecimal bonus = (failedBettingSum.add(serviceSupportPoint))
                    .divide(BigDecimal.valueOf(successCount), RoundingMode.FLOOR);
            reward = reward.add(bonus);
        }

        if (historyRepository.existsByReason(reason)) throw new BusinessException(ErrorCode.ALREADY_RECEIVED_REWARD);
        user.updatePoint(reward);
        PointHistory pointHistory = new PointHistory(userId, HistoryType.PARTY, reward, reason);
        historyRepository.save(pointHistory);
        memberRepository.updateQuestStatusForMember(partyId, memberId, QuestStatus.COMPLETED);

        return new PointResponse(reward);
    }

    @Transactional
    public void dailyVerifyParty(Long userId, Long partyId, MultipartFile image) {
        if (!userRepository.existsById(userId)) throw new NotFoundException(ErrorCode.USER_NOT_FOUND);
        PartyGroup party = partyRepository.findById(partyId).orElseThrow(() -> new NotFoundException(ErrorCode.PARTY_NOT_FOUND));
        Long memberId = memberRepository.findIdByGroupIdAndUserId(partyId, userId).orElseThrow(() -> new NotFoundException(ErrorCode.MEMBER_NOT_FOUND));

        LocalDate today = LocalDate.now();

        if (isValidatePeriod(party, today)) throw new BusinessException(ErrorCode.AUTH_INVALID_DATE);
        if (confirmRepository.existsByMemberIdAndConfirmDate(memberId, today)) throw new BusinessException(ErrorCode.ALREADY_AUTH_TODAY);

        QuestConfirm confirm = new QuestConfirm(memberId, today);

        confirmRepository.save(confirm);
        imageService.handleImage(confirm.getId(), image, EntityType.CONFIRM);
    }

    private boolean isBettingPointInvalid(BigDecimal bettingPoint, BigDecimal userPoint) {
        return userPoint.compareTo(bettingPoint) < 0;
    }

    private boolean isFullParty(Long cap, Long count) {
        return count >= cap;
    }

    private boolean isValidatePeriod(PartyGroup party, LocalDate today) {
        return today.isBefore(party.getStartDate()) || today.isAfter(party.getEndDate());
    }
}
