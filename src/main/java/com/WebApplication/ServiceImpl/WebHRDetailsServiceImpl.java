package com.WebApplication.ServiceImpl;

import com.WebApplication.Entity.WebHRDetails;
import com.WebApplication.Entity.WebUrlMapping;
import com.WebApplication.Repository.WebHRDetailsRepository;
import com.WebApplication.Repository.WebUrlMappingRepository;
import com.WebApplication.Service.WebHRDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@Service
public class WebHRDetailsServiceImpl implements WebHRDetailsService {

    private final WebHRDetailsRepository repository;
    private final PermissionServiceImpl permissionService;
    private final S3Service s3Service;
    private final WebUrlMappingRepository webUrlMappingRepository;

    @Autowired
    public WebHRDetailsServiceImpl(WebHRDetailsRepository repository,
                                   PermissionServiceImpl permissionService,
                                   S3Service s3Service,
                                   WebUrlMappingRepository webUrlMappingRepository) {
        this.repository = repository;
        this.permissionService = permissionService;
        this.s3Service = s3Service;
        this.webUrlMappingRepository = webUrlMappingRepository;
    }

    @Override
    public WebHRDetails createWebHRDetails(WebHRDetails hrDetails,
                                           String role, String email, String dynamicPart) throws IOException {
        if (!permissionService.hasPermission(role, email, "POST")) {
            throw new AccessDeniedException("No permission to create HR details");
        }

        String branchCode = permissionService.fetchBranchCodeByRole(role, email);
        hrDetails.setRole(role);
        hrDetails.setCreatedByEmail(email);
        hrDetails.setBranchCode(branchCode);

        // Find or create WebUrlMapping
        WebUrlMapping webUrlMapping = webUrlMappingRepository.findByDynamicPart(dynamicPart)
                .orElseGet(() -> {
                    WebUrlMapping newMapping = new WebUrlMapping();
                    newMapping.setDynamicPart(dynamicPart);
                    newMapping.setCreatedByEmail(email);
                    newMapping.setRole(role);
                    newMapping.setBranchCode(branchCode);
                    return webUrlMappingRepository.save(newMapping);
                });

        hrDetails.setWebUrlMapping(webUrlMapping);


        return repository.save(hrDetails);
    }

    @Override
    public WebHRDetails publicCreateWebHRDetails(String hrName, String email, String contact,
                                                 String dynamicPart) throws IOException {
        WebHRDetails hrDetails = new WebHRDetails();
        hrDetails.setHrName(hrName);
        hrDetails.setEmail(email);
        hrDetails.setContact(contact);
        hrDetails.setRole("PUBLIC_USER");
        hrDetails.setCreatedByEmail("public@noreply.com");
        hrDetails.setBranchCode("PUBLIC");

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

        hrDetails.setWebUrlMapping(webUrlMapping);


        return repository.save(hrDetails);
    }

    @Override
    public WebHRDetails updateWebHRDetails(Long id, WebHRDetails updated,
                                         String role, String email) throws IOException {
        if (!permissionService.hasPermission(role, email, "PUT")) {
            throw new AccessDeniedException("No permission to update HR details");
        }

        WebHRDetails existing = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("HR details not found"));

        // Update fields
        existing.setHrName(updated.getHrName());
        existing.setEmail(updated.getEmail());
        existing.setContact(updated.getContact());



        return repository.save(existing);
    }

    @Override
    public void deleteWebHRDetailsById(Long id, String role, String email) {
        if (!permissionService.hasPermission(role, email, "DELETE")) {
            throw new AccessDeniedException("No permission to delete HR details");
        }

        repository.findById(id)
                .orElseThrow(() -> new RuntimeException("HR details not found"));

        repository.deleteById(id);
    }

    @Override
    public List<WebHRDetails> getAllWebHRDetails(String role, String email, String branchCode) {
        if (!permissionService.hasPermission(role, email, "GET")) {
            throw new AccessDeniedException("No permission to view HR details");
        }

        return repository.findAllByBranchCode(branchCode);
    }

    @Override
    public WebHRDetails getWebHRDetailsById(Long id, String role, String email) {
        if (!permissionService.hasPermission(role, email, "GET")) {
            throw new AccessDeniedException("No permission to view HR details");
        }

        return repository.findById(id)
                .orElseThrow(() -> new RuntimeException("HR details not found"));
    }

    @Override
    public Optional<WebHRDetails> getWebHRDetailsByEmail(String email) {
        return repository.findByCreatedByEmail(email);
    }

    @Override
    public List<WebHRDetails> getHRDetailsByDynamicPart(String dynamicPart) {
        return repository.findByWebUrlMapping_DynamicPart(dynamicPart);
    }

    @Override
    public WebHRDetails getHRDetailByDynamicPart(String dynamicPart) throws NoSuchElementException {
        return repository.findFirstByWebUrlMapping_DynamicPart(dynamicPart)
                .orElseThrow(() -> new NoSuchElementException("No HR details found for dynamicPart: " + dynamicPart));
    }

    @Override
    public void updateHRDetailsColorByBranchCode(String branchCode, String color, String role, String email) {
        if (!permissionService.hasPermission(role, email, "PUT")) {
            throw new AccessDeniedException("No permission to update HR details color");
        }
        // Implementation depends on your requirements
        // You might need to add a color field to the entity
    }

    @Override
    public void deleteHRDetailsColorByBranchCode(String branchCode, String role, String email) {
        if (!permissionService.hasPermission(role, email, "DELETE")) {
            throw new AccessDeniedException("No permission to delete HR details color");
        }
        // Implementation depends on your requirements
    }
}