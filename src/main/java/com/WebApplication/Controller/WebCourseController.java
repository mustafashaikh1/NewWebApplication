package com.WebApplication.Controller;

import com.WebApplication.Entity.WebCourse;
import com.WebApplication.Service.WebCourseService;
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
public class WebCourseController {

    private final WebCourseService webCourseService;

    @Autowired
    public WebCourseController(WebCourseService webCourseService) {
        this.webCourseService = webCourseService;
    }

    @PostMapping("/createWebCourse")
    public ResponseEntity<?> createWebCourse(
            @RequestParam String courseTitle,
            @RequestParam String link,
            @RequestParam String description,
            @RequestParam String courseColor,
            @RequestParam (required = false) MultipartFile courseImage,
            @RequestParam String role,
            @RequestParam String email,
            @RequestParam String dynamicPart) throws IOException {

        WebCourse webCourse = new WebCourse();
        webCourse.setCourseTitle(courseTitle);
        webCourse.setLink(link);
        webCourse.setDescription(description);
        webCourse.setCourseColor(courseColor);

        try {
            WebCourse created = webCourseService.createWebCourse(webCourse, courseImage, role, email, dynamicPart);
            return ResponseEntity.status(HttpStatus.CREATED).body(Map.of(
                    "success", true,
                    "message", "WebCourse created successfully",
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
                    "message", "Error creating WebCourse: " + e.getMessage()
            ));
        }
    }

    @PostMapping("/public/createWebCourse")
    public ResponseEntity<?> publicCreateWebCourse(
            @RequestParam String courseTitle,
            @RequestParam String link,
            @RequestParam String description,
            @RequestParam String courseColor,
            @RequestPart(required = false) MultipartFile courseImage,
            @RequestParam String dynamicPart)
    {
        try {
            WebCourse created = webCourseService.publicCreateWebCourse(courseTitle, link, description, courseColor, courseImage, dynamicPart);
            return ResponseEntity.status(HttpStatus.CREATED).body(Map.of(
                    "success", true,
                    "message", "WebCourse created successfully",
                    "data", Map.of(
                            "id", created.getCourseId(),
                            "courseTitle", created.getCourseTitle(),
                            "link", created.getLink(),
                            "courseImage", created.getCourseImage(),
                            "courseColor", created.getCourseColor(),
                            "dynamicPart", dynamicPart
                    )
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                    "success", false,
                    "message", "Error creating WebCourse: " + e.getMessage()
            ));
        }
    }

    @GetMapping("/getAllWebCourses")
    public ResponseEntity<?> getAllWebCourses(
            @RequestParam String role,
            @RequestParam String email,
            @RequestParam String branchCode) {
        try {
            List<WebCourse> webCourses = webCourseService.getAllWebCourses(role, email, branchCode);
            return ResponseEntity.ok(webCourses);
        } catch (AccessDeniedException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
        }
    }

    @PutMapping("/updateWebCourse/{id}")
    public ResponseEntity<?> updateWebCourse(
            @PathVariable Long id,
            @RequestParam String courseTitle,
            @RequestParam String link,
            @RequestParam String description,
            @RequestParam String courseColor,
            @RequestPart(required = false) MultipartFile courseImage,
            @RequestParam String role,
            @RequestParam String email) throws IOException {

        WebCourse webCourse = new WebCourse();
        webCourse.setCourseTitle(courseTitle);
        webCourse.setLink(link);
        webCourse.setDescription(description);
        webCourse.setCourseColor(courseColor);

        try {
            WebCourse updated = webCourseService.updateWebCourse(id, webCourse, courseImage, role, email);
            return ResponseEntity.ok(updated);
        } catch (AccessDeniedException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error updating WebCourse: " + e.getMessage());
        }
    }

    @DeleteMapping("/deleteWebCourse/{id}")
    public ResponseEntity<?> deleteWebCourse(
            @PathVariable Long id,
            @RequestParam String role,
            @RequestParam String email) {
        try {
            webCourseService.deleteWebCourse(id, role, email);
            return ResponseEntity.ok("WebCourse deleted successfully.");
        } catch (AccessDeniedException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @GetMapping("/getWebCourseById/{id}")
    public ResponseEntity<?> getWebCourseById(
            @PathVariable Long id,
            @RequestParam String role,
            @RequestParam String email) {
        try {
            WebCourse webCourse = webCourseService.getWebCourseById(id, role, email);
            return ResponseEntity.ok(webCourse);
        } catch (AccessDeniedException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @PostMapping("/addWebCourseColor")
    public ResponseEntity<?> addWebCourseColor(
            @RequestParam String branchCode,
            @RequestParam String courseColor,
            @RequestParam String role,
            @RequestParam String email) {
        try {
            webCourseService.addWebCourseColorByBranchCode(branchCode, courseColor, role, email);
            return ResponseEntity.ok("WebCourse color added successfully");
        } catch (AccessDeniedException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
        }
    }

    @PutMapping("/updateWebCourseColor")
    public ResponseEntity<?> updateWebCourseColor(
            @RequestParam String branchCode,
            @RequestParam String courseColor,
            @RequestParam String role,
            @RequestParam String email) {
        try {
            webCourseService.updateWebCourseColorByBranchCode(branchCode, courseColor, role, email);
            return ResponseEntity.ok("WebCourse color updated successfully");
        } catch (AccessDeniedException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
        }
    }

    @DeleteMapping("/deleteWebCourseColor")
    public ResponseEntity<?> deleteWebCourseColor(
            @RequestParam String branchCode,
            @RequestParam String role,
            @RequestParam String email) {
        try {
            webCourseService.deleteWebCourseColorByBranchCode(branchCode, role, email);
            return ResponseEntity.ok("WebCourse color deleted successfully");
        } catch (AccessDeniedException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
        }
    }

    @GetMapping("/getWebCoursesByDynamicPart")
    public ResponseEntity<?> getWebCoursesByDynamicPart(@RequestParam String dynamicPart) {
        try {
            List<WebCourse> webCourses = webCourseService.getWebCoursesByDynamicPart(dynamicPart);
            return ResponseEntity.ok(webCourses);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error fetching WebCourses: " + e.getMessage());
        }
    }

    @GetMapping("/public/getWebCourse")
    public ResponseEntity<?> getWebCourseByDynamicPart(@RequestParam String dynamicPart) {
        try {
            WebCourse webCourse = webCourseService.getUserWebCourseByDynamicPart(dynamicPart);
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "data", Map.of(
                            "id", webCourse.getCourseId(),
                            "courseTitle", webCourse.getCourseTitle(),
                            "link", webCourse.getLink(),
                            "courseImage", webCourse.getCourseImage(),
                            "description", webCourse.getDescription(),
                            "courseColor", webCourse.getCourseColor(),
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
                    "message", "Failed to fetch WebCourse: " + e.getMessage()
            ));
        }
    }
}