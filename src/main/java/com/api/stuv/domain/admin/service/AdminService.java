package com.api.stuv.domain.admin.service;

import com.api.stuv.domain.admin.dto.CostumeRequest;
import com.api.stuv.domain.admin.exception.CostumeNotFound;
import com.api.stuv.domain.admin.exception.InvalidPointFormat;
import com.api.stuv.domain.image.entity.EntityType;
import com.api.stuv.domain.image.entity.ImageFile;
import com.api.stuv.domain.image.exception.ImageFileNameNotFound;
import com.api.stuv.domain.image.exception.ImageFileNotFound;
import com.api.stuv.domain.image.repository.ImageFileRepository;
import com.api.stuv.domain.image.service.ImageService;
import com.api.stuv.domain.image.service.S3ImageService;
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
    public void createCostume(CostumeRequest request, MultipartFile file) {
        if(file == null || file.isEmpty()) {throw new ImageFileNotFound();}
        if(request.point().compareTo(BigDecimal.ZERO) < 0) {throw new InvalidPointFormat();}
        Costume costume = Costume.createCostumeContents(request.costumeName(), request.point());
        costumeRepository.save(costume);
        imageService.handleImage(costume.getId(), file, EntityType.COSTUME);
    }

    @Transactional
    public void modifyCostume(long costumeId, CostumeRequest request){
        Costume costume = costumeRepository.findById(costumeId).orElseThrow(CostumeNotFound::new);
        if(request.point().compareTo(BigDecimal.ZERO) < 0) {throw new InvalidPointFormat();}
        costume.modifyCostumeContents(request.costumeName(), request.point());
        costumeRepository.save(costume);
    }

    public void deleteCostume(Long costumeId) {
        Costume costume = costumeRepository.findById(costumeId).orElseThrow(CostumeNotFound::new);
        ImageFile imageFile = imageFileRepository.findByEntityIdAndEntityType(costumeId, EntityType.COSTUME).orElseThrow(ImageFileNotFound::new);
        s3ImageService.deleteImageFile(EntityType.COSTUME, costumeId, imageFile.getNewFilename());
        imageFileRepository.deleteById(costumeId);
        costumeRepository.delete(costume);
    }
}
