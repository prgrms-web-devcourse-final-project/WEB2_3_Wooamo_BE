package com.api.stuv.domain.user.repository;

import com.api.stuv.domain.user.dto.response.MyInformationResponse;
import org.springframework.data.repository.query.Param;

public interface UserRepositoryCustom {
    String getUserProfile(Long userCostumeId);

    MyInformationResponse getUserByMyId(@Param("myId") Long myId);
}
