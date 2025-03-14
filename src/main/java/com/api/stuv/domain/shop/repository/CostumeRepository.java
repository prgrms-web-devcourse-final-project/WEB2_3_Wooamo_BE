package com.api.stuv.domain.shop.repository;

import com.api.stuv.domain.shop.entity.Costume;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CostumeRepository extends JpaRepository<Costume, Long>, CostumeRepositoryCustom {
}
