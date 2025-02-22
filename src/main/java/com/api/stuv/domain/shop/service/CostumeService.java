package com.api.stuv.domain.shop.service;

import com.api.stuv.domain.shop.dto.CostumeListResponse;
import com.api.stuv.domain.shop.repository.CostumeRepository;
import com.api.stuv.global.response.PageResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CostumeService {

    private final CostumeRepository costumeRepository;

    public PageResponse<CostumeListResponse> getCostumeList(Pageable pageable) {
        return costumeRepository.getCostumeList(pageable);
    }
}
