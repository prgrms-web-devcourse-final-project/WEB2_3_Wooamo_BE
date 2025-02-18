package com.api.stuv.domain.image.entity;

import com.api.stuv.global.entity.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "images_file")
public class ImageFile extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long entityId; // Entity(costume, post, confirm, party) id 연결

    private Long userId;

    @Column(nullable = false)
    private String originFilename;

    @Column(nullable = false)
    private String newFilename;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ImageType category;

    @Builder
    public ImageFile(Long entityId, Long userId, String originFilename, String newFilename, ImageType category) {
        this.entityId = entityId;
        this.userId = userId;
        this.originFilename = originFilename;
        this.newFilename = newFilename;
        this.category = category;
    }
}