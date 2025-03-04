package com.api.stuv.domain.shop.repository;

import com.api.stuv.domain.image.dto.ImageDTO;
import com.api.stuv.domain.shop.dto.costume.CostumeDTO;
import com.api.stuv.global.response.PageResponse;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface CostumeRepositoryCustom {
    ImageDTO findCostumeByBestSales();
    PageResponse<CostumeDTO> getCostumeList(Pageable pageable);
    Optional<CostumeDTO> getCostume(Long id);
}
