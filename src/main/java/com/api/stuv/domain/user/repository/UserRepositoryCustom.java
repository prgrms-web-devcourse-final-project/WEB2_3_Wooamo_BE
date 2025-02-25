package com.api.stuv.domain.user.repository;

import com.api.stuv.domain.user.dto.response.UserInformationResponse;
import com.api.stuv.domain.user.dto.response.MyInformationResponse;
import org.springframework.data.repository.query.Param;

public interface UserRepositoryCustom {
    UserInformationResponse getUserInformation(Long userId, Long myId);
    MyInformationResponse getUserByMyId(@Param("myId") Long myId);
}
