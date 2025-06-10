package com.WebApplication.Controller;

import com.WebApplication.Entity.WebMapAndImages;
import com.WebApplication.Service.MapAndImagesService;
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
public class MapAndImagesController {

    private final MapAndImagesService service;

    @Autowired
    public MapAndImagesController(MapAndImagesService service) {
        this.service = service;
    }

    @PostMapping("/createMapAndImages")
    public ResponseEntity<?> createMapAndImages(
            @RequestParam String maps,
            @RequestParam(required = false) MultipartFile contactImage,
            @RequestParam String role,
            @RequestParam String email,
            @RequestParam String dynamicPart) {
        try {
            WebMapAndImages mapAndImages = new WebMapAndImages();
            mapAndImages.setMaps(maps);

            WebMapAndImages created = service.createMapAndImages(mapAndImages, contactImage, role, email, dynamicPart);
            return ResponseEntity.status(HttpStatus.CREATED).body(Map.of(
                    "success", true,
                    "message", "Map and Images created successfully",
                    "data", created
            ));
        } catch (AccessDeniedException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of(
                    "success", false,
                    "message", e.getMessage()
            ));
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of(
                    "success", false,
                    "message", e.getMessage()
            ));
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                    "success", false,
                    "message", "Error creating Map and Images: " + e.getMessage()
            ));
        }
    }

    @PostMapping("/public/createMapAndImages")
    public ResponseEntity<?> publicCreateMapAndImages(
            @RequestParam String maps,
            @RequestParam(required = false) MultipartFile contactImage,
            @RequestParam String dynamicPart) {
        try {
            WebMapAndImages created = service.publicCreateMapAndImages(maps, contactImage, dynamicPart);
            return ResponseEntity.status(HttpStatus.CREATED).body(Map.of(
                    "success", true,
                    "message", "Map and Images created successfully",
                    "data", Map.of(
                            "id", created.getId(),
                            "maps", created.getMaps(),
                            "contactImage", created.getContactImage(),
                            "dynamicPart", dynamicPart
                    )
            ));
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of(
                    "success", false,
                    "message", e.getMessage()
            ));
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                    "success", false,
                    "message", "Error creating Map and Images: " + e.getMessage()
            ));
        }
    }

    @GetMapping("/getAllMapAndImages")
    public ResponseEntity<?> getAllMapAndImages(
            @RequestParam String role,
            @RequestParam String email,
            @RequestParam String branchCode) {
        try {
            List<WebMapAndImages> mapAndImages = service.getAllMapAndImages(role, email, branchCode);
            return ResponseEntity.ok(mapAndImages);
        } catch (AccessDeniedException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
        }
    }

    @PutMapping("/updateMapAndImages/{id}")
    public ResponseEntity<?> updateMapAndImages(
            @PathVariable Long id,
            @RequestParam(required = false) String maps,
            @RequestParam(required = false) MultipartFile contactImage,
            @RequestParam String role,
            @RequestParam String email) {
        try {
            WebMapAndImages updated = new WebMapAndImages();
            if (maps != null) {
                updated.setMaps(maps);
            }

            WebMapAndImages result = service.updateMapAndImages(id, updated, contactImage, role, email);
            return ResponseEntity.ok(result);
        } catch (AccessDeniedException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error updating Map and Images: " + e.getMessage());
        }
    }

    @DeleteMapping("/deleteMapAndImages/{id}")
    public ResponseEntity<?> deleteMapAndImages(
            @PathVariable Long id,
            @RequestParam String role,
            @RequestParam String email) {
        try {
            service.deleteMapAndImages(id, role, email);
            return ResponseEntity.ok("Map and Images deleted successfully.");
        } catch (AccessDeniedException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @GetMapping("/getMapAndImagesByEmail")
    public ResponseEntity<?> getMapAndImagesByEmail(@RequestParam String email) {
        try {
            return service.getMapAndImagesByEmail(email)
                    .map(ResponseEntity::ok)
                    .orElse(ResponseEntity.notFound().build());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error fetching Map and Images: " + e.getMessage());
        }
    }

    @GetMapping("/getMapAndImagesByDynamicPart")
    public ResponseEntity<?> getMapAndImagesByDynamicPart(@RequestParam String dynamicPart) {
        try {
            List<WebMapAndImages> mapAndImages = service.getMapAndImagesByDynamicPart(dynamicPart);
            return ResponseEntity.ok(mapAndImages);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error fetching Map and Images: " + e.getMessage());
        }
    }

    @GetMapping("/public/getMapAndImages")
    public ResponseEntity<?> getMapAndImagesByDynamicPartPublic(@RequestParam String dynamicPart) {
        try {
            WebMapAndImages mapAndImages = service.getUserMapAndImagesByDynamicPart(dynamicPart);
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "data", Map.of(
                            "id", mapAndImages.getId(),
                            "maps", mapAndImages.getMaps(),
                            "contactImage", mapAndImages.getContactImage(),
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
                    "message", "Failed to fetch Map and Images: " + e.getMessage()
            ));
        }
    }
}