package com.WebApplication.Controller;

import com.WebApplication.Entity.WebTopper;
import com.WebApplication.Service.WebTopperService;
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
public class WebTopperController {

    @Autowired
    private WebTopperService service;

    @PostMapping("/createWebTopper")
    public ResponseEntity<?> createWebTopper(@RequestParam String name,
                                             @RequestParam Double totalMarks,
                                             @RequestParam String post,
                                             @RequestParam Integer rank,
                                             @RequestParam Integer year,
                                             @RequestParam String topperColor,
                                             @RequestParam(required = false) List<MultipartFile> topperImages,
                                             @RequestParam String role,
                                             @RequestParam String email,
                                             @RequestParam String dynamicPart) {
        try {
            WebTopper topper = new WebTopper();
            topper.setName(name);
            topper.setTotalMarks(totalMarks);
            topper.setPost(post);
            topper.setRank(rank);
            topper.setYear(year);
            topper.setTopperColor(topperColor);

            WebTopper created = service.createWebTopper(topper, topperImages, role, email, dynamicPart);
            return ResponseEntity.status(HttpStatus.CREATED).body(created);
        } catch (AccessDeniedException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error creating topper: " + e.getMessage());
        }
    }

    @PostMapping("/public/createWebTopper")
    public ResponseEntity<?> publicCreateWebTopper(@RequestParam String name,
                                                   @RequestParam Double totalMarks,
                                                   @RequestParam String post,
                                                   @RequestParam Integer rank,
                                                   @RequestParam Integer year,
                                                   @RequestParam String topperColor,
                                                   @RequestParam(required = false) List<MultipartFile> topperImages,
                                                   @RequestParam String dynamicPart) {
        try {
            WebTopper created = service.publicCreateWebTopper(
                    name, totalMarks, post, rank, year, topperColor,
                    topperImages, dynamicPart);
            return ResponseEntity.status(HttpStatus.CREATED).body(created);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error creating topper: " + e.getMessage());
        }
    }

    @PutMapping("/updateWebTopper/{id}")
    public ResponseEntity<?> updateWebTopper(@PathVariable Long id,
                                             @RequestParam String name,
                                             @RequestParam Double totalMarks,
                                             @RequestParam String post,
                                             @RequestParam Integer rank,
                                             @RequestParam Integer year,
                                             @RequestParam String topperColor,
                                             @RequestParam(required = false) List<MultipartFile> topperImages,
                                             @RequestParam String role,
                                             @RequestParam String email) {
        try {
            WebTopper topper = new WebTopper();
            topper.setName(name);
            topper.setTotalMarks(totalMarks);
            topper.setPost(post);
            topper.setRank(rank);
            topper.setYear(year);
            topper.setTopperColor(topperColor);

            WebTopper updated = service.updateWebTopper(id, topper, topperImages, role, email);
            return ResponseEntity.ok(updated);
        } catch (AccessDeniedException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error updating topper: " + e.getMessage());
        }
    }

    @DeleteMapping("/deleteWebTopper/{id}")
    public ResponseEntity<?> deleteWebTopper(@PathVariable Long id,
                                             @RequestParam String role,
                                             @RequestParam String email) {
        try {
            service.deleteWebTopperById(id, role, email);
            return ResponseEntity.ok("Topper deleted successfully.");
        } catch (AccessDeniedException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @GetMapping("/getAllWebToppers")
    public ResponseEntity<?> getAllWebToppers(@RequestParam String role,
                                              @RequestParam String email,
                                              @RequestParam String branchCode) {
        try {
            List<WebTopper> toppers = service.getAllWebToppers(role, email, branchCode);
            return ResponseEntity.ok(toppers);
        } catch (AccessDeniedException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
        }
    }

    @GetMapping("/getWebTopperById/{id}")
    public ResponseEntity<?> getWebTopperById(@PathVariable Long id,
                                              @RequestParam String role,
                                              @RequestParam String email) {
        try {
            WebTopper topper = service.getWebTopperById(id, role, email);
            return ResponseEntity.ok(topper);
        } catch (AccessDeniedException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @GetMapping("/getWebTopperByEmail")
    public ResponseEntity<?> getWebTopperByEmail(@RequestParam String email) {
        try {
            return service.getWebTopperByEmail(email)
                    .map(ResponseEntity::ok)
                    .orElse(ResponseEntity.notFound().build());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error fetching topper: " + e.getMessage());
        }
    }

    @GetMapping("/getWebToppersByDynamicPart")
    public ResponseEntity<?> getWebToppersByDynamicPart(@RequestParam String dynamicPart) {
        try {
            List<WebTopper> toppers = service.getToppersByDynamicPart(dynamicPart);
            return ResponseEntity.ok(toppers);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error fetching toppers: " + e.getMessage());
        }
    }

    @GetMapping("/public/getWebTopper")
    public ResponseEntity<?> getFirstTopper(@RequestParam String dynamicPart) {
        try {
            WebTopper topper = service.getUserTopperByDynamicPart(dynamicPart);
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "data", Map.of(
                            "id", topper.getTopperId(),
                            "name", topper.getName(),
                            "totalMarks", topper.getTotalMarks(),
                            "post", topper.getPost(),
                            "rank", topper.getRank(),
                            "year", topper.getYear(),
                            "images", topper.getTopperImages(),
                            "imageIds", topper.getImageUrlIds(),
                            "color", topper.getTopperColor()
                    )
            ));
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("success", false, "message", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("success", false, "message", e.getMessage()));
        }
    }
}