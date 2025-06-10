package com.WebApplication.Controller;

import com.WebApplication.Entity.WebAboutUs;
import com.WebApplication.Service.WebAboutUsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

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
public class WebAboutUsController {

    private final WebAboutUsService webAboutUsService;

    @Autowired
    public WebAboutUsController(WebAboutUsService webAboutUsService) {
        this.webAboutUsService = webAboutUsService;
    }

    @PostMapping("/createWebAboutUs")
    public ResponseEntity<?> createWebAboutUs(
            @RequestParam String aboutUsTitle,
            @RequestParam String description,
            @RequestParam(required = false) MultipartFile aboutUsImage,
            @RequestParam String role,
            @RequestParam String email,
            @RequestParam String dynamicPart) {
        try {
            WebAboutUs webAboutUs = new WebAboutUs();
            webAboutUs.setAboutUsTitle(aboutUsTitle);
            webAboutUs.setDescription(description);

            WebAboutUs created = webAboutUsService.createWebAboutUs(webAboutUs, aboutUsImage, role, email, dynamicPart);
            return ResponseEntity.status(HttpStatus.CREATED).body(Map.of(
                    "success", true,
                    "message", "WebAboutUs created successfully",
                    "data", created
            ));

        } catch (AccessDeniedException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of(
                    "success", false,
                    "message", e.getMessage()
            ));
        } catch (IllegalStateException e) {
            // Dynamic part already has a WebAboutUs
            return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of(
                    "success", false,
                    "message", e.getMessage()
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                    "success", false,
                    "message", "Error creating WebAboutUs: " + e.getMessage()
            ));
        }
    }

    @PostMapping("/public/createWebAboutUs")
    public ResponseEntity<?> publicCreateWebAboutUs(
            @RequestParam String aboutUsTitle,
            @RequestParam String description,
            @RequestParam(required = false) MultipartFile aboutUsImage,
            @RequestParam String dynamicPart) {
        try {
            WebAboutUs created = webAboutUsService.publicCreateWebAboutUs(aboutUsTitle, description, aboutUsImage, dynamicPart);

            return ResponseEntity.status(HttpStatus.CREATED).body(Map.of(
                    "success", true,
                    "message", "WebAboutUs created successfully",
                    "data", Map.of(
                            "id", created.getAboutUsId(),
                            "aboutUsTitle", created.getAboutUsTitle(),
                            "description", created.getDescription(),
                            "aboutUsImage", created.getAboutUsImage(),
                            "dynamicPart", dynamicPart
                    )
            ));
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of(
                    "success", false,
                    "message", e.getMessage()
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                    "success", false,
                    "message", "Error creating WebAboutUs: " + e.getMessage()
            ));
        }
    }

    @GetMapping("/getAllWebAboutUs")
    public ResponseEntity<?> getAllWebAboutUs(
            @RequestParam String role,
            @RequestParam String email,
            @RequestParam String branchCode) {
        try {
            List<WebAboutUs> webAboutUsList = webAboutUsService.getAllWebAboutUs(role, email, branchCode);
            return ResponseEntity.ok(webAboutUsList);
        } catch (AccessDeniedException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
        }
    }

    @PutMapping("/updateWebAboutUs/{id}")
    public ResponseEntity<?> updateWebAboutUs(
            @PathVariable Long id,
            @RequestParam String aboutUsTitle,
            @RequestParam String description,
            @RequestParam(required = false) MultipartFile aboutUsImage,
            @RequestParam String role,
            @RequestParam String email) {
        try {
            WebAboutUs updatedWebAboutUs = new WebAboutUs();
            updatedWebAboutUs.setAboutUsTitle(aboutUsTitle);
            updatedWebAboutUs.setDescription(description);

            WebAboutUs result = webAboutUsService.updateWebAboutUs(id, updatedWebAboutUs, aboutUsImage, role, email);
            return ResponseEntity.ok(result);
        } catch (AccessDeniedException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @DeleteMapping("/deleteWebAboutUs/{id}")
    public ResponseEntity<?> deleteWebAboutUs(
            @PathVariable Long id,
            @RequestParam String role,
            @RequestParam String email) {
        try {
            webAboutUsService.deleteWebAboutUs(id, role, email);
            return ResponseEntity.ok("WebAboutUs deleted successfully.");
        } catch (AccessDeniedException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @GetMapping("/getWebAboutUsById/{id}")
    public ResponseEntity<?> getWebAboutUsById(
            @PathVariable Long id,
            @RequestParam String role,
            @RequestParam String email) {
        try {
            WebAboutUs webAboutUs = webAboutUsService.getWebAboutUsById(id, role, email);
            return ResponseEntity.ok(webAboutUs);
        } catch (AccessDeniedException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @GetMapping("/getWebAboutUsByEmail/by-email")
    public ResponseEntity<?> getWebAboutUsByEmail(@RequestParam String email) {
        try {
            return webAboutUsService.getWebAboutUsByEmail(email)
                    .map(ResponseEntity::ok)
                    .orElse(ResponseEntity.notFound().build());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error fetching WebAboutUs: " + e.getMessage());
        }
    }

    @GetMapping("/getWebAboutUsByDynamicPart")
    public ResponseEntity<?> getWebAboutUsByDynamicPart(@RequestParam String dynamicPart) {
        try {
            List<WebAboutUs> webAboutUsList = webAboutUsService.getWebAboutUsByDynamicPart(dynamicPart);
            return ResponseEntity.ok(webAboutUsList);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error fetching WebAboutUs: " + e.getMessage());
        }
    }

    // GET USER//
    @GetMapping("/public/getWebAboutUs")
    public ResponseEntity<?> getUserWebAboutUsByDynamicPart(@RequestParam String dynamicPart) {
        try {
            WebAboutUs webAboutUs = webAboutUsService.getUserWebAboutUsByDynamicPart(dynamicPart);
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "data", Map.of(
                            "id", webAboutUs.getAboutUsId(),
                            "aboutUsTitle", webAboutUs.getAboutUsTitle(),
                            "description", webAboutUs.getDescription(),
                            "aboutUsImage", webAboutUs.getAboutUsImage(),
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
                    "message", "Failed to fetch WebAboutUs: " + e.getMessage()
            ));
        }
    }
}