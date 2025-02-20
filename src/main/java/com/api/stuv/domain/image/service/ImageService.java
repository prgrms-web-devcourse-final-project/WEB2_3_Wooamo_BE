package com.api.stuv.domain.image.service;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ImageService {
    public boolean isValidImageExtension(String extension) {
        List<String> approveExtensions = List.of("jpg","jpeg","png","gif");
        return approveExtensions.contains(extension.toLowerCase());
    }
}
