package com.api.stuv.domain.shop.service;

import com.api.stuv.domain.auth.util.TokenUtil;
import com.api.stuv.domain.shop.dto.CostumePurchaseRequest;
import com.api.stuv.domain.shop.dto.CostumeResponse;
import com.api.stuv.domain.shop.exception.CostumeAlreadyException;
import com.api.stuv.domain.shop.exception.PointNotEnoughException;
import com.api.stuv.domain.shop.repository.CostumeRepository;
import com.api.stuv.domain.user.entity.User;
import com.api.stuv.domain.user.entity.UserCostume;
import com.api.stuv.domain.user.repository.UserCostumeRepository;
import com.api.stuv.domain.user.repository.UserRepository;
import com.api.stuv.global.exception.ErrorCode;
import com.api.stuv.global.exception.NotFoundException;
import com.api.stuv.global.response.PageResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CostumeService {

    private final CostumeRepository costumeRepository;
    private final UserCostumeRepository userCostumeRepository;
    private final UserRepository userRepository;
    private final TokenUtil tokenUtil;

    @Transactional(readOnly = true)
    public PageResponse<CostumeResponse> getCostumeList(Pageable pageable) {
        return costumeRepository.getCostumeList(pageable);
    }

    @Transactional(readOnly = true)
    public CostumeResponse getCostume(Long costumeId) {
        return costumeRepository.getCostume(costumeId);
    }

    @Transactional
    public void purchaseCostume(CostumePurchaseRequest request) {
        User user = userRepository.findById(tokenUtil.getUserId()).orElseThrow(() -> new NotFoundException(ErrorCode.USER_NOT_FOUND));
        costumeRepository.findById(request.costumeId()).orElseThrow(() -> new NotFoundException(ErrorCode.COSTUME_NOT_FOUND));
        userCostumeRepository.findCostumeIdByUserId(user.getId(), request.costumeId()).ifPresent( userCostume -> { throw new CostumeAlreadyException(); });
        if(userRepository.findPointByUserId(user.getId()).compareTo(request.point()) < 0){ throw new PointNotEnoughException(); }
        user.subtractPoint(request.point());
        UserCostume userCostume = UserCostume.builder().userId(user.getId()).costumeId(request.costumeId()).build();
        userCostumeRepository.save(userCostume);
    }
}
