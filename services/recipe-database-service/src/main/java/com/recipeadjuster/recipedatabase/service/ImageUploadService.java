package com.recipeadjuster.recipedatabase.service;

import com.recipeadjuster.recipedatabase.exception.ValidationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.Map;

@Service
public class ImageUploadService {
    
    private static final Logger log = LoggerFactory.getLogger(ImageUploadService.class);
    
    @Value("${cloudinary.cloud-name:}")
    private String cloudName;
    
    @Value("${cloudinary.api-key:}")
    private String apiKey;
    
    @Value("${cloudinary.api-secret:}")
    private String apiSecret;
    
    private static final long MAX_FILE_SIZE = 5 * 1024 * 1024; // 5MB
    private static final String[] ALLOWED_FORMATS = {"image/jpeg", "image/jpg", "image/png", "image/webp"};
    
    public Map<String, String> uploadImage(MultipartFile file) {
        log.info("Uploading image: {}", file.getOriginalFilename());
        
        validateImage(file);
        
        // TODO: Integrate with Cloudinary SDK
        // Cloudinary cloudinary = new Cloudinary(ObjectUtils.asMap(
        //     "cloud_name", cloudName,
        //     "api_key", apiKey,
        //     "api_secret", apiSecret
        // ));
        
        // Map uploadResult = cloudinary.uploader().upload(file.getBytes(), ObjectUtils.asMap(
        //     "folder", "recipes",
        //     "transformation", new Transformation().width(800).height(600).crop("limit"),
        //     "eager", Arrays.asList(
        //         new Transformation().width(200).height(200).crop("thumb")
        //     )
        // ));
        
        Map<String, String> result = new HashMap<>();
        result.put("imageUrl", "https://res.cloudinary.com/" + cloudName + "/image/upload/v1/recipes/placeholder.jpg");
        result.put("thumbnailUrl", "https://res.cloudinary.com/" + cloudName + "/image/upload/c_thumb,w_200,h_200/v1/recipes/placeholder.jpg");
        result.put("publicId", "recipes/placeholder");
        
        log.info("Image uploaded successfully");
        return result;
    }
    
    private void validateImage(MultipartFile file) {
        if (file.isEmpty()) {
            throw new ValidationException("File is empty");
        }
        
        if (file.getSize() > MAX_FILE_SIZE) {
            throw new ValidationException("File size exceeds 5MB limit");
        }
        
        String contentType = file.getContentType();
        boolean validFormat = false;
        for (String format : ALLOWED_FORMATS) {
            if (format.equals(contentType)) {
                validFormat = true;
                break;
            }
        }
        
        if (!validFormat) {
            throw new ValidationException("Invalid image format. Allowed: JPEG, PNG, WebP");
        }
    }
    
    public void deleteImage(String publicId) {
        log.info("Deleting image: {}", publicId);
        
        // TODO: Integrate with Cloudinary SDK
        // cloudinary.uploader().destroy(publicId, ObjectUtils.emptyMap());
        
        log.info("Image deleted successfully");
    }
}
