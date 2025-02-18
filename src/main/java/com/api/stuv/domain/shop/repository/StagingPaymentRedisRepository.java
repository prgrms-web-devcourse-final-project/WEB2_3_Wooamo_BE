package com.api.stuv.domain.shop.repository;

import com.api.stuv.domain.shop.entity.StagingPayment;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StagingPaymentRedisRepository extends CrudRepository<StagingPayment, String> {
}
