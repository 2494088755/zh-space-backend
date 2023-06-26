package com.hy.hzspacebackend.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Stream;

@RestController
@CrossOrigin
public class FileUploadController {

    @Value("${upload.path}")
    private String uploadPath;

    private Path uploadDirectory;

    @PostConstruct
    public void initUploadDirectory() {
        uploadDirectory = Paths.get(uploadPath);
    }

    //    private final Path uploadDirectory = Paths.get("D:\\img");

    @PostMapping("/api/upload")
    public ResponseEntity<?> handleFileUpload(@RequestParam("file") MultipartFile file) {
        try {
            if (!Files.exists(uploadDirectory)) {
                Files.createDirectories(uploadDirectory);
            }
            String filename = UUID.randomUUID() + "."+Objects.requireNonNull(file.getOriginalFilename()).split("\\.")[1];
            Path targetLocation = uploadDirectory.resolve(filename);
            file.transferTo(targetLocation);
            return ResponseEntity.ok(targetLocation.toString());
        } catch (IOException e) {
            return ResponseEntity.badRequest().body("Unable to upload file.");
        }
    }

    @GetMapping("/api/files/{filename:.+}")
    public ResponseEntity<?> downloadFile(@PathVariable String filename) {
        try {
            Path file = uploadDirectory.resolve(filename);
            Resource resource = new UrlResource(file.toUri());

            if (resource.exists() || resource.isReadable()) {
                return ResponseEntity.ok()
                        .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"")
                        .body(resource);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (MalformedURLException e) {
            return ResponseEntity.badRequest().body("Unable to download file.");
        }
    }

    @GetMapping("/api/images")
    public ResponseEntity<?> getAllImages() {
        try {
            if (!Files.exists(uploadDirectory)) {
                return ResponseEntity.notFound().build();
            }

            List<String> imageFilenames = new ArrayList<>();
            try (Stream<Path> paths = Files.list(uploadDirectory)) {
                paths.filter(Files::isRegularFile)
                        .filter(path -> path.toString().toLowerCase().matches(".*\\.(jpg|jpeg|png|gif|webp)$"))
                        .forEach(path -> imageFilenames.add(path.getFileName().toString()));
            }

            return ResponseEntity.ok(imageFilenames);
        } catch (IOException e) {
            return ResponseEntity.badRequest().body("Unable to get images.");
        }
    }
}
