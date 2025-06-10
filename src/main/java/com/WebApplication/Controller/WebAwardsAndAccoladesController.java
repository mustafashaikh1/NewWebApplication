package com.WebApplication.Controller;

import com.WebApplication.Entity.WebAwardsAndAccolades;
import com.WebApplication.Service.WebAwardsAndAccoladesService;
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
public class WebAwardsAndAccoladesController {

    @Autowired
    private WebAwardsAndAccoladesService service;

    @PostMapping("/createAward")
    public ResponseEntity<?> createAward(
            @RequestParam String awardName,
            @RequestParam String description,
            @RequestParam String awardedBy,
            @RequestParam String awardTo,
            @RequestParam int year,
            @RequestParam(required = false) MultipartFile awardImage,
            @RequestParam String role,
            @RequestParam String email,
            @RequestParam String dynamicPart) {

        try {
            WebAwardsAndAccolades award = new WebAwardsAndAccolades();
            award.setAwardName(awardName);
            award.setDescription(description);
            award.setAwardedBy(awardedBy);
            award.setAwardTo(awardTo);
            award.setYear(year);

            WebAwardsAndAccolades created = service.createAward(award, awardImage, role, email, dynamicPart);
            return ResponseEntity.status(HttpStatus.CREATED).body(created);
        } catch (AccessDeniedException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error creating award: " + e.getMessage());
        }
    }

    @PostMapping("/public/createAward")
    public ResponseEntity<?> publicCreateAward(
            @RequestParam String awardName,
            @RequestParam String description,
            @RequestParam String awardedBy,
            @RequestParam String awardTo,
            @RequestParam int year,
            @RequestParam(required = false) MultipartFile awardImage,
            @RequestParam String dynamicPart) {

        try {
            WebAwardsAndAccolades created = service.publicCreateAward(
                    awardName, description, awardedBy, awardTo, year, awardImage, dynamicPart);
            return ResponseEntity.status(HttpStatus.CREATED).body(created);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error creating award: " + e.getMessage());
        }
    }

    @GetMapping("/getAllAwards")
    public ResponseEntity<?> getAllAwards(
            @RequestParam String role,
            @RequestParam String email,
            @RequestParam String branchCode) {
        try {
            List<WebAwardsAndAccolades> awards = service.getAllAwards(role, email, branchCode);
            return ResponseEntity.ok(awards);
        } catch (AccessDeniedException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
        }
    }

    @PutMapping("/updateAward/{id}")
    public ResponseEntity<?> updateAward(
            @PathVariable Long id,
            @RequestParam String awardName,
            @RequestParam String description,
            @RequestParam String awardedBy,
            @RequestParam String awardTo,
            @RequestParam int year,
            @RequestParam(required = false) MultipartFile awardImage,
            @RequestParam String role,
            @RequestParam String email) {

        try {
            WebAwardsAndAccolades updatedAward = new WebAwardsAndAccolades();
            updatedAward.setAwardName(awardName);
            updatedAward.setDescription(description);
            updatedAward.setAwardedBy(awardedBy);
            updatedAward.setAwardTo(awardTo);
            updatedAward.setYear(year);

            WebAwardsAndAccolades result = service.updateAward(id, updatedAward, awardImage, role, email);
            return ResponseEntity.ok(result);
        } catch (AccessDeniedException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error updating award: " + e.getMessage());
        }
    }

    @DeleteMapping("/deleteAward/{id}")
    public ResponseEntity<?> deleteAward(
            @PathVariable Long id,
            @RequestParam String role,
            @RequestParam String email) {
        try {
            service.deleteAward(id, role, email);
            return ResponseEntity.ok("Award deleted successfully.");
        } catch (AccessDeniedException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @GetMapping("/getAwardById/{id}")
    public ResponseEntity<?> getAwardById(
            @PathVariable Long id,
            @RequestParam String role,
            @RequestParam String email) {
        try {
            WebAwardsAndAccolades award = service.getAwardById(id, role, email);
            return ResponseEntity.ok(award);
        } catch (AccessDeniedException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @GetMapping("/getAwardsByEmail")
    public ResponseEntity<?> getAwardsByEmail(@RequestParam String email) {
        try {
            return ResponseEntity.ok(service.getAwardsByEmail(email));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error fetching awards: " + e.getMessage());
        }
    }

    @GetMapping("/getAwardsByDynamicPart")
    public ResponseEntity<?> getAwardsByDynamicPart(@RequestParam String dynamicPart) {
        try {
            List<WebAwardsAndAccolades> awards = service.getAwardsByDynamicPart(dynamicPart);
            return ResponseEntity.ok(awards);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error fetching awards: " + e.getMessage());
        }
    }

    @GetMapping("/public/getAward")
    public ResponseEntity<?> getFirstAward(@RequestParam String dynamicPart) {
        try {
            WebAwardsAndAccolades award = service.getUserAwardByDynamicPart(dynamicPart);
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "data", Map.of(
                            "id", award.getId(),
                            "awardName", award.getAwardName(),
                            "description", award.getDescription(),
                            "awardedBy", award.getAwardedBy(),
                            "awardTo", award.getAwardTo(),
                            "year", award.getYear(),
                            "awardImage", award.getAwardImage()
                    )
            ));
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("success", false, "message", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("success", false, "message", e.getMessage()));
        }
    }
}