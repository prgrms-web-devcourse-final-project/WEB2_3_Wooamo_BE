package com.api.stuv.domain.user.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.math.BigDecimal;

@Getter
@RequiredArgsConstructor
public enum RewardType {
    RANK_1(BigDecimal.valueOf(100)),
    RANK_2(BigDecimal.valueOf(50)),
    RANK_3(BigDecimal.valueOf(30)),
    DAILY(BigDecimal.valueOf(3)),
    PARTY(BigDecimal.valueOf(5)),
    ACCEPT(BigDecimal.valueOf(5)),
    ;

    private final BigDecimal value;
}
