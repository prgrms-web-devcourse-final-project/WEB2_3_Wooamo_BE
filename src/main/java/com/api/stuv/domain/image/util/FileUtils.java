package com.api.stuv.domain.image.util;

import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

public class FileUtils {

    public static final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public static String generateNewFilename() {
        LocalDateTime now = LocalDateTime.now();
        String datePart = now.format(dateTimeFormatter);
        String uniquePart = UUID.randomUUID().toString().substring(0, 13).toUpperCase().replace("-", "");

        return String.format("%s_%s", datePart, uniquePart);
    }

    public static String getExtension(MultipartFile file) {
        return StringUtils.getFilenameExtension(file.getOriginalFilename());
    }
}
