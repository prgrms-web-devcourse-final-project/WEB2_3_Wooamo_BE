package com.api.stuv.domain.party.repository;

import com.api.stuv.domain.party.entity.PartyGroup;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PartyGroupRepository extends JpaRepository<PartyGroup, Long>, PartyGroupRepositoryCustom {
}
