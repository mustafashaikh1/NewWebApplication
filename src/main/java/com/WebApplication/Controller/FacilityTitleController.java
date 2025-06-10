package com.WebApplication.Controller;

import com.WebApplication.Entity.FacilityTitle;
import com.WebApplication.Service.FacilityTitleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.*;

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
public class FacilityTitleController {

    @Autowired
    private FacilityTitleService service;

    @PostMapping("/createFacilityTitle")
    public ResponseEntity<?> createFacilityTitle(
            @RequestParam String facilityTitle,
            @RequestParam String role,
            @RequestParam String email,
            @RequestParam String dynamicPart) {
        try {
            FacilityTitle newTitle = new FacilityTitle();
            newTitle.setFacilityTitle(facilityTitle);

            FacilityTitle created = service.createFacilityTitle(newTitle, role, email, dynamicPart);
            return ResponseEntity.status(HttpStatus.CREATED).body(created);
        } catch (AccessDeniedException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        }
    }

    @PostMapping("/public/createFacilityTitle")
    public ResponseEntity<?> publicCreateFacilityTitle(
            @RequestParam String facilityTitle,
            @RequestParam String dynamicPart) {
        try {
            FacilityTitle created = service.publicCreateFacilityTitle(facilityTitle, dynamicPart);
            return ResponseEntity.status(HttpStatus.CREATED).body(created);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error creating facility title: " + e.getMessage());
        }
    }

    @PutMapping("/updateFacilityTitle/{id}")
    public ResponseEntity<?> updateFacilityTitle(
            @PathVariable Long id,
            @RequestParam String facilityTitle,
            @RequestParam String role,
            @RequestParam String email) {
        try {
            FacilityTitle updatedTitle = new FacilityTitle();
            updatedTitle.setFacilityTitle(facilityTitle);

            FacilityTitle result = service.updateFacilityTitle(id, updatedTitle, role, email);
            return ResponseEntity.ok(result);
        } catch (AccessDeniedException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @DeleteMapping("/deleteFacilityTitle/{id}")
    public ResponseEntity<?> deleteFacilityTitle(
            @PathVariable Long id,
            @RequestParam String role,
            @RequestParam String email) {
        try {
            service.deleteFacilityTitle(id, role, email);
            return ResponseEntity.ok("Facility title deleted successfully.");
        } catch (AccessDeniedException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @GetMapping("/getAllFacilityTitles")
    public ResponseEntity<?> getAllFacilityTitles(
            @RequestParam String role,
            @RequestParam String email,
            @RequestParam String branchCode) {
        try {
            List<FacilityTitle> titles = service.getAllFacilityTitles(role, email, branchCode);
            return ResponseEntity.ok(titles);
        } catch (AccessDeniedException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
        }
    }

    @GetMapping("/getFacilityTitleById/{id}")
    public ResponseEntity<?> getFacilityTitleById(
            @PathVariable Long id,
            @RequestParam String role,
            @RequestParam String email) {
        try {
            FacilityTitle title = service.getFacilityTitleById(id, role, email);
            return ResponseEntity.ok(title);
        } catch (AccessDeniedException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @GetMapping("/getFacilityTitleByEmail")
    public ResponseEntity<?> getFacilityTitleByEmail(@RequestParam String email) {
        try {
            return service.getFacilityTitleByEmail(email)
                    .map(ResponseEntity::ok)
                    .orElse(ResponseEntity.notFound().build());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error fetching facility title: " + e.getMessage());
        }
    }

    @GetMapping("/getFacilityTitlesByDynamicPart")
    public ResponseEntity<?> getFacilityTitlesByDynamicPart(@RequestParam String dynamicPart) {
        try {
            List<FacilityTitle> titles = service.getFacilityTitlesByDynamicPart(dynamicPart);
            return ResponseEntity.ok(titles);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error fetching facility titles: " + e.getMessage());
        }
    }

    @GetMapping("/public/getFacilityTitle")
    public ResponseEntity<?> getFirstFacilityTitle(@RequestParam String dynamicPart) {
        try {
            FacilityTitle title = service.getFacilityTitleByDynamicPart(dynamicPart);
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "data", Map.of(
                            "id", title.getFacilityTitleId(),
                            "facilityTitle", title.getFacilityTitle()
                    )
            ));
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("success", false, "message", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("success", false, "message", e.getMessage()));
        }
    }
}