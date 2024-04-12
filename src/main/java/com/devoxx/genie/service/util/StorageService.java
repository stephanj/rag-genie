package com.devoxx.genie.service.util;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.FileSystemUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * @author Stephan Janssen
 */
@Service
public class StorageService {

    private static final Logger LOGGER = LoggerFactory.getLogger(StorageService.class);

    @Value("${file.upload.path}")
    private String imagesUploadPath;

    @PostConstruct
    public void init() {
        if (!Files.exists(getRootLocation())) {
            LOGGER.info("Create upload path {}", getRootLocation());
            try {
                Files.createDirectory(getRootLocation());
            } catch (IOException e) {
                throw new RuntimeException("Could not initialize storage! " + e.getMessage());
            }
        }
    }

    /**
     * Delete all uploaded items when destroying this bean.
     */
    @PreDestroy
    public void deleteAll() {
        LOGGER.info("Deleting all uploaded items.");
        FileSystemUtils.deleteRecursively(getRootLocation().toFile());
    }

    /**
     * Get the root location.
     *
     * @return the root location
     */
    private Path getRootLocation() {
        return Paths.get(imagesUploadPath);
    }

    /**
     * Store a multipart file.
     *
     * @param file the file to store
     * @return the path to the stored file
     */
    public Path storeMultipartFile(final MultipartFile file) {
        LOGGER.debug("Store multipart file {}", file.getOriginalFilename());

        try {
            if (file.getOriginalFilename() == null) {
                LOGGER.error("File name is null!");
                throw new RuntimeException("File name is null!");
            }

            final Path targetPath = getRootLocation().resolve(file.getOriginalFilename());

            File targetFile = targetPath.toFile();
            if (targetFile.exists() && !targetFile.delete()) {
                LOGGER.error("Unable to delete {}", targetFile.getAbsoluteFile());
            }

            Files.copy(file.getInputStream(), targetPath);
            return targetPath;
        } catch (Exception e) {
            throw new RuntimeException("FAIL! " + e.getMessage());
        }
    }
}
