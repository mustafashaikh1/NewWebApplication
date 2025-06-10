package com.WebApplication.Controller;

import com.WebApplication.Entity.WebFacility;
import com.WebApplication.Service.WebFacilityService;
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
public class WebFacilityController {

    @Autowired
    private WebFacilityService webFacilityService;

    @PostMapping("/createWebFacility")
    public ResponseEntity<?> createWebFacility(
            @RequestParam String facilityName,
            @RequestParam Byte experienceInYear,
            @RequestParam String subject,
            @RequestParam String facilityEducation,
            @RequestParam String description,
            @RequestParam String facilityColor,
            @RequestParam(required = false) MultipartFile facilityImage,
            @RequestParam String role,
            @RequestParam String email,
            @RequestParam String dynamicPart) {

        try {
            WebFacility webFacility = new WebFacility();
            webFacility.setFacilityName(facilityName);
            webFacility.setExperienceInYear(experienceInYear);
            webFacility.setSubject(subject);
            webFacility.setFacilityEducation(facilityEducation);
            webFacility.setDescription(description);
            webFacility.setFacilityColor(facilityColor);

            WebFacility created = webFacilityService.createWebFacility(webFacility, facilityImage, role, email, dynamicPart);
            return ResponseEntity.status(HttpStatus.CREATED).body(created);
        } catch (AccessDeniedException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error creating WebFacility: " + e.getMessage());
        }
    }

    @PostMapping("/public/CreateWebFacility")
    public ResponseEntity<?> publicCreateWebFacility(
            @RequestParam String facilityName,
            @RequestParam Byte experienceInYear,
            @RequestParam String subject,
            @RequestParam String facilityEducation,
            @RequestParam String description,
            @RequestParam String facilityColor,
            @RequestParam(required = false) MultipartFile facilityImage,
            @RequestParam String dynamicPart) {

        try {
            WebFacility created = webFacilityService.publicCreateWebFacility(
                    facilityName, experienceInYear, subject, facilityEducation,
                    description, facilityColor, facilityImage, dynamicPart);
            return ResponseEntity.status(HttpStatus.CREATED).body(created);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error creating WebFacility: " + e.getMessage());
        }
    }

    @GetMapping("/getAllWebFacilities")
    public ResponseEntity<?> getAllWebFacilities(
            @RequestParam String role,
            @RequestParam String email,
            @RequestParam String branchCode) {
        try {
            List<WebFacility> webFacilities = webFacilityService.getAllWebFacilities(role, email, branchCode);
            return ResponseEntity.ok(webFacilities);
        } catch (AccessDeniedException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
        }
    }

    @PutMapping("/updateWebFacility/{id}")
    public ResponseEntity<?> updateWebFacility(
            @PathVariable Long id,
            @RequestParam String facilityName,
            @RequestParam Byte experienceInYear,
            @RequestParam String subject,
            @RequestParam String facilityEducation,
            @RequestParam String description,
            @RequestParam String facilityColor,
            @RequestParam(required = false) MultipartFile facilityImage,
            @RequestParam String role,
            @RequestParam String email) {

        try {
            WebFacility updatedWebFacility = new WebFacility();
            updatedWebFacility.setFacilityName(facilityName);
            updatedWebFacility.setExperienceInYear(experienceInYear);
            updatedWebFacility.setSubject(subject);
            updatedWebFacility.setFacilityEducation(facilityEducation);
            updatedWebFacility.setDescription(description);
            updatedWebFacility.setFacilityColor(facilityColor);

            WebFacility result = webFacilityService.updateWebFacility(id, updatedWebFacility, facilityImage, role, email);
            return ResponseEntity.ok(result);
        } catch (AccessDeniedException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error updating WebFacility: " + e.getMessage());
        }
    }

