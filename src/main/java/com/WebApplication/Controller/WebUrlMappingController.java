package com.WebApplication.Controller;

import com.WebApplication.Entity.WebUrlMapping;
import com.WebApplication.Service.WebUrlMappingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@CrossOrigin(origins = {
        "https://pjsofttech.in",
        "https://live.ooacademy.co.in",
        "https://course.yashodapublication.com",
        "https://lokrajyaacademy.com"
})
public class WebUrlMappingController {

    @Autowired
    private WebUrlMappingService service;

    @PostMapping("/createWebUrl")
    public ResponseEntity<?> create(@RequestParam String dynamicPart,
                                    @RequestParam String role,
                                    @RequestParam String email) {
        try {
            WebUrlMapping mapping = service.create(dynamicPart, role, email);
            return ResponseEntity.ok("Web URL mapping created successfully with ID: " + mapping.getId());
        } catch (AccessDeniedException e) {
            return ResponseEntity.status(403).body("Access denied: " + e.getMessage());
        } catch (RuntimeException e) {
            return ResponseEntity.status(400).body("Failed to create Web URL mapping: " + e.getMessage());
        }
    }

    @PostMapping("/public/createWebUrl")
    public ResponseEntity<?> createPublicWebUrl(@RequestParam String dynamicPart) {
        try {
            WebUrlMapping mapping = service.publicCreate(dynamicPart);
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "Web URL mapping created successfully",
                    "id", mapping.getId()
            ));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of(
                            "success", false,
                            "message", e.getMessage()
                    ));
        }
    }
    @PutMapping("/updateWebUrl/{id}")
    public ResponseEntity<?> update(@PathVariable Long id,
                                    @RequestParam String dynamicPart,
                                    @RequestParam String role,
                                    @RequestParam String email) {
        try {
            WebUrlMapping mapping = service.update(id, dynamicPart, role, email);
            return ResponseEntity.ok(mapping);
        } catch (AccessDeniedException e) {
            return ResponseEntity.status(403).body(e.getMessage());
        }
    }

    @DeleteMapping("/deleteWebUrl/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id,
                                    @RequestParam String role,
                                    @RequestParam String email) {
        try {
            service.delete(id, role, email);
            return ResponseEntity.ok("Deleted successfully");
        } catch (AccessDeniedException e) {
            return ResponseEntity.status(403).body(e.getMessage());
        }
    }

    @GetMapping("/getWebUrlById/{id}")
    public ResponseEntity<?> getById(@PathVariable Long id,
                                     @RequestParam String role,
                                     @RequestParam String email) {
        try {
            return ResponseEntity.ok(service.getById(id, role, email));
        } catch (AccessDeniedException e) {
            return ResponseEntity.status(403).body(e.getMessage());
        }
    }

    @GetMapping("/getAllWebUrl")
    public ResponseEntity<?> getAll(@RequestParam String role,
                                    @RequestParam String email,
                                    @RequestParam String branchCode) {
        try {
            List<WebUrlMapping> mappings = service.getAll(role, email, branchCode);
            return ResponseEntity.ok(mappings);
        } catch (AccessDeniedException e) {
            return ResponseEntity.status(403).body(e.getMessage());
        }
    }

    @GetMapping("/getWebUrlMappingByEmail")
    public ResponseEntity<WebUrlMapping> getByEmail(@RequestParam String email) {
        WebUrlMapping mapping = service.getByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Mapping not found for email: " + email));
        return ResponseEntity.ok(mapping);
    }

    @GetMapping("/public/getWebUrl")
    public ResponseEntity<?> getPublicWebUrl(@RequestParam String dynamicPart) {
        try {
            Optional<WebUrlMapping> mappingOpt = service.findByDynamicPart(dynamicPart);
            if (mappingOpt.isPresent()) {
                WebUrlMapping mapping = mappingOpt.get();
                return ResponseEntity.ok(Map.of(
                        "success", true,
                        "message", "Web URL mapping found",
                        "data", mapping
                ));
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of(
                        "success", false,
                        "message", "Web URL mapping not found for dynamicPart: " + dynamicPart
                ));
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                    "success", false,
                    "message", "Error retrieving Web URL mapping: " + e.getMessage()
            ));
        }
    }


}