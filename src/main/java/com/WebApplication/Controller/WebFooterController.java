package com.WebApplication.Controller;

import com.WebApplication.Entity.WebFooter;
import com.WebApplication.Service.WebFooterService;
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
public class WebFooterController {

    private final WebFooterService webFooterService;

    @Autowired
    public WebFooterController(WebFooterService webFooterService) {
        this.webFooterService = webFooterService;
    }

    @PostMapping("/createWebFooter")
    public ResponseEntity<?> createWebFooter(
            @RequestParam String title,
            @RequestParam String footerColor,
            @RequestParam(required = false) String instagramLink,
            @RequestParam(required = false) String facebookLink,
            @RequestParam(required = false) String twitterLink,
            @RequestParam(required = false) String youtubeLink,
            @RequestParam(required = false) String whatsappLink,
            @RequestParam String email,
            @RequestParam String mobileNumber,
            @RequestParam String address,
            @RequestParam String role,
            @RequestParam String createdByEmail,
            @RequestParam String dynamicPart) {

        try {
            WebFooter footer = new WebFooter();
            footer.setTitle(title);
            footer.setFooterColor(footerColor);
            footer.setInstagramLink(instagramLink);
            footer.setFacebookLink(facebookLink);
            footer.setTwitterLink(twitterLink);
            footer.setYoutubeLink(youtubeLink);
            footer.setWhatsappLink(whatsappLink);
            footer.setEmail(email);
            footer.setMobileNumber(mobileNumber);
            footer.setAddress(address);
            footer.setCreatedByEmail(createdByEmail);

            WebFooter created = webFooterService.createWebFooter(footer, role, createdByEmail, dynamicPart);
            return ResponseEntity.status(HttpStatus.CREATED).body(Map.of(
                    "success", true,
                    "message", "Footer created successfully",
                    "data", created
            ));
        } catch (AccessDeniedException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of(
                    "success", false,
                    "message", e.getMessage()
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                    "success", false,
                    "message", "Error creating footer: " + e.getMessage()
            ));
        }
    }

    @PostMapping("/public/createWebFooter")
    public ResponseEntity<?> publicCreateWebFooter(
            @RequestParam String title,
            @RequestParam String footerColor,
            @RequestParam(required = false) String instagramLink,
            @RequestParam(required = false) String facebookLink,
            @RequestParam(required = false) String twitterLink,
            @RequestParam(required = false) String youtubeLink,
            @RequestParam(required = false) String whatsappLink,
            @RequestParam String email,
            @RequestParam String mobileNumber,
            @RequestParam String address,
            @RequestParam String dynamicPart) {

        try {
            WebFooter footer = new WebFooter();
            footer.setTitle(title);
            footer.setFooterColor(footerColor);
            footer.setInstagramLink(instagramLink);
            footer.setFacebookLink(facebookLink);
            footer.setTwitterLink(twitterLink);
            footer.setYoutubeLink(youtubeLink);
            footer.setWhatsappLink(whatsappLink);
            footer.setEmail(email);
            footer.setMobileNumber(mobileNumber);
            footer.setAddress(address);
            footer.setCreatedByEmail("public@noreply.com");

            WebFooter created = webFooterService.publicCreateWebFooter(footer, dynamicPart);
            return ResponseEntity.status(HttpStatus.CREATED).body(Map.of(
                    "success", true,
                    "message", "Public footer created successfully",
                    "data", created
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                    "success", false,
                    "message", "Error creating public footer: " + e.getMessage()
            ));
        }
    }

    @GetMapping("/getAllWebFooters")
    public ResponseEntity<?> getAllWebFooters(
            @RequestParam String role,
            @RequestParam String email,
            @RequestParam String branchCode) {

        try {
            List<WebFooter> footers = webFooterService.getAllWebFooters(role, email, branchCode);
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "data", footers
            ));
        } catch (AccessDeniedException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of(
                    "success", false,
                    "message", e.getMessage()
            ));
        }
    }

    @PutMapping("/updateWebFooter/{id}")
    public ResponseEntity<?> updateWebFooter(
            @PathVariable Long id,
            @RequestBody WebFooter footer,
            @RequestParam String role,
            @RequestParam String email) {

        try {
            WebFooter updated = webFooterService.updateWebFooter(id, footer, role, email);
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "Footer updated successfully",
                    "data", updated
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

    @DeleteMapping("/deleteWebFooter/{id}")
    public ResponseEntity<?> deleteWebFooter(
            @PathVariable Long id,
            @RequestParam String role,
            @RequestParam String email) {

        try {
            webFooterService.deleteWebFooter(id, role, email);
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "Footer deleted successfully"
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

    @GetMapping("/getWebFooterById/{id}")
    public ResponseEntity<?> getWebFooterById(
            @PathVariable Long id,
            @RequestParam String role,
            @RequestParam String email) {

        try {
            WebFooter footer = webFooterService.getWebFooterById(id, role, email);
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "data", footer
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

    @GetMapping("/getWebFooterByEmail")
    public ResponseEntity<?> getWebFooterByEmail(@RequestParam String email) {
        try {
            return webFooterService.getWebFooterByEmail(email)
                    .map(footer -> ResponseEntity.ok(Map.of(
                            "success", true,
                            "data", footer
                    )))
                    .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of(
                            "success", false,
                            "message", "Footer not found for email: " + email
                    )));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                    "success", false,
                    "message", "Error fetching footer: " + e.getMessage()
            ));
        }
    }


    @GetMapping("/getWebFootersByDynamicPart")
    public ResponseEntity<?> getWebFootersByDynamicPart(@RequestParam String dynamicPart) {
        try {
            List<WebFooter> webFooters = webFooterService.getWebFootersByDynamicPart(dynamicPart);
            return ResponseEntity.ok(webFooters);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error fetching WebFooters: " + e.getMessage());
        }
    }

    @GetMapping("/public/getUserWebFooterByDynamicPart")
    public ResponseEntity<?> getUserWebFooterByDynamicPart(@RequestParam String dynamicPart) {
        try {
            WebFooter footer = webFooterService.getWebFooterByDynamicPart(dynamicPart);
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "data", footer
            ));
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of(
                    "success", false,
                    "message", e.getMessage()
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                    "success", false,
                    "message", "Failed to fetch footer: " + e.getMessage()
            ));
        }
    }
}