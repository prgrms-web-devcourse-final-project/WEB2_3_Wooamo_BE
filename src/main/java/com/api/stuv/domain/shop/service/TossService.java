package com.api.stuv.domain.shop.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class TossService {

    @Value("${payment.toss.test_secret_api_key}")
    private String secreteKey;

    public static final String TOSS_URL = "https://api.tosspayments.com/v1/payments/";


}