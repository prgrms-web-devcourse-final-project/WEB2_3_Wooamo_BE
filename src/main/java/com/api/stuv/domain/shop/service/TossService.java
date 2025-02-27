package com.api.stuv.domain.shop.service;

import com.api.stuv.domain.auth.util.TokenUtil;
import com.api.stuv.domain.shop.dto.TossPaymentRequest;
import com.api.stuv.domain.shop.dto.TossPaymentResponse;
import com.api.stuv.domain.shop.entity.Payment;
import com.api.stuv.domain.shop.repository.PaymentRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class TossService {

    private final PaymentRepository paymentRepository;
    private final TokenUtil tokenUtil;
    public static final String TOSS_URL = "https://api.tosspayments.com/v1/payments/";

    @Value("${payment.toss.test_secret_api_key}")
    private String secreteKey;

    public TossService(PaymentRepository paymentRepository, TokenUtil tokenUtil) {
        this.paymentRepository = paymentRepository;
        this.tokenUtil = tokenUtil;
    }

    public TossPaymentResponse requestPayments(TossPaymentRequest request) {
        String orderId = UUID.randomUUID().toString().substring(0,10).replace("-","");
        Payment payment = Payment.builder()
                .orderId(orderId)
                .userId(tokenUtil.getUserId())
                .amount(request.amount())
                .point(request.point())
                .build();
        paymentRepository.save(payment);
        return new TossPaymentResponse(payment.getOrderId(), payment.getAmount(), payment.getPoint());
    }
}