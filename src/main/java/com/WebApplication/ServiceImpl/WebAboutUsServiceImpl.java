package com.WebApplication.ServiceImpl;

import com.WebApplication.Entity.WebAboutUs;
import com.WebApplication.Entity.WebUrlMapping;
import com.WebApplication.Repository.WebAboutUsRepository;
import com.WebApplication.Repository.WebUrlMappingRepository;
import com.WebApplication.Service.WebAboutUsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@Service
public class WebAboutUsServiceImpl implements WebAboutUsService {

    private final WebAboutUsRepository repository;
    private final PermissionServiceImpl permissionService;
    private final S3Service s3Service;
    private final WebUrlMappingRepository webUrlMappingRepository;

    @Autowired
    public WebAboutUsServiceImpl(WebAboutUsRepository repository,
                                 PermissionServiceImpl permissionService,
                                 S3Service s3Service,
                                 WebUrlMappingRepository webUrlMappingRepository) {
        this.repository = repository;
        this.permissionService = permissionService;
        this.s3Service = s3Service;
        this.webUrlMappingRepository = webUrlMappingRepository;
    }

    @Override
    public WebAboutUs createWebAboutUs(WebAboutUs webAboutUs, MultipartFile aboutUsImage,
                                       String role, String email, String dynamicPart) {

        if (!permissionService.hasPermission(role, email, "POST")) {
            throw new AccessDeniedException("No permission to create WebAboutUs");
        }

        // Check if WebAboutUs already exists for the dynamicPart
        if (repository.findByWebUrlMappingDynamicPart(dynamicPart).isPresent()) {
            throw new IllegalStateException("WebAboutUs already exists for dynamicPart: " + dynamicPart);
        }

        try {
            String branchCode = permissionService.fetchBranchCodeByRole(role, email);
            webAboutUs.setRole(role);
            webAboutUs.setCreatedByEmail(email);
            webAboutUs.setBranchCode(branchCode);

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

            webAboutUs.setWebUrlMapping(webUrlMapping);

            if (aboutUsImage != null && !aboutUsImage.isEmpty()) {
                String imageUrl = s3Service.uploadImage(aboutUsImage);
                webAboutUs.setAboutUsImage(imageUrl);
            }

            return repository.save(webAboutUs);
        } catch (IOException e) {
            throw new RuntimeException("Failed to upload image", e);
        }
    }

    @Override
    public WebAboutUs publicCreateWebAboutUs(String aboutUsTitle, String description,
                                             MultipartFile aboutUsImage, String dynamicPart) {
        // Check if WebAboutUs already exists for this dynamicPart
        if (repository.findByWebUrlMappingDynamicPart(dynamicPart).isPresent()) {
            throw new IllegalStateException("WebAboutUs already exists for dynamicPart: " + dynamicPart);
        }

        try {
            WebAboutUs webAboutUs = new WebAboutUs();
            webAboutUs.setAboutUsTitle(aboutUsTitle);
            webAboutUs.setDescription(description);
            webAboutUs.setRole("PUBLIC_USER");
            webAboutUs.setCreatedByEmail("public@noreply.com");
            webAboutUs.setBranchCode("PUBLIC");

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

            webAboutUs.setWebUrlMapping(webUrlMapping);

            if (aboutUsImage != null && !aboutUsImage.isEmpty()) {
                String imageUrl = s3Service.uploadImage(aboutUsImage);
                webAboutUs.setAboutUsImage(imageUrl);
            }

            return repository.save(webAboutUs);
        } catch (IOException e) {
            throw new RuntimeException("Failed to upload image", e);
        }
    }

    @Override
    public WebAboutUs updateWebAboutUs(Long id, WebAboutUs updatedWebAboutUs, MultipartFile aboutUsImage, String role, String email) {
        if (!permissionService.hasPermission(role, email, "PUT")) {
            throw new AccessDeniedException("No permission to update WebAboutUs");
        }

        WebAboutUs existing = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("WebAboutUs not found"));

        existing.setAboutUsTitle(updatedWebAboutUs.getAboutUsTitle());
        existing.setDescription(updatedWebAboutUs.getDescription());

        try {
            if (aboutUsImage != null && !aboutUsImage.isEmpty()) {
                String imageUrl = s3Service.uploadImage(aboutUsImage);
                existing.setAboutUsImage(imageUrl);
            }

            return repository.save(existing);
        } catch (IOException e) {
            throw new RuntimeException("Failed to upload image", e);
        }
    }

    @Override
    public void deleteWebAboutUs(Long id, String role, String email) {
        if (!permissionService.hasPermission(role, email, "DELETE")) {
            throw new AccessDeniedException("No permission to delete WebAboutUs");
        }

        repository.findById(id)
                .orElseThrow(() -> new RuntimeException("WebAboutUs not found"));

        repository.deleteById(id);
    }

    @Override
    public WebAboutUs getWebAboutUsById(Long id, String role, String email) {
        if (!permissionService.hasPermission(role, email, "GET")) {
            throw new AccessDeniedException("No permission to view WebAboutUs");
        }

        return repository.findById(id)
                .orElseThrow(() -> new RuntimeException("WebAboutUs not found"));
    }

    @Override
    public List<WebAboutUs> getAllWebAboutUs(String role, String email, String branchCode) {
        if (!permissionService.hasPermission(role, email, "GET")) {
            throw new AccessDeniedException("No permission to view WebAboutUs list");
        }
        return repository.findAllByBranchCode(branchCode);
    }

    @Override
    public Optional<WebAboutUs> getWebAboutUsByEmail(String email) {
        return repository.findByCreatedByEmail(email);
    }

    @Override
    public List<WebAboutUs> getWebAboutUsByDynamicPart(String dynamicPart) {
        return repository.findByWebUrlMapping_DynamicPart(dynamicPart);
    }

    @Override
    public WebAboutUs getUserWebAboutUsByDynamicPart(String dynamicPart) {
        return repository.findByWebUrlMappingDynamicPart(dynamicPart)
                .orElseThrow(() -> new NoSuchElementException("No WebAboutUs found for dynamicPart: " + dynamicPart));
    }
}