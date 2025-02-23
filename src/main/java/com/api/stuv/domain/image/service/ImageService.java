package com.api.stuv.domain.image.service;

import com.api.stuv.domain.image.exception.InvalidImageFileFormat;
import com.api.stuv.domain.image.util.FileUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class ImageService {

    private static final Set<String> approveExtensions = Set.of("jpg", "jpeg", "png", "gif");

    public String getFileName(MultipartFile file) {
        String extension = FileUtils.getExtension(file);
        if(!isValidImageExtension(extension)) {throw new InvalidImageFileFormat();}
        return FileUtils.generateNewFilename() + "." + extension;
    }

    public boolean isValidImageExtension(String extension) {
        return approveExtensions.contains(extension.toLowerCase());
    }
}
