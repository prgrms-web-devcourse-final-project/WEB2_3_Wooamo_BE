package com.api.stuv.domain.user.repository;

import com.api.stuv.domain.user.dto.response.UserInformationResponse;

public interface UserRepositoryCustom {
    String getUserProfile(Long userCostumeId);
    UserInformationResponse getUserInformation(Long userId, Long myId);
}
