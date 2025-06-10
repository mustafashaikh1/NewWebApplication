package com.WebApplication.Controller;

import com.WebApplication.Entity.WebCounter;
import com.WebApplication.Service.WebCounterService;
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
public class WebCounterController {

    @Autowired
    private WebCounterService service;

    @PostMapping("/createWebCounter")
    public ResponseEntity<?> createWebCounter(
            @RequestParam String counterName1,
            @RequestParam int countValue1,
            @RequestParam String counterColor1,
            @RequestParam String counterName2,
            @RequestParam int countValue2,
            @RequestParam String counterColor2,
            @RequestParam String counterName3,
            @RequestParam int countValue3,
            @RequestParam String counterColor3,
            @RequestParam String role,
            @RequestParam String email,
            @RequestParam String dynamicPart) {
        try {
            WebCounter counter = new WebCounter();
            counter.setCounterName1(counterName1);
            counter.setCountValue1(countValue1);
            counter.setCounterColor1(counterColor1);
            counter.setCounterName2(counterName2);
            counter.setCountValue2(countValue2);
            counter.setCounterColor2(counterColor2);
            counter.setCounterName3(counterName3);
            counter.setCountValue3(countValue3);
            counter.setCounterColor3(counterColor3);

            WebCounter created = service.createWebCounter(counter, role, email, dynamicPart);
            return ResponseEntity.status(HttpStatus.CREATED).body(created);
        } catch (AccessDeniedException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error creating counter: " + e.getMessage());
        }
    }

    @PostMapping("/public/createWebCounter")
    public ResponseEntity<?> publicCreateWebCounter(
            @RequestParam String counterName1,
            @RequestParam int countValue1,
            @RequestParam String counterColor1,
            @RequestParam String counterName2,
            @RequestParam int countValue2,
            @RequestParam String counterColor2,
            @RequestParam String counterName3,
            @RequestParam int countValue3,
            @RequestParam String counterColor3,
            @RequestParam String dynamicPart) {
        try {
            WebCounter created = service.publicCreateWebCounter(
                    counterName1, countValue1, counterColor1,
                    counterName2, countValue2, counterColor2,
                    counterName3, countValue3, counterColor3,
                    dynamicPart);
            return ResponseEntity.status(HttpStatus.CREATED).body(created);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error creating counter: " + e.getMessage());
        }
    }

    @PutMapping("/updateWebCounter/{id}")
    public ResponseEntity<?> updateWebCounter(
            @PathVariable Long id,
            @RequestParam String counterName1,
            @RequestParam int countValue1,
            @RequestParam String counterColor1,
            @RequestParam String counterName2,
            @RequestParam int countValue2,
            @RequestParam String counterColor2,
            @RequestParam String counterName3,
            @RequestParam int countValue3,
            @RequestParam String counterColor3,
            @RequestParam String role,
            @RequestParam String email) {
        try {
            WebCounter updatedCounter = new WebCounter();
            updatedCounter.setCounterName1(counterName1);
            updatedCounter.setCountValue1(countValue1);
            updatedCounter.setCounterColor1(counterColor1);
            updatedCounter.setCounterName2(counterName2);
            updatedCounter.setCountValue2(countValue2);
            updatedCounter.setCounterColor2(counterColor2);
            updatedCounter.setCounterName3(counterName3);
            updatedCounter.setCountValue3(countValue3);
            updatedCounter.setCounterColor3(counterColor3);

            WebCounter result = service.updateWebCounter(id, updatedCounter, role, email);
            return ResponseEntity.ok(result);
        } catch (AccessDeniedException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @DeleteMapping("/deleteWebCounter/{id}")
    public ResponseEntity<?> deleteWebCounter(
            @PathVariable Long id,
            @RequestParam String role,
            @RequestParam String email) {
        try {
            service.deleteWebCounterById(id, role, email);
            return ResponseEntity.ok("Counter deleted successfully.");
        } catch (AccessDeniedException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @GetMapping("/getAllWebCounters")
    public ResponseEntity<?> getAllWebCounters(
            @RequestParam String role,
            @RequestParam String email,
            @RequestParam String branchCode) {
        try {
            List<WebCounter> counters = service.getAllWebCounters(role, email, branchCode);
            return ResponseEntity.ok(counters);
        } catch (AccessDeniedException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
        }
    }

    @GetMapping("/getWebCounterById/{id}")
    public ResponseEntity<?> getWebCounterById(
            @PathVariable Long id,
            @RequestParam String role,
            @RequestParam String email) {
        try {
            WebCounter counter = service.getWebCounterById(id, role, email);
            return ResponseEntity.ok(counter);
        } catch (AccessDeniedException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @GetMapping("/getWebCounterByEmail")
    public ResponseEntity<?> getWebCounterByEmail(@RequestParam String email) {
        try {
            return service.getWebCounterByEmail(email)
                    .map(ResponseEntity::ok)
                    .orElse(ResponseEntity.notFound().build());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error fetching counter: " + e.getMessage());
        }
    }

    @GetMapping("/getWebCountersByDynamicPart")
    public ResponseEntity<?> getWebCountersByDynamicPart(@RequestParam String dynamicPart) {
        try {
            List<WebCounter> counters = service.getCountersByDynamicPart(dynamicPart);
            return ResponseEntity.ok(counters);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error fetching counters: " + e.getMessage());
        }
    }

    @GetMapping("/public/getWebCounter")
    public ResponseEntity<?> getFirstCounter(@RequestParam String dynamicPart) {
        try {
            WebCounter counter = service.getCounterByDynamicPart(dynamicPart);
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "data", Map.of(
                            "id", counter.getId(),
                            "counter1", Map.of(
                                    "name", counter.getCounterName1(),
                                    "value", counter.getCountValue1(),
                                    "color", counter.getCounterColor1()
                            ),
                            "counter2", Map.of(
                                    "name", counter.getCounterName2(),
                                    "value", counter.getCountValue2(),
                                    "color", counter.getCounterColor2()
                            ),
                            "counter3", Map.of(
                                    "name", counter.getCounterName3(),
                                    "value", counter.getCountValue3(),
                                    "color", counter.getCounterColor3()
                            )
                    )
            ));
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("success", false, "message", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("success", false, "message", e.getMessage()));
        }
    }
}