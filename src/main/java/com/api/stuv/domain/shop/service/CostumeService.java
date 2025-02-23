package com.api.stuv.domain.shop.service;

import com.api.stuv.domain.shop.dto.CostumeResponse;
import com.api.stuv.domain.shop.repository.CostumeRepository;
import com.api.stuv.global.response.PageResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CostumeService {

    private final CostumeRepository costumeRepository;

    @Transactional(readOnly = true)
    public PageResponse<CostumeResponse> getCostumeList(Pageable pageable) {
        return costumeRepository.getCostumeList(pageable);
    }

    @Transactional(readOnly = true)
    public CostumeResponse getCostume(Long costumeId) {
        return costumeRepository.getCostume(costumeId);
    }
}