    @DeleteMapping("/deleteWebFacility/{id}")
    public ResponseEntity<?> deleteWebFacility(
            @PathVariable Long id,
            @RequestParam String role,
            @RequestParam String email) {
        try {
            webFacilityService.deleteWebFacility(id, role, email);
            return ResponseEntity.ok("WebFacility deleted successfully.");
        } catch (AccessDeniedException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @GetMapping("/getWebFacilityById/{id}")
    public ResponseEntity<?> getWebFacilityById(
            @PathVariable Long id,
            @RequestParam String role,
            @RequestParam String email) {
        try {
            WebFacility webFacility = webFacilityService.getWebFacilityById(id, role, email);
            return ResponseEntity.ok(webFacility);
        } catch (AccessDeniedException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @GetMapping("/getWebFacilityByEmail")
    public ResponseEntity<?> getWebFacilityByEmail(@RequestParam String email) {
        try {
            return webFacilityService.getWebFacilityByEmail(email)
                    .map(ResponseEntity::ok)
                    .orElse(ResponseEntity.notFound().build());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error fetching WebFacility: " + e.getMessage());
        }
    }

    @GetMapping("/getWebFacilitiesByDynamicPart")
    public ResponseEntity<?> getWebFacilitiesByDynamicPart(@RequestParam String dynamicPart) {
        try {
            List<WebFacility> facilities = webFacilityService.getWebFacilitiesByDynamicPart(dynamicPart);
            return ResponseEntity.ok(facilities);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error fetching WebFacilities: " + e.getMessage());
        }
    }

    @PostMapping("/addWebFacilityColor")
    public ResponseEntity<?> addWebFacilityColor(
            @RequestParam String branchCode,
            @RequestParam String facilityColor,
            @RequestParam String role,
            @RequestParam String email) {

        try {
            webFacilityService.addFacilityColorByBranchCode(branchCode, facilityColor);
            return ResponseEntity.ok("Facility color added successfully for branch " + branchCode);
        } catch (AccessDeniedException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
        }
    }

    @PutMapping("/updateWebFacilityColor")
    public ResponseEntity<?> updateWebFacilityColor(
            @RequestParam String branchCode,
            @RequestParam String facilityColor,
            @RequestParam String role,
            @RequestParam String email) {

        try {
            webFacilityService.updateFacilityColorByBranchCode(branchCode, facilityColor);
            return ResponseEntity.ok("Facility color updated successfully for branch: " + branchCode);
        } catch (AccessDeniedException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
        }
    }

    @DeleteMapping("/deleteWebFacilityColor")
    public ResponseEntity<?> deleteWebFacilityColor(
            @RequestParam String branchCode,
            @RequestParam String role,
            @RequestParam String email) {

        try {
            webFacilityService.deleteFacilityColorByBranchCode(branchCode);
            return ResponseEntity.ok("Facility color deleted successfully for branch: " + branchCode);
        } catch (AccessDeniedException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
        }
    }

    @GetMapping("/getWebFacilityByDynamicPart")
    public ResponseEntity<?> getFacilities(@RequestParam String dynamicPart) {
        try {
            List<WebFacility> list = webFacilityService.getFacilitiesByDynamicPart(dynamicPart);
            return ResponseEntity.ok(list);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error: " + e.getMessage());
        }
    }

    @GetMapping("/public/getWebFacility")
    public ResponseEntity<?> getFirstFacility(@RequestParam String dynamicPart) {
        try {
            WebFacility facility = webFacilityService.getUserFacilityByDynamicPart(dynamicPart);
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "data", Map.of(
                            "id", facility.getWebFacilityId(),
                            "name", facility.getFacilityName(),
                            "education", facility.getFacilityEducation(),
                            "image", facility.getFacilityImage(),
                            "experience", facility.getExperienceInYear(),
                            "subject", facility.getSubject(),
                            "description", facility.getDescription(),
                            "facilityColor", facility.getFacilityColor()
                    )
            ));
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("success", false, "message", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("success", false, "message", e.getMessage()));
        }
    }
}