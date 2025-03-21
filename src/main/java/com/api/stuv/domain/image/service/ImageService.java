package com.api.stuv.domain.image.service;

import com.api.stuv.domain.image.entity.EntityType;
import com.api.stuv.domain.image.entity.ImageFile;
import com.api.stuv.domain.image.exception.InvalidImageFileFormat;
import com.api.stuv.domain.image.repository.ImageFileRepository;
import com.api.stuv.domain.image.util.FileUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.Set;

@Service
@RequiredArgsConstructor
public class ImageService {

    private static final Set<String> approveExtensions = Set.of("jpg", "jpeg", "png", "gif", "webp");
    private final S3ImageService s3ImageService;
    private final ImageFileRepository imageFileRepository;

    public void handleImage(Long entityId, MultipartFile file, EntityType entityType) {
        String fullFileName = getFileName(file);
        ImageFile imageFile = ImageFile.createImageFile(file.getOriginalFilename(), fullFileName, entityId, entityType);
        imageFileRepository.save(imageFile);
        s3ImageService.uploadImageFile(file, entityType, entityId, fullFileName);
    }

    public String getFileName(MultipartFile file) {
        String extension = FileUtils.getExtension(file);
        if(!isValidImageExtension(extension)) {throw new InvalidImageFileFormat();}
        return FileUtils.generateNewFilename() + "." + extension;
    }

    public boolean isValidImageExtension(String extension) {
        return approveExtensions.contains(extension.toLowerCase());
    }
}
