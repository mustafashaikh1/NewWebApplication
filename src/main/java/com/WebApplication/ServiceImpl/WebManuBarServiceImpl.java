package com.WebApplication.ServiceImpl;

import com.WebApplication.Entity.WebManuBar;
import com.WebApplication.Entity.WebUrlMapping;
import com.WebApplication.Repository.WebManuBarRepository;
import com.WebApplication.Repository.WebUrlMappingRepository;
import com.WebApplication.Service.WebManuBarService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@Service
public class WebManuBarServiceImpl implements WebManuBarService {

    private final WebManuBarRepository repository;
    private final PermissionServiceImpl permissionService;
    private final S3Service s3Service;
    private final WebUrlMappingRepository webUrlMappingRepository;

    @Autowired
    public WebManuBarServiceImpl(WebManuBarRepository repository,
                                 PermissionServiceImpl permissionService,
                                 S3Service s3Service, WebUrlMappingRepository webUrlMappingRepository) {
        this.repository = repository;
        this.permissionService = permissionService;
        this.s3Service = s3Service;
        this.webUrlMappingRepository = webUrlMappingRepository;
    }

    @Override
    public WebManuBar createWebManuBar(WebManuBar webManuBar, MultipartFile menubarImage,
                                       String role, String email, String dynamicPart) {

        if (!permissionService.hasPermission(role, email, "POST")) {
            throw new AccessDeniedException("No permission to create WebManuBar");
        }

        // Check if WebManuBar already exists for the dynamicPart
        if (repository.findByWebUrlMappingDynamicPart(dynamicPart).isPresent()) {
            throw new IllegalStateException("WebManuBar already exists for dynamicPart: " + dynamicPart);
        }

        try {
            String branchCode = permissionService.fetchBranchCodeByRole(role, email);
            webManuBar.setRole(role);
            webManuBar.setCreatedByEmail(email);
            webManuBar.setBranchCode(branchCode);

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

            webManuBar.setWebUrlMapping(webUrlMapping);

            if (menubarImage != null && !menubarImage.isEmpty()) {
                String imageUrl = s3Service.uploadImage(menubarImage);
                webManuBar.setMenubarImage(imageUrl);
            }

            return repository.save(webManuBar);
        } catch (IOException e) {
            throw new RuntimeException("Failed to upload image", e);
        }
    }
    @Override
    public WebManuBar publicCreateWebManuBar(String manuBarColor, MultipartFile menubarImage, String dynamicPart) {
        // Check if WebManuBar already exists for this dynamicPart
        if (repository.findByWebUrlMappingDynamicPart(dynamicPart).isPresent()) {
            throw new IllegalStateException("WebManuBar already exists for dynamicPart: " + dynamicPart);
        }

        try {
            WebManuBar webManuBar = new WebManuBar();
            webManuBar.setManuBarColor(manuBarColor);
            webManuBar.setRole("PUBLIC_USER");
            webManuBar.setCreatedByEmail("public@noreply.com");
            webManuBar.setBranchCode("PUBLIC");

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

            webManuBar.setWebUrlMapping(webUrlMapping);

            if (menubarImage != null && !menubarImage.isEmpty()) {
                String imageUrl = s3Service.uploadImage(menubarImage);
                webManuBar.setMenubarImage(imageUrl);
            }

            return repository.save(webManuBar);
        } catch (IOException e) {
            throw new RuntimeException("Failed to upload image", e);
        }
    }

    @Override
    public List<WebManuBar> getAllWebManuBars(String role, String email, String branchCode) {
        if (!permissionService.hasPermission(role, email, "GET")) {
            throw new AccessDeniedException("No permission to view WebManuBar list");
        }
        return repository.findAllByBranchCode(branchCode);
    }

    @Override
    public WebManuBar updateWebManuBar(Long id, WebManuBar updatedWebManuBar, MultipartFile menubarImage, String role, String email) throws IOException {
        if (!permissionService.hasPermission(role, email, "PUT")) {
            throw new AccessDeniedException("No permission to update WebManuBar");
        }

        WebManuBar existing = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("WebManuBar not found"));

        existing.setManuBarColor(updatedWebManuBar.getManuBarColor());

        if (menubarImage != null && !menubarImage.isEmpty()) {
            String imageUrl = s3Service.uploadImage(menubarImage);
            existing.setMenubarImage(imageUrl);
        }

        return repository.save(existing);
    }

    @Override
    public void deleteWebManuBar(Long id, String role, String email) {
        if (!permissionService.hasPermission(role, email, "DELETE")) {
            throw new AccessDeniedException("No permission to delete WebManuBar");
        }

        repository.findById(id)
                .orElseThrow(() -> new RuntimeException("WebManuBar not found"));

        repository.deleteById(id);
    }

    @Override
    public WebManuBar getWebManuBarById(Long id, String role, String email) {
        if (!permissionService.hasPermission(role, email, "GET")) {
            throw new AccessDeniedException("No permission to view WebManuBar");
        }

        return repository.findById(id)
                .orElseThrow(() -> new RuntimeException("WebManuBar not found"));
    }

    @Override
    public Optional<WebManuBar> getWebManuBarByEmail(String email) {
        return repository.findByCreatedByEmail(email);
    }

    @Override
    public List<WebManuBar> getWebManuBarsByDynamicPart(String dynamicPart) {
        return repository.findByWebUrlMapping_DynamicPart(dynamicPart);
    }

    @Override
    public WebManuBar getUserWebManuBarByDynamicPart(String dynamicPart) {
        return repository.findByWebUrlMappingDynamicPart(dynamicPart)
                .orElseThrow(() -> new NoSuchElementException("No WebManuBar found for dynamicPart: " + dynamicPart));
    }

}