package com.homelens.service;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import jakarta.annotation.PostConstruct;

@Service
public class FileStorageService {

    @Value("${app.upload.dir:uploads}")
    private String baseUploadDir;

    private static final List<String> ALLOWED_EXTENSIONS = List.of(".jpg", ".jpeg", ".png", ".webp");
    private Path propertyUploadPath;

    @PostConstruct
    public void init() {
        this.propertyUploadPath = Paths.get(baseUploadDir, "properties").toAbsolutePath().normalize();
        try {
            Files.createDirectories(this.propertyUploadPath);
        } catch (IOException e) {
            throw new RuntimeException("Could not initialize local upload directory: " + propertyUploadPath, e);
        }
    }

    public String storePropertyImage(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("Cannot store an empty or null file.");
        }

        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null) {
            originalFilename = "image.jpg";
        }

        String extension = "";
        int dotIndex = originalFilename.lastIndexOf('.');
        if (dotIndex >= 0) {
            extension = originalFilename.substring(dotIndex).toLowerCase();
        }

        if (!ALLOWED_EXTENSIONS.contains(extension)) {
            throw new IllegalArgumentException("Unsupported file format: " + extension +
                    ". Allowed formats: " + ALLOWED_EXTENSIONS);
        }

        String uniqueFilename = UUID.randomUUID() + extension;
        Path targetLocation = this.propertyUploadPath.resolve(uniqueFilename);

        try (InputStream inputStream = file.getInputStream()) {
            Files.copy(inputStream, targetLocation, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            throw new RuntimeException("Failed to store file: " + uniqueFilename, e);
        }

        // Return public relative path
        return "/uploads/properties/" + uniqueFilename;
    }

    public void deletePropertyImage(String relativePath) {
        if (relativePath == null || !relativePath.startsWith("/uploads/properties/")) {
            return;
        }

        String filename = relativePath.substring("/uploads/properties/".length());
        Path fileLocation = this.propertyUploadPath.resolve(filename);
        try {
            Files.deleteIfExists(fileLocation);
        } catch (IOException ignored) {
            // Log or ignore non-critical delete failures
        }
    }
}
