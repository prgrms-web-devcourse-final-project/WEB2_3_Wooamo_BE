package com.api.stuv.domain.shop.repository;

import com.api.stuv.domain.admin.dto.response.PointSalesResponse;

import java.util.List;

public interface PaymentRepositoryCustom {
    List<PointSalesResponse> findPointSalesList();
}
