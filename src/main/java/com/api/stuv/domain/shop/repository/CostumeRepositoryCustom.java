package com.api.stuv.domain.shop.repository;

import com.api.stuv.domain.image.dto.ImageDTO;
import com.api.stuv.domain.shop.dto.CostumeResponse;
import com.api.stuv.global.response.PageResponse;
import org.springframework.data.domain.Pageable;

public interface CostumeRepositoryCustom {
    PageResponse<CostumeResponse> getCostumeList(Pageable pageable);
    CostumeResponse getCostume(Long id);
    ImageDTO findCostumeByBestSales();
}
