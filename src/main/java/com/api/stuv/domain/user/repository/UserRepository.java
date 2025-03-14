package com.api.stuv.domain.user.repository;

import com.api.stuv.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long>, UserRepositoryCustom {

    Boolean existsByEmail(String email);

    Boolean existsByNickname(String nickname);

    @Query("SELECT COUNT(*) FROM User u WHERE u.id IN :idList")
    int isDuplicateIds(@Param("idList") List<Long> idList);

    User findByEmail(String email);

    Optional<User> findBySocialId(Long socialId);

    Optional<User> findById(Long userId);

    @Query("SELECT u.nickname FROM User u WHERE u.id = :userId")
    String findNicknameByUserId(@Param("userId") Long userId);

}
