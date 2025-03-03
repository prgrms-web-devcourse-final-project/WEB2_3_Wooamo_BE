package com.api.stuv.domain.user.repository;

import com.api.stuv.domain.user.entity.UserCostume;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserCostumeRepository extends JpaRepository<UserCostume, Long> {
    @Query("SELECT uc.costumeId FROM UserCostume uc WHERE uc.userId = :userId AND uc.costumeId = :costumeId")
    Optional<Long> findCostumeIdByUserId(Long userId, Long costumeId);

    @Query("SELECT uc.costumeId FROM UserCostume uc WHERE uc.userId = :userId")
    List<Long> findCostumeIdListByUserId(Long userId);

    @Query("SELECT id FROM UserCostume WHERE costumeId = :costumeId AND userId = :userId")
    Long findIdByCostumeId(Long costumeId, Long userId);
}
