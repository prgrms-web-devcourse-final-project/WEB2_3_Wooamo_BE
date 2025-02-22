package com.api.stuv.domain.shop.repository;

import com.api.stuv.domain.shop.dto.CostumeListResponse;
import com.api.stuv.global.response.PageResponse;
import org.springframework.data.domain.Pageable;

public interface CostumeRepositoryCustom {
    PageResponse<CostumeListResponse> getCostumeList(Pageable pageable);
}
