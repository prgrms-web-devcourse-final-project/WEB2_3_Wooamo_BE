package com.api.stuv.domain.user.repository;

import com.api.stuv.domain.user.entity.Timer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TimerRepository extends JpaRepository<Timer, Long> {
}
