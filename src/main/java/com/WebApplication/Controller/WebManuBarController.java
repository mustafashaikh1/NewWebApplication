package com.WebApplication.Controller;

import com.WebApplication.Entity.WebManuBar;
import com.WebApplication.Service.WebManuBarService;
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
public class WebManuBarController {

    private final WebManuBarService webManuBarService;

    @Autowired
    public WebManuBarController(WebManuBarService webManuBarService) {
        this.webManuBarService = webManuBarService;
    }

    @PostMapping("/createWebManuBar")
    public ResponseEntity<?> createWebManuBar(@RequestParam String manuBarColor,
                                              @RequestParam(required = false) MultipartFile menubarImage,
                                              @RequestParam String role,
                                              @RequestParam String email,
                                              @RequestParam String dynamicPart) {
        try {
            WebManuBar webManuBar = new WebManuBar();
            webManuBar.setManuBarColor(manuBarColor);

            WebManuBar created = webManuBarService.createWebManuBar(webManuBar, menubarImage, role, email, dynamicPart);
            return ResponseEntity.status(HttpStatus.CREATED).body(Map.of(
                    "success", true,
                    "message", "WebManuBar created successfully",
                    "data", created
            ));

        } catch (AccessDeniedException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of(
                    "success", false,
                    "message", e.getMessage()
            ));
        } catch (IllegalStateException e) {
            // Dynamic part already has a WebManuBar
            return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of(
                    "success", false,
                    "message", e.getMessage()
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                    "success", false,
                    "message", "Error creating WebManuBar: " + e.getMessage()
            ));
        }
    }

    @PostMapping("/public/createWebManuBar")
    public ResponseEntity<?> publicCreateWebManuBar(
            @RequestParam String manuBarColor,
            @RequestParam(required = false) MultipartFile menubarImage,
            @RequestParam String dynamicPart) {
        try {
            WebManuBar created = webManuBarService.publicCreateWebManuBar(manuBarColor, menubarImage, dynamicPart);

            return ResponseEntity.status(HttpStatus.CREATED).body(Map.of(
                    "success", true,
                    "message", "WebManuBar created successfully",
                    "data", Map.of(
                            "id", created.getWebManuBarId(),
                            "manuBarColor", created.getManuBarColor(),
                            "menubarImage", created.getMenubarImage(),
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
                    "message", "Error creating WebManuBar: " + e.getMessage()
            ));
        }
    }

    @GetMapping("/getAllWebManuBars")
    public ResponseEntity<?> getAllWebManuBars(@RequestParam String role,
                                               @RequestParam String email,
                                               @RequestParam String branchCode) {
        try {
            List<WebManuBar> webManuBars = webManuBarService.getAllWebManuBars(role, email, branchCode);
            return ResponseEntity.ok(webManuBars);
        } catch (AccessDeniedException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
        }
    }

    @PutMapping("/updateWebManuBar/{id}")
    public ResponseEntity<?> updateWebManuBar(@PathVariable Long id,
                                              @RequestParam String manuBarColor,
                                              @RequestParam(required = false) MultipartFile menubarImage,
                                              @RequestParam String role,
                                              @RequestParam String email) {
        try {
            WebManuBar updatedWebManuBar = new WebManuBar();
            updatedWebManuBar.setManuBarColor(manuBarColor);

            WebManuBar result = webManuBarService.updateWebManuBar(id, updatedWebManuBar, menubarImage, role, email);
            return ResponseEntity.ok(result);
        } catch (AccessDeniedException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error updating WebManuBar: " + e.getMessage());
        }
    }

    @DeleteMapping("/deleteWebManuBar/{id}")
    public ResponseEntity<?> deleteWebManuBar(@PathVariable Long id,
                                              @RequestParam String role,
                                              @RequestParam String email) {
        try {
            webManuBarService.deleteWebManuBar(id, role, email);
            return ResponseEntity.ok("WebManuBar deleted successfully.");
        } catch (AccessDeniedException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @GetMapping("/getWebManuBarById/{id}")
    public ResponseEntity<?> getWebManuBarById(@PathVariable Long id,
                                               @RequestParam String role,
                                               @RequestParam String email) {
        try {
            WebManuBar webManuBar = webManuBarService.getWebManuBarById(id, role, email);
            return ResponseEntity.ok(webManuBar);
        } catch (AccessDeniedException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @GetMapping("/getWebManuBarByEmail/by-email")
    public ResponseEntity<?> getWebManuBarByEmail(@RequestParam String email) {
        try {
            return webManuBarService.getWebManuBarByEmail(email)
                    .map(ResponseEntity::ok)
                    .orElse(ResponseEntity.notFound().build());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error fetching WebManuBar: " + e.getMessage());
        }
    }

    @GetMapping("/getWebManuBarsByDynamicPart")
    public ResponseEntity<?> getWebManuBarsByDynamicPart(@RequestParam String dynamicPart) {
        try {
            List<WebManuBar> webManuBars = webManuBarService.getWebManuBarsByDynamicPart(dynamicPart);
            return ResponseEntity.ok(webManuBars);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error fetching WebManuBars: " + e.getMessage());
        }
    }

    // GET USER//

    @GetMapping("/public/getWebManuBar")
    public ResponseEntity<?> getWebManuBarByDynamicPart(@RequestParam String dynamicPart) {
        try {
            WebManuBar webManuBar = webManuBarService.getUserWebManuBarByDynamicPart(dynamicPart);
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "data", Map.of(
                            "id", webManuBar.getWebManuBarId(),
                            "manuBarColor", webManuBar.getManuBarColor(),
                            "menubarImage", webManuBar.getMenubarImage(),
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
                    "message", "Failed to fetch WebManuBar: " + e.getMessage()
            ));
        }
    }

}