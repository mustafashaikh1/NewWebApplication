package com.WebApplication.Controller;

import com.WebApplication.Entity.WebGallery;
import com.WebApplication.Entity.WebUrlMapping;
import com.WebApplication.Service.WebGalleryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

@RestController
@CrossOrigin(origins = {
        "https://pjsofttech.in",
        "https://live.ooacademy.co.in",
        "https://course.yashodapublication.com",
        "https://lokrajyaacademy.com"
})
public class WebGalleryController {

    private final WebGalleryService webGalleryService;

    @Autowired
    public WebGalleryController(WebGalleryService webGalleryService) {
        this.webGalleryService = webGalleryService;
    }

    @PostMapping("/createWebGallery")
    public ResponseEntity<?> createWebGallery(
            @RequestParam String eventName,
            @RequestParam Integer year,
            @RequestParam String galleryColor,
            @RequestParam(required = false) List<MultipartFile> galleryImages,
            @RequestParam String role,
            @RequestParam String email,
            @RequestParam String dynamicPart) {

        try {
            WebGallery webGallery = new WebGallery();
            webGallery.setEventName(eventName);
            webGallery.setYear(year);
            webGallery.setGalleryColor(galleryColor);

            WebGallery created = webGalleryService.createWebGallery(webGallery, galleryImages, role, email, dynamicPart);
            return ResponseEntity.status(HttpStatus.CREATED).body(Map.of(
                    "success", true,
                    "message", "WebGallery created successfully",
                    "data", created
            ));
        } catch (AccessDeniedException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of(
                    "success", false,
                    "message", e.getMessage()
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                    "success", false,
                    "message", "Error creating WebGallery: " + e.getMessage()
            ));
        }
    }

    @PostMapping("/public/createWebGallery")
    public ResponseEntity<?> publicCreateWebGallery(
            @RequestParam String eventName,
            @RequestParam Integer year,
            @RequestParam String galleryColor,
            @RequestParam(required = false) List<MultipartFile> galleryImages,
            @RequestParam String dynamicPart) {

        try {
            WebGallery created = webGalleryService.publicCreateWebGallery(eventName, year, galleryColor, galleryImages, dynamicPart);
            return ResponseEntity.status(HttpStatus.CREATED).body(Map.of(
                    "success", true,
                    "message", "WebGallery created successfully",
                    "data", Map.of(
                            "id", created.getWebGalleryId(),
                            "eventName", created.getEventName(),
                            "year", created.getYear(),
                            "galleryColor", created.getGalleryColor(),
                            "dynamicPart", dynamicPart
                    )
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                    "success", false,
                    "message", "Error creating WebGallery: " + e.getMessage()
            ));
        }
    }

    @PutMapping("/updateWebGallery/{id}")
    public ResponseEntity<?> updateWebGallery(
            @PathVariable Long id,
            @RequestParam String eventName,
            @RequestParam Integer year,
            @RequestParam String galleryColor,
            @RequestParam(required = false) String dynamicPart,
            @RequestParam(required = false) List<MultipartFile> galleryImages,
            @RequestParam String role,
            @RequestParam String email) {

        try {
            WebGallery updatedWebGallery = new WebGallery();
            updatedWebGallery.setEventName(eventName);
            updatedWebGallery.setYear(year);
            updatedWebGallery.setGalleryColor(galleryColor);

            if (dynamicPart != null && !dynamicPart.isEmpty()) {
                WebUrlMapping mapping = new WebUrlMapping();
                mapping.setDynamicPart(dynamicPart);
                updatedWebGallery.setWebUrlMapping(mapping);
            }

            WebGallery result = webGalleryService.updateWebGallery(id, updatedWebGallery, galleryImages, role, email);
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "WebGallery updated successfully",
                    "data", result
            ));
        } catch (AccessDeniedException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of(
                    "success", false,
                    "message", e.getMessage()
            ));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of(
                    "success", false,
                    "message", e.getMessage()
            ));
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                    "success", false,
                    "message", "Error updating WebGallery: " + e.getMessage()
            ));
        }
    }


    @DeleteMapping("/deleteWebGallery/{id}")
    public ResponseEntity<?> deleteWebGallery(
            @PathVariable Long id,
            @RequestParam String role,
            @RequestParam String email) {

        try {
            webGalleryService.deleteWebGallery(id, role, email);
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "WebGallery deleted successfully"
            ));
        } catch (AccessDeniedException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of(
                    "success", false,
                    "message", e.getMessage()
            ));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of(
                    "success", false,
                    "message", e.getMessage()
            ));
        }
    }

    @GetMapping("/getAllWebGalleries")
    public ResponseEntity<?> getAllWebGalleries(
            @RequestParam String role,
            @RequestParam String email,
            @RequestParam String branchCode) {

        try {
            List<WebGallery> webGalleries = webGalleryService.getAllWebGalleries(role, email, branchCode);
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "data", webGalleries
            ));
        } catch (AccessDeniedException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of(
                    "success", false,
                    "message", e.getMessage()
            ));
        }
    }

    @GetMapping("/getWebGalleryById/{id}")
    public ResponseEntity<?> getWebGalleryById(
            @PathVariable Long id,
            @RequestParam String role,
            @RequestParam String email) {

        try {
            WebGallery webGallery = webGalleryService.getWebGalleryById(id, role, email);
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "data", webGallery
            ));
        } catch (AccessDeniedException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of(
                    "success", false,
                    "message", e.getMessage()
            ));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of(
                    "success", false,
                    "message", e.getMessage()
            ));
        }
    }

    @GetMapping("/getWebGalleriesByDynamicPart")
    public ResponseEntity<?> getWebGalleriesByDynamicPart(@RequestParam String dynamicPart) {
        try {
            List<WebGallery> webGalleries = webGalleryService.getWebGalleriesByDynamicPart(dynamicPart);
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "data", webGalleries
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                    "success", false,
                    "message", "Error fetching WebGalleries: " + e.getMessage()
            ));
        }
    }

    @GetMapping("/public/getWebGallery")
    public ResponseEntity<?> getWebGalleryByDynamicPart(@RequestParam String dynamicPart) {
        try {
            WebGallery webGallery = webGalleryService.getUserWebGalleryByDynamicPart(dynamicPart);
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "data", Map.of(
                            "id", webGallery.getWebGalleryId(),
                            "eventName", webGallery.getEventName(),
                            "year", webGallery.getYear(),
                            "galleryColor", webGallery.getGalleryColor(),
                            "galleryImages", webGallery.getGalleryImages(),
                            "imageUrlIds", webGallery.getImageUrlIds(),
                            "dynamicPart", dynamicPart
                    )
            ));
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of(
                    "success", false,
                    "message", e.getMessage()
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                    "success", false,
                    "message", "Failed to fetch WebGallery: " + e.getMessage()
            ));
        }
    }
}