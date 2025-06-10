package com.WebApplication.Controller;

import com.WebApplication.Entity.WebJobCareerOption;
import com.WebApplication.Service.WebJobCareerOptionService;
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
public class WebJobCareerOptionController {

    @Autowired
    private WebJobCareerOptionService service;

    @PostMapping("/createWebJobCareerOption")
    public ResponseEntity<?> createWebJobCareerOption(
            @RequestParam String title,
            @RequestParam String description,
            @RequestParam String location,
            @RequestParam String salaryRange,
            @RequestParam String responsibilities,
            @RequestParam String jobCareerOptionColor,
            @RequestParam(required = false) String lastDateToApply,
            @RequestParam(required = false) MultipartFile resume,
            @RequestParam String role,
            @RequestParam String email,
            @RequestParam String dynamicPart) {
        try {
            WebJobCareerOption job = new WebJobCareerOption();
            job.setTitle(title);
            job.setDescription(description);
            job.setLocation(location);
            job.setSalaryRange(salaryRange);
            job.setResponsibilities(responsibilities);
            job.setJobCareerOptionColor(jobCareerOptionColor);
            job.setLastDateToApply(lastDateToApply);

            WebJobCareerOption created = service.createWebJobCareerOption(job, resume, role, email, dynamicPart);
            return ResponseEntity.status(HttpStatus.CREATED).body(created);
        } catch (AccessDeniedException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error creating job: " + e.getMessage());
        }
    }

    @PostMapping("/public/createWebJobCareerOption")
    public ResponseEntity<?> publicCreateWebJobCareerOption(
            @RequestParam String title,
            @RequestParam String description,
            @RequestParam String location,
            @RequestParam String salaryRange,
            @RequestParam String responsibilities,
            @RequestParam(required = false) MultipartFile resume,
            @RequestParam String dynamicPart) {
        try {
            WebJobCareerOption created = service.publicCreateWebJobCareerOption(
                    title, description, location, salaryRange, responsibilities, resume, dynamicPart);
            return ResponseEntity.status(HttpStatus.CREATED).body(created);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error creating job: " + e.getMessage());
        }
    }

    @PutMapping("/updateWebJobCareerOption/{id}")
    public ResponseEntity<?> updateWebJobCareerOption(
            @PathVariable Long id,
            @RequestParam String title,
            @RequestParam String description,
            @RequestParam String location,
            @RequestParam String salaryRange,
            @RequestParam String responsibilities,
            @RequestParam String jobCareerOptionColor,
            @RequestParam(required = false) String lastDateToApply,
            @RequestParam(required = false) MultipartFile resume,
            @RequestParam String role,
            @RequestParam String email) {
        try {
            WebJobCareerOption updatedJob = new WebJobCareerOption();
            updatedJob.setTitle(title);
            updatedJob.setDescription(description);
            updatedJob.setLocation(location);
            updatedJob.setSalaryRange(salaryRange);
            updatedJob.setResponsibilities(responsibilities);
            updatedJob.setJobCareerOptionColor(jobCareerOptionColor);
            updatedJob.setLastDateToApply(lastDateToApply);

            WebJobCareerOption result = service.updateWebJobCareerOption(id, updatedJob, resume, role, email);
            return ResponseEntity.ok(result);
        } catch (AccessDeniedException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error updating job: " + e.getMessage());
        }
    }

    @DeleteMapping("/deleteWebJobCareerOption/{id}")
    public ResponseEntity<?> deleteWebJobCareerOption(
            @PathVariable Long id,
            @RequestParam String role,
            @RequestParam String email) {
        try {
            service.deleteWebJobCareerOptionById(id, role, email);
            return ResponseEntity.ok("Job deleted successfully.");
        } catch (AccessDeniedException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @GetMapping("/getAllWebJobCareerOptions")
    public ResponseEntity<?> getAllWebJobCareerOptions(
            @RequestParam String role,
            @RequestParam String email,
            @RequestParam String branchCode) {
        try {
            List<WebJobCareerOption> jobs = service.getAllWebJobCareerOptions(role, email, branchCode);
            return ResponseEntity.ok(jobs);
        } catch (AccessDeniedException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
        }
    }

    @GetMapping("/getWebJobCareerOptionById/{id}")
    public ResponseEntity<?> getWebJobCareerOptionById(
            @PathVariable Long id,
            @RequestParam String role,
            @RequestParam String email) {
        try {
            WebJobCareerOption job = service.getWebJobCareerOptionById(id, role, email);
            return ResponseEntity.ok(job);
        } catch (AccessDeniedException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @GetMapping("/getWebJobCareerOptionByEmail")
    public ResponseEntity<?> getWebJobCareerOptionByEmail(@RequestParam String email) {
        try {
            return service.getWebJobCareerOptionByEmail(email)
                    .map(ResponseEntity::ok)
                    .orElse(ResponseEntity.notFound().build());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error fetching job: " + e.getMessage());
        }
    }

    @GetMapping("/getWebJobCareerOptionsByDynamicPart")
    public ResponseEntity<?> getWebJobCareerOptionsByDynamicPart(@RequestParam String dynamicPart) {
        try {
            List<WebJobCareerOption> jobs = service.getJobsByDynamicPart(dynamicPart);
            return ResponseEntity.ok(jobs);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error fetching jobs: " + e.getMessage());
        }
    }

    @GetMapping("/public/getWebJobCareerOption")
    public ResponseEntity<?> getFirstJob(@RequestParam String dynamicPart) {
        try {
            WebJobCareerOption job = service.getJobByDynamicPart(dynamicPart);
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "data", Map.of(
                            "id", job.getWebJobCareerOptionId(),
                            "title", job.getTitle(),
                            "description", job.getDescription(),
                            "location", job.getLocation(),
                            "salaryRange", job.getSalaryRange(),
                            "responsibilities", job.getResponsibilities(),
                            "resumeUrl", job.getResumeUrl(),
                            "lastDateToApply", job.getLastDateToApply(),
                            "color", job.getJobCareerOptionColor()
                    )
            ));
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("success", false, "message", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("success", false, "message", e.getMessage()));
        }
    }
}