package com.api.stuv.domain.admin.service;

import com.api.stuv.domain.admin.dto.CreateCostumeRequest;
import com.api.stuv.domain.admin.exception.InvalidPointFormat;
import com.api.stuv.domain.image.entity.ImageFile;
import com.api.stuv.domain.image.entity.ImageType;
import com.api.stuv.domain.image.exception.ImageFileNameNotFound;
import com.api.stuv.domain.image.exception.ImageFileNotFound;
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

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class AdminService {

    private final CostumeRepository costumeRepository;
    private final ImageFileRepository imageFileRepository;
    private final S3ImageService s3ImageService;
    private final ImageService imageService;

    @Transactional
    public void createCostume(CreateCostumeRequest request, MultipartFile file) {
        if(file == null || file.isEmpty()) {throw new ImageFileNotFound();}
        if(request.point().compareTo(BigDecimal.ZERO) < 0) {throw new InvalidPointFormat();}
        Costume costume = Costume.createCostumeContents(request.costumeName(), request.point());
        costumeRepository.save(costume);
        handleImage(costume, file);
    }

    public void deleteCostume(Long costumeId) {
        // todo : 커스튬 예외 develop 병합 시, 가져오기
        Costume costume = costumeRepository.findById(costumeId).orElseThrow();
        ImageFile imageFile = imageFileRepository.findById(costume.getImagefileId()).orElseThrow(ImageFileNameNotFound::new);
        s3ImageService.deleteImageFile(ImageType.COSTUME, costume.getImagefileId(), imageFile.getNewFilename());
        imageFileRepository.deleteById(costume.getImagefileId());
        costumeRepository.delete(costume);
    }

    public void handleImage(Costume costume, MultipartFile file) {
        String extension = FileUtils.getExtension(file);
        if(!imageService.isValidImageExtension(extension)) {throw new InvalidImageFileFormat();}
        String newFileName = FileUtils.generateNewFilename();
        String fullFileName = newFileName + "." + extension;
        s3ImageService.uploadImageFile(file, ImageType.COSTUME, costume.getId(), fullFileName);
        ImageFile imageFile = ImageFile.createImageFile(file.getOriginalFilename(), fullFileName, ImageType.COSTUME);

        imageFileRepository.save(imageFile);
        costume.updateImageFile(imageFile.getId());
    }
}
