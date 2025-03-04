package com.api.stuv.domain.party.repository.confirm;

import com.api.stuv.domain.party.entity.QuestConfirm;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;

@Repository
public interface QuestConfirmRepository extends JpaRepository<QuestConfirm, Long>, QuestConfirmRepositoryCustom {
    boolean existsByIdAndConfirmDate(Long id, LocalDate confirmDate);
}
