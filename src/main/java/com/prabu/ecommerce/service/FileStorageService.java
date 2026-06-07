package com.prabu.ecommerce.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.*;
import java.util.List;
import java.util.UUID;

@Service
public class FileStorageService {

    @Value("${file.upload-dir:uploads}")
    private String uploadDir;

    private static final List<String> ALLOWED_TYPES =
            List.of("image/jpeg", "image/png",
                    "image/jpg",  "image/webp");

    private static final long MAX_SIZE = 5 * 1024 * 1024; // 5MB

    public String saveFile(MultipartFile file, String subDir) {

        if (file.isEmpty()) {
            throw new RuntimeException("File is empty");
        }

        if (!ALLOWED_TYPES.contains(file.getContentType())) {
            throw new RuntimeException(
                    "Invalid file type. Only JPG, PNG, WEBP allowed"
            );
        }

        if (file.getSize() > MAX_SIZE) {
            throw new RuntimeException(
                    "File too large. Maximum size is 5MB"
            );
        }

        try {
            Path uploadPath = Paths.get(uploadDir, subDir);
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            String extension = getExtension(
                    file.getOriginalFilename()
            );
            String fileName = UUID.randomUUID() + "." + extension;

            Path filePath = uploadPath.resolve(fileName);
            Files.copy(file.getInputStream(), filePath,
                    StandardCopyOption.REPLACE_EXISTING);

            return "/uploads/" + subDir + "/" + fileName;

        } catch (IOException e) {
            throw new RuntimeException(
                    "Could not save file: " + e.getMessage()
            );
        }
    }

    public void deleteFile(String fileUrl) {
        if (fileUrl == null || fileUrl.isEmpty()) return;
        try {
            // Remove leading slash if present for path resolution, but here we expect /uploads/subDir/filename
            // The fileUrl is like /uploads/products/uuid.jpg
            // uploadDir is uploads/
            String relativePath = fileUrl.replaceFirst("^/uploads/", "");
            Path filePath = Paths.get(uploadDir).resolve(relativePath);
            Files.deleteIfExists(filePath);

        } catch (IOException e) {
            System.err.println(
                    "Could not delete file: " + e.getMessage()
            );
        }
    }

    private String getExtension(String filename) {
        if (filename == null || !filename.contains(".")) {
            return "jpg";
        }
        return filename.substring(
                filename.lastIndexOf(".") + 1
        ).toLowerCase();
    }
}