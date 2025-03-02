package com.api.stuv.domain.user.repository;

import com.api.stuv.domain.user.entity.PointHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;

@Repository
public interface PointHistoryRepository extends JpaRepository<PointHistory, Long>, PointHistoryRepositoryCustom {

    @Query("SELECT p FROM PointHistory p WHERE p.userId = :userId AND p.updatedAt >= :startOfDay AND p.updatedAt < :endOfDay AND p.transactionType = 'PERSONAL'")
    PointHistory findByUserIdAndTransactionType(@Param("userId") Long userId,
                                                @Param("startOfDay") LocalDateTime startOfDay,
                                                @Param("endOfDay") LocalDateTime endOfDay);
}
