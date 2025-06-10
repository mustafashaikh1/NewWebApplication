package com.WebApplication.Controller;

import com.WebApplication.Entity.WebVisionMission;
import com.WebApplication.Service.WebVisionMissionService;
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
public class WebVisionMissionController {

    private final WebVisionMissionService service;

    @Autowired
    public WebVisionMissionController(WebVisionMissionService service) {
        this.service = service;
    }

    @PostMapping("/createWebVisionMission")
    public ResponseEntity<?> create(
            @RequestParam String vision,
            @RequestParam String mission,
            @RequestParam String visionmissionColor,
            @RequestParam(required = false) String directorMessage,
            @RequestParam(required = false) String directorName,
            @RequestParam(required = false) String description,
            @RequestParam(required = false) MultipartFile directorImage,
            @RequestParam String role,
            @RequestParam String email,
            @RequestParam String dynamicPart) {
        try {
            WebVisionMission vm = new WebVisionMission();
            vm.setVision(vision);
            vm.setMission(mission);
            vm.setVisionmissionColor(visionmissionColor);
            vm.setDirectorMessage(directorMessage);
            vm.setDirectorName(directorName);
            vm.setDescription(description);

            WebVisionMission created = service.createWebVisionMission(vm, directorImage, role, email, dynamicPart);
            return ResponseEntity.status(HttpStatus.CREATED).body(Map.of(
                    "success", true,
                    "message", "WebVisionMission created successfully",
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
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                    "success", false,
                    "message", "Error creating WebVisionMission: " + e.getMessage()
            ));
        }
    }

    @PostMapping("/public/createWebVisionMission")
    public ResponseEntity<?> publicCreate(
            @RequestParam String vision,
            @RequestParam String mission,
            @RequestParam String visionmissionColor,
            @RequestParam(required = false) String directorMessage,
            @RequestParam(required = false) String directorName,
            @RequestParam(required = false) String description,
            @RequestParam(required = false) MultipartFile directorImage,
            @RequestParam String dynamicPart) {
        try {
            WebVisionMission created = service.publicCreateWebVisionMission(
                    vision, mission, visionmissionColor,
                    directorMessage, directorName, description,
                    directorImage, dynamicPart);

            return ResponseEntity.status(HttpStatus.CREATED).body(Map.of(
                    "success", true,
                    "message", "WebVisionMission created successfully",
                    "data", Map.of(
                            "id", created.getId(),
                            "vision", created.getVision(),
                            "mission", created.getMission(),
                            "visionmissionColor", created.getVisionmissionColor(),
                            "directorMessage", created.getDirectorMessage(),
                            "directorName", created.getDirectorName(),
                            "description", created.getDescription(),
                            "directorImage", created.getDirectorImage(),
                            "dynamicPart", dynamicPart
                    )
            ));
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of(
                    "success", false,
                    "message", e.getMessage()
            ));
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                    "success", false,
                    "message", "Error creating WebVisionMission: " + e.getMessage()
            ));
        }
    }

    @PutMapping("/updateWebVisionMission/{id}")
    public ResponseEntity<?> update(
            @PathVariable Long id,
            @RequestParam(required = false) String vision,
            @RequestParam(required = false) String mission,
            @RequestParam(required = false) String visionmissionColor,
            @RequestParam(required = false) String directorMessage,
            @RequestParam(required = false) String directorName,
            @RequestParam(required = false) String description,
            @RequestParam(required = false) MultipartFile directorImage,
            @RequestParam String role,
            @RequestParam String email) {
        try {
            WebVisionMission vm = new WebVisionMission();
            vm.setVision(vision);
            vm.setMission(mission);
            vm.setVisionmissionColor(visionmissionColor);
            vm.setDirectorMessage(directorMessage);
            vm.setDirectorName(directorName);
            vm.setDescription(description);

            WebVisionMission result = service.updateWebVisionMission(id, vm, directorImage, role, email);
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "WebVisionMission updated successfully",
                    "data", result
            ));
        } catch (AccessDeniedException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of(
                    "success", false,
                    "message", e.getMessage()
            ));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of(
                    "success", false,
                    "message", e.getMessage()
            ));
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                    "success", false,
                    "message", "Error updating WebVisionMission: " + e.getMessage()
            ));
        }
    }

    @DeleteMapping("/deleteWebVisionMission/{id}")
    public ResponseEntity<?> delete(
            @PathVariable Long id,
            @RequestParam String role,
            @RequestParam String email) {
        try {
            service.deleteWebVisionMission(id, role, email);
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "WebVisionMission deleted successfully"
            ));
        } catch (AccessDeniedException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of(
                    "success", false,
                    "message", e.getMessage()
            ));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of(
                    "success", false,
                    "message", e.getMessage()
            ));
        }
    }

    @GetMapping("/getWebVisionMissionById/{id}")
    public ResponseEntity<?> getById(
            @PathVariable Long id,
            @RequestParam String role,
            @RequestParam String email) {
        try {
            WebVisionMission vm = service.getWebVisionMissionById(id, role, email);
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "data", vm
            ));
        } catch (AccessDeniedException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of(
                    "success", false,
                    "message", e.getMessage()
            ));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of(
                    "success", false,
                    "message", e.getMessage()
            ));
        }
    }

    @GetMapping("/getAllWebVisionMissions")
    public ResponseEntity<?> getAll(
            @RequestParam String role,
            @RequestParam String email,
            @RequestParam String branchCode) {
        try {
            List<WebVisionMission> data = service.getAllWebVisionMissions(role, email, branchCode);
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "data", data
            ));
        } catch (AccessDeniedException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of(
                    "success", false,
                    "message", e.getMessage()
            ));
        }
    }

    @GetMapping("/getWebVisionMissionByEmail")
    public ResponseEntity<?> getByEmail(@RequestParam String email) {
        try {
            return service.getWebVisionMissionByEmail(email)
                    .map(vm -> ResponseEntity.ok(Map.of(
                            "success", true,
                            "data", vm
                    )))
                    .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of(
                            "success", false,
                            "message", "No WebVisionMission found for email: " + email
                    )));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                    "success", false,
                    "message", "Error fetching WebVisionMission: " + e.getMessage()
            ));
        }
    }

    @GetMapping("/getWebVisionMissionsByDynamicPart")
    public ResponseEntity<?> getByDynamicPart(@RequestParam String dynamicPart) {
        try {
            List<WebVisionMission> vms = service.getWebVisionMissionsByDynamicPart(dynamicPart);
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "data", vms
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                    "success", false,
                    "message", "Error fetching WebVisionMissions: " + e.getMessage()
            ));
        }
    }

    @GetMapping("/public/getWebVisionMission")
    public ResponseEntity<?> getUserWebVisionMissionByDynamicPart(@RequestParam String dynamicPart) {
        try {
            WebVisionMission vm = service.getUserWebVisionMissionByDynamicPart(dynamicPart);
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "data", Map.of(
                            "id", vm.getId(),
                            "vision", vm.getVision(),
                            "mission", vm.getMission(),
                            "visionmissionColor", vm.getVisionmissionColor(),
                            "directorMessage", vm.getDirectorMessage(),
                            "directorName", vm.getDirectorName(),
                            "description", vm.getDescription(),
                            "directorImage", vm.getDirectorImage(),
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
                    "message", "Failed to fetch WebVisionMission: " + e.getMessage()
            ));
        }
    }
}