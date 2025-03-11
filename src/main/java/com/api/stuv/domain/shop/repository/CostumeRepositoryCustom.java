package com.api.stuv.domain.shop.repository;

import com.api.stuv.domain.image.dto.ImageDTO;
import com.api.stuv.domain.shop.dto.costume.CostumeDTO;

import java.util.List;
import java.util.Optional;

public interface CostumeRepositoryCustom {
    ImageDTO findCostumeByBestSales();
    List<CostumeDTO> getCostumeList();
    Optional<CostumeDTO> getCostume(Long id);
}
