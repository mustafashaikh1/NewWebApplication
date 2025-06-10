package com.WebApplication.ServiceImpl;

import com.WebApplication.Entity.ContactForm;
import com.WebApplication.Entity.WebUrlMapping;
import com.WebApplication.Repository.ContactFormRepository;
import com.WebApplication.Repository.WebUrlMappingRepository;
import com.WebApplication.Service.WebContactFormService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;

@Service
public class WebContactFormServiceImpl implements WebContactFormService {

    private final ContactFormRepository contactFormRepository;
    private final WebUrlMappingRepository webUrlMappingRepository;
    private final PermissionServiceImpl permissionService;

    @Autowired
    public WebContactFormServiceImpl(
            ContactFormRepository contactFormRepository,
            WebUrlMappingRepository webUrlMappingRepository,
            PermissionServiceImpl permissionService) {
        this.contactFormRepository = contactFormRepository;
        this.webUrlMappingRepository = webUrlMappingRepository;
        this.permissionService = permissionService;
    }

    @Override
    public ContactForm createContactForm(ContactForm contactForm, String role, String createdByEmail, String dynamicPart) {
        if (!permissionService.hasPermission(role, createdByEmail, "POST")) {
            throw new AccessDeniedException("No permission to create contact form");
        }

        String branchCode = permissionService.fetchBranchCodeByRole(role, createdByEmail);
        contactForm.setRole(role);
        contactForm.setCreatedByEmail(createdByEmail);
        contactForm.setBranchCode(branchCode);

        // Find or create WebUrlMapping
        WebUrlMapping webUrlMapping = webUrlMappingRepository.findByDynamicPart(dynamicPart)
                .orElseGet(() -> {
                    WebUrlMapping newMapping = new WebUrlMapping();
                    newMapping.setDynamicPart(dynamicPart);
                    newMapping.setCreatedByEmail(createdByEmail);
                    newMapping.setRole(role);
                    newMapping.setBranchCode(branchCode);
                    return webUrlMappingRepository.save(newMapping);
                });

        contactForm.setWebUrlMapping(webUrlMapping);
        return contactFormRepository.save(contactForm);
    }

    @Override
    public ContactForm publicCreateContactForm(ContactForm contactForm, String dynamicPart) {
        contactForm.setRole("PUBLIC_USER");
        contactForm.setCreatedByEmail("public@noreply.com");
        contactForm.setBranchCode("PUBLIC");

        // Find or create WebUrlMapping
        WebUrlMapping webUrlMapping = webUrlMappingRepository.findByDynamicPart(dynamicPart)
                .orElseGet(() -> {
                    WebUrlMapping newMapping = new WebUrlMapping();
                    newMapping.setDynamicPart(dynamicPart);
                    newMapping.setCreatedByEmail("public@noreply.com");
                    newMapping.setRole("PUBLIC_USER");
                    newMapping.setBranchCode("PUBLIC");
                    return webUrlMappingRepository.save(newMapping);
                });

        contactForm.setWebUrlMapping(webUrlMapping);
        return contactFormRepository.save(contactForm);
    }

    @Override
    public ContactForm updateContactForm(Long id, ContactForm updatedContactForm, String role, String createdByEmail) {
        if (!permissionService.hasPermission(role, createdByEmail, "PUT")) {
            throw new AccessDeniedException("No permission to update contact form");
        }

        ContactForm existing = contactFormRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Contact form not found"));

        // Update fields
        existing.setName(updatedContactForm.getName());
        existing.setMobileNo(updatedContactForm.getMobileNo());
        existing.setCourse(updatedContactForm.getCourse());
        existing.setContactEmail(updatedContactForm.getContactEmail());
        existing.setAcademicYear(updatedContactForm.getAcademicYear());
        existing.setDescription(updatedContactForm.getDescription());

        return contactFormRepository.save(existing);
    }

    @Override
    public void deleteContactForm(Long id, String role, String email) {
        if (!permissionService.hasPermission(role, email, "DELETE")) {
            throw new AccessDeniedException("No permission to delete contact form");
        }

        contactFormRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Contact form not found"));

        contactFormRepository.deleteById(id);
    }

    @Override
    public ContactForm getContactFormById(Long id, String role, String email) {
        if (!permissionService.hasPermission(role, email, "GET")) {
            throw new AccessDeniedException("No permission to view contact form");
        }

        return contactFormRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Contact form not found"));
    }

    @Override
    public List<ContactForm> getAllContactForms(String role, String createdByEmail, String branchCode) {
        if (!permissionService.hasPermission(role, createdByEmail, "GET")) {
            throw new AccessDeniedException("No permission to view contact form list");
        }
        return contactFormRepository.findAllByBranchCode(branchCode);
    }

    @Override
    public List<ContactForm> getContactFormsByEmail(String email) {
        return contactFormRepository.findByCreatedByEmail(email);
    }

    @Override
    public List<ContactForm> getContactFormsByDynamicPart(String dynamicPart) {
        return contactFormRepository.findByWebUrlMapping_DynamicPart(dynamicPart);
    }

    @Override
    public ContactForm getUserContactFormByDynamicPart(String dynamicPart) {
        return (ContactForm) contactFormRepository.findFirstByWebUrlMapping_DynamicPart(dynamicPart)
                .orElseThrow(() -> new NoSuchElementException("No contact form found for dynamicPart: " + dynamicPart));
    }
}