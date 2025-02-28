package com.api.stuv.domain.shop.repository;

import com.api.stuv.domain.shop.entity.Payment;
import jakarta.validation.constraints.NotBlank;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {
    Payment findByOrderId(@NotBlank(message = "고유 주문 번호를 입력해주세요") String s);
}
