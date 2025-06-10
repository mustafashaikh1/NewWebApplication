package com.WebApplication.Controller;

import com.WebApplication.Entity.WebSlideBar;
import com.WebApplication.Service.WebSlideBarService;
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
public class WebSlideBarController {

    private final WebSlideBarService webSlideBarService;

    @Autowired
    public WebSlideBarController(WebSlideBarService webSlideBarService) {
        this.webSlideBarService = webSlideBarService;
    }

    @PostMapping("/createWebSlideBar")
    public ResponseEntity<?> createWebSlideBar(@RequestParam String slideBarColor,
                                               @RequestParam(required = false) List<MultipartFile> slideImages,
                                               @RequestParam String role,
                                               @RequestParam String email,
                                               @RequestParam String dynamicPart) {
        try {
            WebSlideBar webSlideBar = new WebSlideBar();
            webSlideBar.setSlideBarColor(slideBarColor);

            WebSlideBar created = webSlideBarService.createWebSlideBar(webSlideBar, slideImages, role, email, dynamicPart);
            return ResponseEntity.status(HttpStatus.CREATED).body(Map.of(
                    "success", true,
                    "message", "WebSlideBar created successfully",
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
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                    "success", false,
                    "message", "Error creating WebSlideBar: " + e.getMessage()
            ));
        }
    }

    @PostMapping("/public/createWebSlideBar")
    public ResponseEntity<?> publicCreateWebSlideBar(
            @RequestParam String slideBarColor,
            @RequestParam(required = false) List<MultipartFile> slideImages,
            @RequestParam String dynamicPart) {
        try {
            WebSlideBar created = webSlideBarService.publicCreateWebSlideBar(slideBarColor, slideImages, dynamicPart);

            return ResponseEntity.status(HttpStatus.CREATED).body(Map.of(
                    "success", true,
                    "message", "WebSlideBar created successfully",
                    "data", Map.of(
                            "id", created.getWebSlideBarId(),
                            "slideBarColor", created.getSlideBarColor(),
                            "slideImages", created.getSlideImages(),
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
                    "message", "Error creating WebSlideBar: " + e.getMessage()
            ));
        }
    }

    @GetMapping("/getAllWebSlideBars")
    public ResponseEntity<?> getAllWebSlideBars(@RequestParam String role,
                                                @RequestParam String email,
                                                @RequestParam String branchCode) {
        try {
            List<WebSlideBar> webSlideBars = webSlideBarService.getAllWebSlideBars(role, email, branchCode);
            return ResponseEntity.ok(webSlideBars);
        } catch (AccessDeniedException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
        }
    }

    @PutMapping("/updateWebSlideBar/{id}")
    public ResponseEntity<?> updateWebSlideBar(@PathVariable Long id,
                                               @RequestParam String slideBarColor,
                                               @RequestParam(required = false) List<MultipartFile> slideImages,
                                               @RequestParam String role,
                                               @RequestParam String email) {
        try {
            WebSlideBar updatedWebSlideBar = new WebSlideBar();
            updatedWebSlideBar.setSlideBarColor(slideBarColor);

            WebSlideBar result = webSlideBarService.updateWebSlideBar(id, updatedWebSlideBar, slideImages, role, email);
            return ResponseEntity.ok(result);
        } catch (AccessDeniedException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error updating WebSlideBar: " + e.getMessage());
        }
    }

    @DeleteMapping("/deleteWebSlideBar/{id}")
    public ResponseEntity<?> deleteWebSlideBar(@PathVariable Long id,
                                               @RequestParam String role,
                                               @RequestParam String email) {
        try {
            webSlideBarService.deleteWebSlideBar(id, role, email);
            return ResponseEntity.ok("WebSlideBar deleted successfully.");
        } catch (AccessDeniedException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @GetMapping("/getWebSlideBarById/{id}")
    public ResponseEntity<?> getWebSlideBarById(@PathVariable Long id,
                                                @RequestParam String role,
                                                @RequestParam String email) {
        try {
            WebSlideBar webSlideBar = webSlideBarService.getWebSlideBarById(id, role, email);
            return ResponseEntity.ok(webSlideBar);
        } catch (AccessDeniedException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @GetMapping("/getWebSlideBarByEmail/by-email")
    public ResponseEntity<?> getWebSlideBarByEmail(@RequestParam String email) {
        try {
            return webSlideBarService.getWebSlideBarByEmail(email)
                    .map(ResponseEntity::ok)
                    .orElse(ResponseEntity.notFound().build());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error fetching WebSlideBar: " + e.getMessage());
        }
    }

    @GetMapping("/getWebSlideBarsByDynamicPart")
    public ResponseEntity<?> getWebSlideBarsByDynamicPart(@RequestParam String dynamicPart) {
        try {
            List<WebSlideBar> webSlideBars = webSlideBarService.getWebSlideBarsByDynamicPart(dynamicPart);
            return ResponseEntity.ok(webSlideBars);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error fetching WebSlideBars: " + e.getMessage());
        }
    }

    @GetMapping("/public/getWebSlideBar")
    public ResponseEntity<?> getWebSlideBarByDynamicPart(@RequestParam String dynamicPart) {
        try {
            WebSlideBar webSlideBar = webSlideBarService.getUserWebSlideBarByDynamicPart(dynamicPart);
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "data", Map.of(
                            "id", webSlideBar.getWebSlideBarId(),
                            "slideBarColor", webSlideBar.getSlideBarColor(),
                            "slideImages", webSlideBar.getSlideImages(),
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
                    "message", "Failed to fetch WebSlideBar: " + e.getMessage()
            ));
        }
    }
}