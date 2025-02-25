package com.api.stuv.domain.user.entity;

import com.api.stuv.domain.user.dto.request.ModifyProfileRequest;
import com.api.stuv.global.entity.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Getter
@Entity
@Table(name = "users")
@NoArgsConstructor(access = AccessLevel.PROTECTED) // 기본 생성자 protected
public class User extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // AUTO_INCREMENT 적용
    private Long id;

    @Column(nullable = false, unique = true)
    private String email;

    private String password;

    private Long socialId;

    @Column(nullable = false)
    private Long costumeId;

    @Column(nullable = false, unique = true)
    private String nickname;

    @Column(precision = 10, nullable = false)
    private BigDecimal point;

    @Column(columnDefinition = "TEXT")
    private String context;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private RoleType role;

    private String blogLink;

    @Builder
    public User(Long id, String email, String password, Long socialId, Long costumeId, String nickname, BigDecimal point, String context, RoleType role, String blogLink) {
        this.id = id;
        this.email = email;
        this.password = password;
        this.socialId = socialId;
        this.costumeId = costumeId;
        this.nickname = nickname;
        this.point = point != null ? point : BigDecimal.ZERO;
        this.context = context;
        this.role = role;
        this.blogLink = blogLink;
    }

    public void modifyProfileRequest(String context, String link) {
        this.context = context;
        this.blogLink = link;
    }
    
    public void updatePoint(BigDecimal point) {
        this.point = this.point.add(point);
    }
}
