package com.WebApplication.Controller;

import com.WebApplication.Entity.WebHRDetails;
import com.WebApplication.Service.WebHRDetailsService;
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
public class WebHRDetailsController {

    @Autowired
    private WebHRDetailsService service;

    @PostMapping("/createWebHRDetails")
    public ResponseEntity<?> createWebHRDetails(
            @RequestParam String hrName,
            @RequestParam String email,
            @RequestParam String contact,
            @RequestParam(required = false) MultipartFile image,
            @RequestParam String role,
            @RequestParam String createdByEmail,
            @RequestParam String dynamicPart) {
        try {
            WebHRDetails hrDetails = new WebHRDetails();
            hrDetails.setHrName(hrName);
            hrDetails.setEmail(email);
            hrDetails.setContact(contact);

            WebHRDetails created = service.createWebHRDetails(hrDetails, role, createdByEmail, dynamicPart);
            return ResponseEntity.status(HttpStatus.CREATED).body(created);
        } catch (AccessDeniedException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error creating HR details: " + e.getMessage());
        }
    }

    @PostMapping("/public/createWebHRDetails")
    public ResponseEntity<?> publicCreateWebHRDetails(
            @RequestParam String hrName,
            @RequestParam String email,
            @RequestParam String contact,

            @RequestParam String dynamicPart) {
        try {
            WebHRDetails created = service.publicCreateWebHRDetails(
                    hrName, email, contact,  dynamicPart);
            return ResponseEntity.status(HttpStatus.CREATED).body(created);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error creating HR details: " + e.getMessage());
        }
    }

    @PutMapping("/updateWebHRDetails/{id}")
    public ResponseEntity<?> updateWebHRDetails(
            @PathVariable Long id,
            @RequestParam String hrName,
            @RequestParam String email,
            @RequestParam String contact,
            @RequestParam String role,
            @RequestParam String createdByEmail) {
        try {
            WebHRDetails hrDetails = new WebHRDetails();
            hrDetails.setHrName(hrName);
            hrDetails.setEmail(email);
            hrDetails.setContact(contact);

            WebHRDetails updated = service.updateWebHRDetails(id, hrDetails, role, createdByEmail);
            return ResponseEntity.ok(updated);
        } catch (AccessDeniedException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error updating HR details: " + e.getMessage());
        }
    }

    @DeleteMapping("/deleteWebHRDetails/{id}")
    public ResponseEntity<?> deleteWebHRDetails(
            @PathVariable Long id,
            @RequestParam String role,
            @RequestParam String email) {
        try {
            service.deleteWebHRDetailsById(id, role, email);
            return ResponseEntity.ok("HR details deleted successfully.");
        } catch (AccessDeniedException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @GetMapping("/getAllWebHRDetails")
    public ResponseEntity<?> getAllWebHRDetails(
            @RequestParam String role,
            @RequestParam String email,
            @RequestParam String branchCode) {
        try {
            List<WebHRDetails> hrDetails = service.getAllWebHRDetails(role, email, branchCode);
            return ResponseEntity.ok(hrDetails);
        } catch (AccessDeniedException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
        }
    }

    @GetMapping("/getWebHRDetailsById/{id}")
    public ResponseEntity<?> getWebHRDetailsById(
            @PathVariable Long id,
            @RequestParam String role,
            @RequestParam String email) {
        try {
            WebHRDetails hrDetails = service.getWebHRDetailsById(id, role, email);
            return ResponseEntity.ok(hrDetails);
        } catch (AccessDeniedException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @GetMapping("/getWebHRDetailsByEmail")
    public ResponseEntity<?> getWebHRDetailsByEmail(@RequestParam String email) {
        try {
            return service.getWebHRDetailsByEmail(email)
                    .map(ResponseEntity::ok)
                    .orElse(ResponseEntity.notFound().build());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error fetching HR details: " + e.getMessage());
        }
    }

    @GetMapping("/getWebHRDetailsByDynamicPart")
    public ResponseEntity<?> getWebHRDetailsByDynamicPart(@RequestParam String dynamicPart) {
        try {
            List<WebHRDetails> hrDetails = service.getHRDetailsByDynamicPart(dynamicPart);
            return ResponseEntity.ok(hrDetails);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error fetching HR details: " + e.getMessage());
        }
    }

    @GetMapping("/public/getWebHRDetails")
    public ResponseEntity<?> getFirstHRDetails(@RequestParam String dynamicPart) {
        try {
            WebHRDetails hrDetails = service.getHRDetailByDynamicPart(dynamicPart);
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "data", Map.of(
                            "id", hrDetails.getId(),
                            "hrName", hrDetails.getHrName(),
                            "email", hrDetails.getEmail(),
                            "contact", hrDetails.getContact()

                    )
            ));
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("success", false, "message", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("success", false, "message", e.getMessage()));
        }
    }
}