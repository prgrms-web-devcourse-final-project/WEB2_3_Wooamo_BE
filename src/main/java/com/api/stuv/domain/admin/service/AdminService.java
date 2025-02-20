package com.api.stuv.domain.admin.service;

import com.api.stuv.domain.admin.dto.CreateCostumeRequest;
import com.api.stuv.domain.image.entity.ImageFile;
import com.api.stuv.domain.image.entity.ImageType;
import com.api.stuv.domain.image.exception.InvalidImageFileFormat;
import com.api.stuv.domain.image.repository.ImageFileRepository;
import com.api.stuv.domain.image.service.ImageService;
import com.api.stuv.domain.image.service.S3ImageService;
import com.api.stuv.domain.image.util.FileUtils;
import com.api.stuv.domain.shop.entity.Costume;
import com.api.stuv.domain.shop.repository.CostumeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class AdminService {

    private final CostumeRepository costumeRepository;
    private final ImageFileRepository imageFileRepository;
    private final S3ImageService s3ImageService;
    private final ImageService imageService;

    @Transactional
    public void createCostume(CreateCostumeRequest request, MultipartFile file) {
        Costume costume = Costume.builder()
                .costumeName(request.costumeName())
                .point(request.point())
                .build();
        costumeRepository.save(costume);
        handleImage(costume, file);
    }

    public void handleImage(Costume costume, MultipartFile file) {
        String extension = FileUtils.getExtension(file);
        if(!imageService.isValidImageExtension(extension)) {throw new InvalidImageFileFormat();}
        String newFileName = FileUtils.generateNewFilename();
        String fullFileName = newFileName + "." + extension;
        s3ImageService.uploadImageFile(file, ImageType.COSTUME, costume.getId(), fullFileName);

        ImageFile imageFile = ImageFile.builder()
                .imageType(ImageType.COSTUME)
                .originFilename(file.getOriginalFilename())
                .newFilename(fullFileName)
                .build();

        imageFileRepository.save(imageFile);
        costume.updateImageFile(imageFile.getId());
    }
}
