package com.api.stuv.domain.user.repository;

import com.api.stuv.domain.user.dto.response.UserBoardListResponse;
import com.api.stuv.domain.user.dto.response.UserInformationResponse;
import com.api.stuv.domain.user.dto.response.MyInformationResponse;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface UserRepositoryCustom {
    UserInformationResponse getUserInformation(Long userId, Long myId);
    MyInformationResponse getUserByMyId(@Param("myId") Long myId);
    List<UserBoardListResponse> getUserBoardList(@Param("userId") Long userId);
}
