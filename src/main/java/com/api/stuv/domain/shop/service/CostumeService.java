package com.api.stuv.domain.shop.service;

import com.api.stuv.domain.admin.exception.CostumeNotFound;
import com.api.stuv.domain.auth.util.TokenUtil;
import com.api.stuv.domain.image.entity.EntityType;
import com.api.stuv.domain.image.entity.ImageFile;
import com.api.stuv.domain.image.exception.ImageFileNotFound;
import com.api.stuv.domain.image.repository.ImageFileRepository;
import com.api.stuv.domain.image.service.S3ImageService;
import com.api.stuv.domain.shop.dto.CostumePurchaseRequest;
import com.api.stuv.domain.shop.dto.CostumeRandomResponse;
import com.api.stuv.domain.shop.dto.CostumeResponse;
import com.api.stuv.domain.shop.entity.Costume;
import com.api.stuv.domain.shop.exception.CostumeAlreadyException;
import com.api.stuv.domain.shop.exception.CostumeNotPurchaseException;
import com.api.stuv.domain.shop.repository.CostumeRepository;
import com.api.stuv.domain.user.entity.HistoryType;
import com.api.stuv.domain.user.entity.PointHistory;
import com.api.stuv.domain.user.entity.User;
import com.api.stuv.domain.user.entity.UserCostume;
import com.api.stuv.domain.user.repository.PointHistoryRepository;
import com.api.stuv.domain.user.repository.UserCostumeRepository;
import com.api.stuv.domain.user.repository.UserRepository;
import com.api.stuv.global.exception.ErrorCode;
import com.api.stuv.global.exception.NotFoundException;
import com.api.stuv.global.response.PageResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class CostumeService {

    private final CostumeRepository costumeRepository;
    private final UserCostumeRepository userCostumeRepository;
    private final UserRepository userRepository;
    private final TokenUtil tokenUtil;
    private final S3ImageService s3ImageService;
    private final ImageFileRepository imageFileRepository;
    private final PointHistoryRepository pointHistoryRepository;

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
        user.subtractPoint(request.point());
        userCostumeRepository.save(new UserCostume(user.getId(), request.costumeId()));
        pointHistoryRepository.save(new PointHistory(user.getId(), HistoryType.CONSUME, request.point(), HistoryType.CONSUME.getText()));
    }

    @Transactional
    public CostumeRandomResponse purchaseRandomCostume(BigDecimal point){
        User user = userRepository.findById(tokenUtil.getUserId()).orElseThrow(() -> new NotFoundException(ErrorCode.USER_NOT_FOUND));
        List<Long> userPurchasedCostume = userCostumeRepository.findCostumeIdListByUserId(user.getId());
        List<Long> availableCostume = costumeRepository.findAll().stream()
                        .map(Costume::getId)
                        .filter(costumeId -> !userPurchasedCostume.contains(costumeId)).toList();
        if (availableCostume.isEmpty()) { throw new CostumeNotPurchaseException();}
        Long randomCostumeId = availableCostume.get(new Random().nextInt(availableCostume.size()));
        user.subtractPoint(point);
        userCostumeRepository.save(new UserCostume(user.getId(), randomCostumeId));
        pointHistoryRepository.save(new PointHistory(user.getId(), HistoryType.CONSUME, point, HistoryType.CONSUME.getText()));
        ImageFile imageFile = imageFileRepository.findByEntityIdAndEntityType(randomCostumeId, EntityType.COSTUME).orElseThrow(ImageFileNotFound::new);
        Costume costume = costumeRepository.findById(randomCostumeId).orElseThrow(CostumeNotFound::new);
        return new CostumeRandomResponse(s3ImageService.generateImageFile(EntityType.COSTUME, randomCostumeId, imageFile.getNewFilename()), costume.getCostumeName());
    }
}
