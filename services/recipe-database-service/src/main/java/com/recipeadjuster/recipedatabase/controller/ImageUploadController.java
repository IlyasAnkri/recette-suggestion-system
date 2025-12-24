package com.recipeadjuster.recipedatabase.controller;

import com.recipeadjuster.recipedatabase.service.ImageUploadService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/recipes/images")
public class ImageUploadController {
    
    private static final Logger log = LoggerFactory.getLogger(ImageUploadController.class);
    private final ImageUploadService imageUploadService;
    
    public ImageUploadController(ImageUploadService imageUploadService) {
        this.imageUploadService = imageUploadService;
    }
    
    @PostMapping("/upload")
    public ResponseEntity<Map<String, String>> uploadImage(
            @RequestParam("file") MultipartFile file,
            @RequestHeader(value = "X-User-Id", required = false) String userIdHeader) {
        
        log.info("POST /api/v1/recipes/images/upload - Uploading image");
        
        Map<String, String> result = imageUploadService.uploadImage(file);
        return ResponseEntity.ok(result);
    }
    
    @DeleteMapping("/{publicId}")
    public ResponseEntity<Void> deleteImage(
            @PathVariable String publicId,
            @RequestHeader(value = "X-User-Role", required = false, defaultValue = "USER") String userRole) {
        
        log.info("DELETE /api/v1/recipes/images/{} - Deleting image", publicId);
        
        if (!"ADMIN".equals(userRole) && !"MODERATOR".equals(userRole)) {
            return ResponseEntity.status(403).build();
        }
        
        imageUploadService.deleteImage(publicId);
        return ResponseEntity.noContent().build();
    }
}
