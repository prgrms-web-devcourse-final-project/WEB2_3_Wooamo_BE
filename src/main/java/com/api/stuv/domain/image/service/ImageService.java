package com.api.stuv.domain.image.service;

import org.springframework.stereotype.Service;
import java.util.Set;

@Service
public class ImageService {
    private static final Set<String> approveExtensions = Set.of("jpg", "jpeg", "png", "gif");
    public boolean isValidImageExtension(String extension) {
        return approveExtensions.contains(extension.toLowerCase());
    }
}
