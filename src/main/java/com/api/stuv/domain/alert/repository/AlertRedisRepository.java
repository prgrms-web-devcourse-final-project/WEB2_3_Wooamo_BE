package com.api.stuv.domain.alert.repository;

import com.api.stuv.domain.alert.entity.Alert;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AlertRedisRepository extends CrudRepository<Alert, Long> {
}
