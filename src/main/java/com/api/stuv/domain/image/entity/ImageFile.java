package com.api.stuv.domain.image.entity;

import com.api.stuv.global.entity.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
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

    @Column(nullable = false)
    private String originFilename;

    @Column(nullable = false)
    private String newFilename;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ImageType imageType;

    public ImageFile(String originFilename, String newFilename, ImageType imageType) {
        this.originFilename = originFilename;
        this.newFilename = newFilename;
        this.imageType = imageType;
    }

    public static ImageFile createImageFile(String originFilename, String newFilename, ImageType imageType) {
        return new ImageFile(originFilename, newFilename, imageType);
    }
}