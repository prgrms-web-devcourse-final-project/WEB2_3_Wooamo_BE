package com.api.stuv.domain.image.service;

import com.api.stuv.domain.image.entity.ImageType;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.IOException;

@Service
@RequiredArgsConstructor
public class S3ImageService {
    private final S3Client s3Client;

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    public void uploadImageFile(MultipartFile file, ImageType imageType, Long id, String fileName) {
        try{
            PutObjectRequest request = PutObjectRequest.builder()
                    .bucket(bucket)
                    .key(getKey(imageType, id, fileName)) // s3 내부 저장 경로 설정
                    .contentType(file.getContentType()) // 파일 타입
                    .build();
            s3Client.putObject(request, RequestBody.fromInputStream(file.getInputStream(), file.getSize())); // s3에 파일 업로드
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static String getKey(ImageType imageType, Long id, String fileName) {
        return String.format("%s/%d/%s", imageType.getPath(), id, fileName);
    }
}
