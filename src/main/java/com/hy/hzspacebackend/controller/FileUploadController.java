package com.hy.hzspacebackend.controller;

import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

@RestController
@CrossOrigin
public class FileUploadController {
    private final Path uploadDirectory = Paths.get("/var/img/uploads");

    @PostMapping("/api/upload")
    public ResponseEntity<?> handleFileUpload(@RequestParam("file") MultipartFile file) {
        try {
            // 更改上传路径为 /var/img
//            Path uploadDirectory = Paths.get("/var/img");

            if (!Files.exists(uploadDirectory)) {
                Files.createDirectories(uploadDirectory);
            }

            Path targetLocation = uploadDirectory.resolve(file.getOriginalFilename());
            Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);
            return ResponseEntity.ok("File uploaded successfully.");
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
