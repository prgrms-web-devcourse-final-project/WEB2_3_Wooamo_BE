package com.api.stuv.domain.timer.repository;

import com.api.stuv.domain.timer.entity.Timer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TimerRepository extends JpaRepository<Timer, Long> {
    Optional<Timer> findByIdAndUserId(Long id, Long userId);
}
