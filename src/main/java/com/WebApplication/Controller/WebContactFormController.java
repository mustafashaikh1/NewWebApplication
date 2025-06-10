package com.WebApplication.Controller;

import com.WebApplication.Entity.ContactForm;
import com.WebApplication.Service.WebContactFormService;
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
public class WebContactFormController {

    private final WebContactFormService webContactFormService;

    @Autowired
    public WebContactFormController(WebContactFormService contactFormService) {
        this.webContactFormService = contactFormService;
    }

    @PostMapping("/createContactForm")
    public ResponseEntity<?> createContactForm(
            @RequestParam String name,
            @RequestParam String mobileNo,
            @RequestParam String contactEmail,
            @RequestParam(required = false) String course,
            @RequestParam(required = false) String academicYear,
            @RequestParam(required = false) String description,
            @RequestParam String role,
            @RequestParam String createdByEmail,
            @RequestParam String branchCode) {
        try {
            // Create ContactForm object from parameters
            ContactForm contactForm = new ContactForm();
            contactForm.setName(name);
            contactForm.setMobileNo(mobileNo);
            contactForm.setContactEmail(contactEmail);
            contactForm.setCourse(course);
            contactForm.setAcademicYear(academicYear);
            contactForm.setDescription(description);
            contactForm.setRole(role);
            contactForm.setCreatedByEmail(createdByEmail);
            contactForm.setBranchCode(branchCode);

            String dynamicPart = branchCode; // Using branchCode as dynamicPart

            ContactForm created = webContactFormService.createContactForm(contactForm, role, createdByEmail, dynamicPart);
            return ResponseEntity.status(HttpStatus.CREATED).body(Map.of(
                    "success", true,
                    "message", "Contact form created successfully",
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
                    "message", "Error creating contact form: " + e.getMessage()
            ));
        }
    }

    @PostMapping("/public/createContactForm")
    public ResponseEntity<?> publicCreateContactForm(
            @RequestParam String name,
            @RequestParam String mobileNo,
            @RequestParam String contactEmail,
            @RequestParam(required = false) String course,
            @RequestParam(required = false) String academicYear,
            @RequestParam(required = false) String description,
            @RequestParam String dynamicPart) {
        try {
            // Create ContactForm object from parameters
            ContactForm contactForm = new ContactForm();
            contactForm.setName(name);
            contactForm.setMobileNo(mobileNo);
            contactForm.setContactEmail(contactEmail);
            contactForm.setCourse(course);
            contactForm.setAcademicYear(academicYear);
            contactForm.setDescription(description);

            ContactForm created = webContactFormService.publicCreateContactForm(contactForm, dynamicPart);
            return ResponseEntity.status(HttpStatus.CREATED).body(Map.of(
                    "success", true,
                    "message", "Contact form submitted successfully",
                    "data", Map.of(
                            "id", created.getContactId(),
                            "name", created.getName(),
                            "contactEmail", created.getContactEmail(),
                            "course", created.getCourse(),
                            "mobileNo", created.getMobileNo(),
                            "dynamicPart", dynamicPart
                    )
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                    "success", false,
                    "message", "Error submitting contact form: " + e.getMessage()
            ));
        }
    }

    @GetMapping("/getAllContactForms")
    public ResponseEntity<?> getAllContactForms(
            @RequestParam String role,
            @RequestParam String createdByEmail,
            @RequestParam(required = false) String branchCode) {
        try {
            List<ContactForm> contactForms = webContactFormService.getAllContactForms(role, createdByEmail, branchCode);
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "data", contactForms
            ));
        } catch (AccessDeniedException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of(
                    "success", false,
                    "message", e.getMessage()
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                    "success", false,
                    "message", "Error fetching contact forms: " + e.getMessage()
            ));
        }
    }

    @PutMapping("/updateContactForm/{id}")
    public ResponseEntity<?> updateContactForm(
            @PathVariable Long id,
            @RequestParam String name,
            @RequestParam String mobileNo,
            @RequestParam String contactEmail,
            @RequestParam(required = false) String course,
            @RequestParam(required = false) String academicYear,
            @RequestParam(required = false) String description,
            @RequestParam String role,
            @RequestParam String createdByEmail) {
        try {
            // Create ContactForm object from parameters
            ContactForm contactForm = new ContactForm();
            contactForm.setName(name);
            contactForm.setMobileNo(mobileNo);
            contactForm.setContactEmail(contactEmail);
            contactForm.setCourse(course);
            contactForm.setAcademicYear(academicYear);
            contactForm.setDescription(description);

            ContactForm updated = webContactFormService.updateContactForm(id, contactForm, role, createdByEmail);
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "Contact form updated successfully",
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
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                    "success", false,
                    "message", "Error updating contact form: " + e.getMessage()
            ));
        }
    }

    @DeleteMapping("/deleteContactForm/{id}")
    public ResponseEntity<?> deleteContactForm(
            @PathVariable Long id,
            @RequestParam String role,
            @RequestParam String createdByEmail) {
        try {
            webContactFormService.deleteContactForm(id, role, createdByEmail);
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "Contact form deleted successfully"
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
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                    "success", false,
                    "message", "Error deleting contact form: " + e.getMessage()
            ));
        }
    }

    @GetMapping("/getContactFormById/{id}")
    public ResponseEntity<?> getContactFormById(
            @PathVariable Long id,
            @RequestParam String role,
            @RequestParam String createdByEmail) {
        try {
            ContactForm contactForm = webContactFormService.getContactFormById(id, role, createdByEmail);
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "data", contactForm
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
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                    "success", false,
                    "message", "Error fetching contact form: " + e.getMessage()
            ));
        }
    }

    @GetMapping("/getContactFormsByEmail")
    public ResponseEntity<?> getContactFormsByEmail(@RequestParam String contactEmail) {
        try {
            List<ContactForm> contactForms = webContactFormService.getContactFormsByEmail(contactEmail);
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "data", contactForms
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                    "success", false,
                    "message", "Error fetching contact forms: " + e.getMessage()
            ));
        }
    }

    @GetMapping("/getContactFormsByDynamicPart")
    public ResponseEntity<?> getContactFormsByDynamicPart(@RequestParam String dynamicPart) {
        try {
            List<ContactForm> contactForms = webContactFormService.getContactFormsByDynamicPart(dynamicPart);
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "data", contactForms
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                    "success", false,
                    "message", "Error fetching contact forms: " + e.getMessage()
            ));
        }
    }

    @GetMapping("/public/getContactForm")
    public ResponseEntity<?> getUserContactFormByDynamicPart(@RequestParam String dynamicPart) {
        try {
            ContactForm contactForm = webContactFormService.getUserContactFormByDynamicPart(dynamicPart);
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "data", Map.of(
                            "id", contactForm.getContactId(),
                            "name", contactForm.getName(),
                            "contactEmail", contactForm.getContactEmail(),
                            "course", contactForm.getCourse(),
                            "mobileNo", contactForm.getMobileNo(),
                            "description", contactForm.getDescription(),
                            "academicYear", contactForm.getAcademicYear(),
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
                    "message", "Failed to fetch contact form: " + e.getMessage()
            ));
        }
    }
}