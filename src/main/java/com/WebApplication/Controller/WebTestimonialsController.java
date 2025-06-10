package com.WebApplication.Controller;

import com.WebApplication.Entity.WebTestimonials;
import com.WebApplication.Service.WebTestimonialsService;
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
public class WebTestimonialsController {

    @Autowired
    private WebTestimonialsService service;

    @PostMapping("/createWebTestimonial")
    public ResponseEntity<?> createWebTestimonial(@RequestParam String testimonialTitle,
                                                  @RequestParam String testimonialName,
                                                  @RequestParam String exam,
                                                  @RequestParam String post,
                                                  @RequestParam String description,
                                                  @RequestParam String testimonialColor,
                                                  @RequestParam(required = false) MultipartFile testimonialImage,
                                                  @RequestParam String role,
                                                  @RequestParam String email,
                                                  @RequestParam String dynamicPart) {
        try {
            WebTestimonials testimonial = new WebTestimonials();
            testimonial.setTestimonialTitle(testimonialTitle);
            testimonial.setTestimonialName(testimonialName);
            testimonial.setExam(exam);
            testimonial.setPost(post);
            testimonial.setDescription(description);
            testimonial.setTestimonialColor(testimonialColor);

            WebTestimonials created = service.createWebTestimonial(testimonial, testimonialImage, role, email, dynamicPart);
            return ResponseEntity.status(HttpStatus.CREATED).body(created);
        } catch (AccessDeniedException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error creating testimonial: " + e.getMessage());
        }
    }

    @PostMapping("/public/createWebTestimonial")
    public ResponseEntity<?> publicCreateWebTestimonial(@RequestParam String testimonialTitle,
                                                        @RequestParam String testimonialName,
                                                        @RequestParam String exam,
                                                        @RequestParam String post,
                                                        @RequestParam String description,
                                                        @RequestParam String testimonialColor,
                                                        @RequestParam(required = false) MultipartFile testimonialImage,
                                                        @RequestParam String dynamicPart) {
        try {
            WebTestimonials created = service.publicCreateWebTestimonial(
                    testimonialTitle, testimonialName, exam, post, description,
                    testimonialImage, dynamicPart, testimonialColor);
            return ResponseEntity.status(HttpStatus.CREATED).body(created);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error creating testimonial: " + e.getMessage());
        }
    }

    @PutMapping("/updateWebTestimonial/{id}")
    public ResponseEntity<?> updateWebTestimonial(@PathVariable Long id,
                                                  @RequestParam String testimonialTitle,
                                                  @RequestParam String testimonialName,
                                                  @RequestParam String exam,
                                                  @RequestParam String post,
                                                  @RequestParam String description,
                                                  @RequestParam String testimonialColor,
                                                  @RequestParam(required = false) MultipartFile testimonialImage,
                                                  @RequestParam String role,
                                                  @RequestParam String email) {
        try {
            WebTestimonials testimonial = new WebTestimonials();
            testimonial.setTestimonialTitle(testimonialTitle);
            testimonial.setTestimonialName(testimonialName);
            testimonial.setExam(exam);
            testimonial.setPost(post);
            testimonial.setDescription(description);
            testimonial.setTestimonialColor(testimonialColor);

            WebTestimonials updated = service.updateWebTestimonial(id, testimonial, testimonialImage, role, email);
            return ResponseEntity.ok(updated);
        } catch (AccessDeniedException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error updating testimonial: " + e.getMessage());
        }
    }

    @DeleteMapping("/deleteWebTestimonial/{id}")
    public ResponseEntity<?> deleteWebTestimonial(@PathVariable Long id,
                                                  @RequestParam String role,
                                                  @RequestParam String email) {
        try {
            service.deleteWebTestimonialById(id, role, email);
            return ResponseEntity.ok("Testimonial deleted successfully.");
        } catch (AccessDeniedException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @GetMapping("/getAllWebTestimonials")
    public ResponseEntity<?> getAllWebTestimonials(@RequestParam String role,
                                                   @RequestParam String email,
                                                   @RequestParam String branchCode) {
        try {
            List<WebTestimonials> testimonials = service.getAllWebTestimonials(role, email, branchCode);
            return ResponseEntity.ok(testimonials);
        } catch (AccessDeniedException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
        }
    }

    @GetMapping("/getWebTestimonialById/{id}")
    public ResponseEntity<?> getWebTestimonialById(@PathVariable Long id,
                                                   @RequestParam String role,
                                                   @RequestParam String email) {
        try {
            WebTestimonials testimonial = service.getWebTestimonialById(id, role, email);
            return ResponseEntity.ok(testimonial);
        } catch (AccessDeniedException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @GetMapping("/getWebTestimonialByEmail")
    public ResponseEntity<?> getWebTestimonialByEmail(@RequestParam String email) {
        try {
            return service.getWebTestimonialByEmail(email)
                    .map(ResponseEntity::ok)
                    .orElse(ResponseEntity.notFound().build());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error fetching testimonial: " + e.getMessage());
        }
    }

    @GetMapping("/getWebTestimonialsByDynamicPart")
    public ResponseEntity<?> getWebTestimonialsByDynamicPart(@RequestParam String dynamicPart) {
        try {
            List<WebTestimonials> testimonials = service.getTestimonialsByDynamicPart(dynamicPart);
            return ResponseEntity.ok(testimonials);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error fetching testimonials: " + e.getMessage());
        }
    }

    @PutMapping("/updateWebTestimonialColor")
    public ResponseEntity<?> updateWebTestimonialColor(@RequestParam String branchCode,
                                                       @RequestParam String testimonialColor,
                                                       @RequestParam String role,
                                                       @RequestParam String email) {
        try {
            service.updateTestimonialColorByBranchCode(branchCode, testimonialColor, role, email);
            return ResponseEntity.ok("Testimonial color updated successfully for branch: " + branchCode);
        } catch (AccessDeniedException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
        }
    }

    @DeleteMapping("/deleteWebTestimonialColor")
    public ResponseEntity<?> deleteWebTestimonialColor(@RequestParam String branchCode,
                                                       @RequestParam String role,
                                                       @RequestParam String email) {
        try {
            service.deleteTestimonialColorByBranchCode(branchCode, role, email);
            return ResponseEntity.ok("Testimonial color deleted successfully for branch: " + branchCode);
        } catch (AccessDeniedException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
        }
    }

    @GetMapping("/public/getWebTestimonial")
    public ResponseEntity<?> getFirstTestimonial(@RequestParam String dynamicPart) {
        try {
            WebTestimonials testimonial = service.getUserTestimonialByDynamicPart(dynamicPart);
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "data", Map.of(
                            "id", testimonial.getTestimonialId(),
                            "title", testimonial.getTestimonialTitle(),
                            "name", testimonial.getTestimonialName(),
                            "exam", testimonial.getExam(),
                            "post", testimonial.getPost(),
                            "image", testimonial.getTestimonialImage(),
                            "description", testimonial.getDescription(),
                            "color", testimonial.getTestimonialColor()
                    )
            ));
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("success", false, "message", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("success", false, "message", e.getMessage()));
        }
    }
}