package com.api.stuv.domain.user.repository;

import com.api.stuv.domain.user.dto.*;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public interface UserRepositoryCustom {
    UserInformationDTO getUserInformation(Long userId, Long myId, Long friends);
    MyInformationDTO getUserByMyId(@Param("myId") Long myId, @Param("friends") Long friends);
    List<UserBoardListDTO> getUserBoardList(@Param("userId") Long userId);
    List<ImageUrlDTO> getUserCostume(@Param("userId") Long userId);
    ImageUrlDTO getCostumeInfoByUserId(Long userId);
    List<UserProfileInfoDTO> findUserInfoByIds(List<Long> userIds);
    Long countNewUserByWeekend(LocalDateTime startDate, LocalDateTime endDate);
}
