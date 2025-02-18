package com.api.stuv.domain.party.repository;

import com.api.stuv.domain.party.entity.QuestConfirm;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface QuestConfirmRepository extends JpaRepository<QuestConfirm, Long> {
}
