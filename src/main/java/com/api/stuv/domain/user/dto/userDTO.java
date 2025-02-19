package com.api.stuv.domain.user.dto;

import com.api.stuv.domain.user.entity.RoleType;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Setter
@Getter
public class userDTO {
    private String email;
    private String password;
    private String nickname;

    private Long costumeId;
    private RoleType role;
    private String socialId;
    private BigDecimal point;
}
