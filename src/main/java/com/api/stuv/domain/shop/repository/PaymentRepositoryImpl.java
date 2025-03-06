package com.api.stuv.domain.shop.repository;

import com.api.stuv.domain.admin.dto.response.PointSalesResponse;
import com.api.stuv.domain.shop.entity.QPayment;
import com.api.stuv.domain.user.entity.QUser;
import com.api.stuv.global.util.common.TemplateUtils;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor
public class PaymentRepositoryImpl implements PaymentRepositoryCustom {

    private final JPAQueryFactory factory;
    private final QPayment p = QPayment.payment;
    private final QUser u = QUser.user;

    @Override
    public List<PointSalesResponse> findPointSalesList() {
        return factory
                .select(Projections.constructor(
                        PointSalesResponse.class,
                        TemplateUtils.timeFormater(p.createdAt),
                        u.nickname,
                        p.amount,
                        p.point
                ))
                .from(p)
                .leftJoin(u).on(p.userId.eq(u.id))
                .where(p.isPaymentSuccess.eq(true))
                .orderBy(p.createdAt.desc())
                .limit(10)
                .fetch();
    }
}
