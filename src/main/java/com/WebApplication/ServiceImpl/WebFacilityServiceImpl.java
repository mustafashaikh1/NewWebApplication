package com.WebApplication.ServiceImpl;

import com.WebApplication.Entity.WebFacility;
import com.WebApplication.Entity.WebUrlMapping;
import com.WebApplication.Repository.WebFacilityRepository;
import com.WebApplication.Repository.WebUrlMappingRepository;
import com.WebApplication.Service.WebFacilityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@Service
public class WebFacilityServiceImpl implements WebFacilityService {

    private final WebFacilityRepository repository;
    private final PermissionServiceImpl permissionService;
    private final S3Service s3Service;
    private final WebUrlMappingRepository webUrlMappingRepository;

    @Autowired
    public WebFacilityServiceImpl(WebFacilityRepository repository,
                                  PermissionServiceImpl permissionService,
                                  S3Service s3Service,
                                  WebUrlMappingRepository webUrlMappingRepository) {
        this.repository = repository;
        this.permissionService = permissionService;
        this.s3Service = s3Service;
        this.webUrlMappingRepository = webUrlMappingRepository;
    }

    @Override
    public WebFacility createWebFacility(WebFacility webFacility, MultipartFile facilityImage,
                                         String role, String email, String dynamicPart) throws IOException {
        if (!permissionService.hasPermission(role, email, "POST")) {
            throw new AccessDeniedException("No permission to create WebFacility");
        }

        try {
            String branchCode = permissionService.fetchBranchCodeByRole(role, email);
            webFacility.setRole(role);
            webFacility.setCreatedByEmail(email);
            webFacility.setBranchCode(branchCode);

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

            webFacility.setWebUrlMapping(webUrlMapping);

            if (facilityImage != null && !facilityImage.isEmpty()) {
                String imageUrl = s3Service.uploadImage(facilityImage);
                webFacility.setFacilityImage(imageUrl);
            }

            return repository.save(webFacility);
        } catch (IOException e) {
            throw new RuntimeException("Failed to upload image", e);
        }
    }

    @Override
    public WebFacility publicCreateWebFacility(String facilityName, Byte experienceInYear, String subject,
                                               String facilityEducation, String description, String facilityColor,
                                               MultipartFile facilityImage, String dynamicPart) throws IOException {
        try {
            WebFacility webFacility = new WebFacility();
            webFacility.setFacilityName(facilityName);
            webFacility.setExperienceInYear(experienceInYear);
            webFacility.setSubject(subject);
            webFacility.setFacilityEducation(facilityEducation);
            webFacility.setDescription(description);
            webFacility.setFacilityColor(facilityColor);
            webFacility.setRole("PUBLIC_USER");
            webFacility.setCreatedByEmail("public@noreply.com");
            webFacility.setBranchCode("PUBLIC");

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

            webFacility.setWebUrlMapping(webUrlMapping);

            if (facilityImage != null && !facilityImage.isEmpty()) {
                String imageUrl = s3Service.uploadImage(facilityImage);
                webFacility.setFacilityImage(imageUrl);
            }

            return repository.save(webFacility);
        } catch (IOException e) {
            throw new RuntimeException("Failed to upload image", e);
        }
    }

    @Override
    public WebFacility updateWebFacility(Long id, WebFacility updatedWebFacility,
                                         MultipartFile facilityImage, String role, String email) throws IOException {
        if (!permissionService.hasPermission(role, email, "PUT")) {
            throw new AccessDeniedException("No permission to update WebFacility");
        }

        WebFacility existing = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("WebFacility not found"));

        // Update fields
        existing.setFacilityName(updatedWebFacility.getFacilityName());
        existing.setExperienceInYear(updatedWebFacility.getExperienceInYear());
        existing.setSubject(updatedWebFacility.getSubject());
        existing.setFacilityEducation(updatedWebFacility.getFacilityEducation());
        existing.setDescription(updatedWebFacility.getDescription());
        existing.setFacilityColor(updatedWebFacility.getFacilityColor());

        if (facilityImage != null && !facilityImage.isEmpty()) {
            String imageUrl = s3Service.uploadImage(facilityImage);
            existing.setFacilityImage(imageUrl);
        }

        return repository.save(existing);
    }

    @Override
    public void deleteWebFacility(Long id, String role, String email) {
        if (!permissionService.hasPermission(role, email, "DELETE")) {
            throw new AccessDeniedException("No permission to delete WebFacility");
        }

        repository.findById(id)
                .orElseThrow(() -> new RuntimeException("WebFacility not found"));

        repository.deleteById(id);
    }

    @Override
    public WebFacility getWebFacilityById(Long id, String role, String email) {
        if (!permissionService.hasPermission(role, email, "GET")) {
            throw new AccessDeniedException("No permission to view WebFacility");
        }

        return repository.findById(id)
                .orElseThrow(() -> new RuntimeException("WebFacility not found"));
    }

    @Override
    public List<WebFacility> getAllWebFacilities(String role, String email, String branchCode) {
        if (!permissionService.hasPermission(role, email, "GET")) {
            throw new AccessDeniedException("No permission to view WebFacility list");
        }
        return repository.findAllByBranchCode(branchCode);
    }

    @Override
    public Optional<WebFacility> getWebFacilityByEmail(String email) {
        return repository.findByCreatedByEmail(email);
    }

    @Override
    public List<WebFacility> getWebFacilitiesByDynamicPart(String dynamicPart) {
        return repository.findByWebUrlMapping_DynamicPart(dynamicPart);
    }

    @Override
    public void addFacilityColorByBranchCode(String branchCode, String facilityColor) {
        repository.addFacilityColorByBranchCode(branchCode, facilityColor);
    }

    @Override
    public void updateFacilityColorByBranchCode(String branchCode, String facilityColor) {
        repository.updateFacilityColorByBranchCode(branchCode, facilityColor);
    }

    @Override
    public void deleteFacilityColorByBranchCode(String branchCode) {
        repository.deleteFacilityColorByBranchCode(branchCode);
    }

    @Override
    public List<WebFacility> getFacilitiesByDynamicPart(String dynamicPart) {
        return repository.findByWebUrlMapping_DynamicPart(dynamicPart);
    }

    @Override
    public WebFacility getUserFacilityByDynamicPart(String dynamicPart) {
        return repository.findFirstByWebUrlMapping_DynamicPart(dynamicPart)
                .orElseThrow(() -> new NoSuchElementException("No facility found for dynamicPart: " + dynamicPart));
    }
}