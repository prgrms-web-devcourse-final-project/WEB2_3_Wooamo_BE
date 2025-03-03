package com.api.stuv.domain.shop.service;

import com.api.stuv.domain.auth.util.TokenUtil;
import com.api.stuv.domain.shop.dto.PaymentConfirmRequest;
import com.api.stuv.domain.shop.dto.PaymentRequest;
import com.api.stuv.domain.shop.dto.PaymentResponse;
import com.api.stuv.domain.shop.entity.Payment;
import com.api.stuv.domain.shop.exception.PaymentsMismatchException;
import com.api.stuv.domain.shop.exception.PaymentsNotFoundException;
import com.api.stuv.domain.shop.repository.PaymentRepository;
import com.api.stuv.domain.user.entity.HistoryType;
import com.api.stuv.domain.user.entity.PointHistory;
import com.api.stuv.domain.user.entity.User;
import com.api.stuv.domain.user.repository.PointHistoryRepository;
import com.api.stuv.domain.user.repository.UserRepository;
import com.api.stuv.global.exception.ErrorCode;
import com.api.stuv.global.exception.NotFoundException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

@Service
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final TokenUtil tokenUtil;
    public static final String TOSS_URL = "https://api.tosspayments.com/v1/payments/";
    private final UserRepository userRepository;
    private static final Logger log = LoggerFactory.getLogger(PaymentService.class);
    private static final HttpClient httpClient = HttpClient.newHttpClient();
    private final PointHistoryRepository pointHistoryRepository;

    @Value("${payment.toss.test_secret_api_key}")
    private String secretKey;

    public PaymentService(PaymentRepository paymentRepository, TokenUtil tokenUtil, UserRepository userRepository, PointHistoryRepository pointHistoryRepository) {
        this.paymentRepository = paymentRepository;
        this.tokenUtil = tokenUtil;
        this.userRepository = userRepository;
        this.pointHistoryRepository = pointHistoryRepository;
    }

    @Transactional
    public PaymentResponse requestPayments(PaymentRequest request) {
        String orderId = UUID.randomUUID().toString();
        Payment payment = Payment.builder()
                .orderId(orderId)
                .userId(tokenUtil.getUserId())
                .amount(request.amount())
                .point(request.point())
                .isPaymentSuccess(false)
                .build();
        paymentRepository.save(payment);
        return new PaymentResponse(payment.getOrderId(), payment.getAmount(), payment.getPoint());
    }

    @Transactional
    public void requestConfirm(PaymentConfirmRequest request) throws IOException, InterruptedException {
        Payment payment = paymentRepository.findByOrderId(request.orderId());
        User user = userRepository.findById(tokenUtil.getUserId()).orElseThrow(() -> new NotFoundException(ErrorCode.USER_NOT_FOUND));

        if(!request.amount().equals(payment.getAmount())) { throw new PaymentsMismatchException();}
        HttpResponse<String> response = requestConfirmPayments(request);

        if(response.statusCode() == 200){
            payment.setPaymentSuccess();
            payment.setPaymentKey(request.paymentKey());
            user.updatePoint(request.point());
            pointHistoryRepository.save(new PointHistory(user.getId(), HistoryType.CHARGE, request.point(), HistoryType.CHARGE.getText()));
        } else {
            log.error("Payment confirmation failed : {}", response.body());
            throw new PaymentsNotFoundException();
        }
    }

    public HttpResponse<String> requestConfirmPayments(PaymentConfirmRequest request) throws IOException, InterruptedException{
        ObjectMapper objectMapper = new ObjectMapper();
        Map<String, Object> requestBody = new LinkedHashMap<>();
        requestBody.put("paymentKey", request.paymentKey());
        requestBody.put("amount", request.amount());
        requestBody.put("orderId", request.orderId());
        HttpRequest httpRequest = HttpRequest.newBuilder()
                .uri(URI.create(TOSS_URL + "confirm"))
                .header("Authorization", getAuthorization())
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(objectMapper.writeValueAsString(requestBody)))
                .build();
        return httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());
    }

    public void requestCancelPayments(String paymentKey, String cancelReason) throws IOException, InterruptedException{
        HttpRequest httpRequest = HttpRequest.newBuilder()
                .uri(URI.create(TOSS_URL + paymentKey + "/cancel"))
                .header("Authorization", getAuthorization())
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString("{\"cancelReason\":\"" + cancelReason + "\"}"))
                .build();
        httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());
    }

    public String getAuthorization() {
        return "Basic " + Base64.getEncoder().encodeToString((secretKey + ":").getBytes(StandardCharsets.UTF_8));
    }
}