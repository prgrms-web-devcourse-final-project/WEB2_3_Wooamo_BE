package com.api.stuv.domain.timer.repository;

import com.api.stuv.domain.timer.entity.StudyTime;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StudyTimeRepository extends JpaRepository<StudyTime, Long>, StudyTimeRepositoryCustom {

    @Query("SELECT studyTime FROM StudyTime WHERE userId = :userId AND studyDate = CURRENT DATE")
    List<Long> findStudyTimeByUserIdAndStudyDate(Long userId);
}
