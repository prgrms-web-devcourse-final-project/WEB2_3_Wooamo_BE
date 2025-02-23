package com.api.stuv.domain.shop.entity;

import com.api.stuv.global.entity.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Entity
@Table(name = "costumes")
public class Costume extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long imagefileId;

    @Column(nullable = false)
    private String costumeName;

    @Column(precision = 10, nullable = false)
    private BigDecimal point;

    public Costume(String costumeName, BigDecimal point) {
        this.costumeName = costumeName;
        this.point = point;
    }

    public static Costume createCostumeContents(String costumeName, BigDecimal point) {
        return new Costume(costumeName, point);
    }

    public void modifyCostumeContents(String costumeName, BigDecimal point) {
        this.costumeName = costumeName;
        this.point = point;
    }

    public void updateImageFile(Long imagefileId) {
        this.imagefileId = imagefileId;
    }
}