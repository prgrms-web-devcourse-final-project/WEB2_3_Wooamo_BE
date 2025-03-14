package com.api.stuv.domain.shop.controller;

import com.api.stuv.domain.shop.dto.costume.CostumePurchaseRequest;
import com.api.stuv.domain.shop.dto.costume.CostumeRandomRequest;
import com.api.stuv.domain.shop.dto.costume.CostumeRandomResponse;
import com.api.stuv.domain.shop.dto.costume.CostumeResponse;
import com.api.stuv.domain.shop.dto.payments.PaymentConfirmRequest;
import com.api.stuv.domain.shop.dto.payments.PaymentRequest;
import com.api.stuv.domain.shop.dto.payments.PaymentResponse;
import com.api.stuv.domain.shop.service.CostumeService;
import com.api.stuv.domain.shop.service.PaymentService;
import com.api.stuv.global.response.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class ShopController {

    private final CostumeService costumeService;
    private final PaymentService paymentService;

    @GetMapping(value = "/costume")
    public ResponseEntity<ApiResponse<List<CostumeResponse>>> listCostumes () {
        return ResponseEntity.ok().body(ApiResponse.success(
                costumeService.getCostumeList()));
    }

    @GetMapping(value = "/costume/{costumeId}")
    public ResponseEntity<ApiResponse<CostumeResponse>> getCostume(
            @PathVariable long costumeId
    ) {
        return ResponseEntity.ok().body(ApiResponse.success(
                costumeService.getCostume(costumeId)));
    }

    @PostMapping(value = "/costume")
    public ResponseEntity<ApiResponse<Void>> addUserCostume(
            @RequestBody @Valid CostumePurchaseRequest costumePurchaseRequest
    ){
        costumeService.purchaseCostume(costumePurchaseRequest);
        return ResponseEntity.ok().body(ApiResponse.success());
    }

    @PostMapping(value = "/costume/random")
    public ResponseEntity<ApiResponse<CostumeRandomResponse>> addRandomCostume(
            @RequestBody @Valid CostumeRandomRequest costumeRandomRequest
    ){
        CostumeRandomResponse response = costumeService.purchaseRandomCostume(costumeRandomRequest.point());
        return ResponseEntity.ok().body(ApiResponse.success(response));
    }

    @PostMapping(value = "/payments")
    public ResponseEntity<ApiResponse<PaymentResponse>> requestPayments (
            @RequestBody @Valid PaymentRequest paymentRequest
    ) {
        PaymentResponse response = paymentService.requestPayments(paymentRequest);
        return ResponseEntity.ok().body(ApiResponse.success(response));
    }

    @PostMapping(value = "/payments/confirm")
    public ResponseEntity<ApiResponse<Void>> confirmPayments (
            @RequestBody @Valid PaymentConfirmRequest paymentConfirmRequestRequest
    ) throws IOException, InterruptedException {
        paymentService.requestConfirm(paymentConfirmRequestRequest);
        return ResponseEntity.ok().body(ApiResponse.success());
    }
}
