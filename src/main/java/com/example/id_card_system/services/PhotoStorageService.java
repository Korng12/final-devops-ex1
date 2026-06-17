package com.example.id_card_system.services;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Locale;
import java.util.Set;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class PhotoStorageService {

    private static final Set<String> ALLOWED_TYPES = Set.of("image/jpeg", "image/png");

    private final Path storageDir;
    private final long maxSizeBytes;

    public PhotoStorageService(
            @Value("${idcard.photo.storage-dir:uploads/photos}") String storageDir,
            @Value("${idcard.photo.max-size-bytes:2097152}") long maxSizeBytes) {
        this.storageDir = Path.of(storageDir);
        this.maxSizeBytes = maxSizeBytes;
    }

    public String store(MultipartFile file) {
        validate(file);
        try {
            Files.createDirectories(storageDir);
            String extension = extensionFor(file.getContentType());
            String fileName = UUID.randomUUID() + extension;
            Files.copy(file.getInputStream(), storageDir.resolve(fileName));
            return fileName;
        } catch (IOException ex) {
            throw new IllegalStateException("Could not store photo", ex);
        }
    }

    public Path resolve(String fileName) {
        return storageDir.resolve(fileName).normalize();
    }

    private void validate(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("Photo file is required");
        }
        if (file.getSize() > maxSizeBytes) {
            throw new IllegalArgumentException("Photo must be smaller than " + maxSizeBytes + " bytes");
        }
        if (!ALLOWED_TYPES.contains(file.getContentType())) {
            throw new IllegalArgumentException("Only JPEG and PNG photos are supported");
        }
    }

    private String extensionFor(String contentType) {
        return "image/png".equals(contentType.toLowerCase(Locale.ROOT)) ? ".png" : ".jpg";
    }
}
