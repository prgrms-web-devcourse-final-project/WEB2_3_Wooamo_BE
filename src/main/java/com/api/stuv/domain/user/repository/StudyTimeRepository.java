package com.api.stuv.domain.user.repository;

import com.api.stuv.domain.user.entity.StudyTime;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StudyTimeRepository extends JpaRepository<StudyTime, Long> {
}
