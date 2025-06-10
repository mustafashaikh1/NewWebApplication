package com.WebApplication.ServiceImpl;

import com.WebApplication.Entity.WebSlideBar;
import com.WebApplication.Entity.WebUrlMapping;
import com.WebApplication.Repository.WebSlideBarRepository;
import com.WebApplication.Repository.WebUrlMappingRepository;
import com.WebApplication.Service.WebSlideBarService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@Service
public class WebSlideBarServiceImpl implements WebSlideBarService {

    private final WebSlideBarRepository repository;
    private final PermissionServiceImpl permissionService;
    private final S3Service s3Service;
    private final WebUrlMappingRepository webUrlMappingRepository;

    @Autowired
    public WebSlideBarServiceImpl(WebSlideBarRepository repository,
                                  PermissionServiceImpl permissionService,
                                  S3Service s3Service,
                                  WebUrlMappingRepository webUrlMappingRepository) {
        this.repository = repository;
        this.permissionService = permissionService;
        this.s3Service = s3Service;
        this.webUrlMappingRepository = webUrlMappingRepository;
    }

    @Override
    public WebSlideBar createWebSlideBar(WebSlideBar webSlideBar, List<MultipartFile> slideImages,
                                         String role, String email, String dynamicPart) throws IOException {
        if (!permissionService.hasPermission(role, email, "POST")) {
            throw new AccessDeniedException("No permission to create WebSlideBar");
        }

        // Check if WebSlideBar already exists for the dynamicPart
        if (repository.findByWebUrlMappingDynamicPart(dynamicPart).isPresent()) {
            throw new IllegalStateException("WebSlideBar already exists for dynamicPart: " + dynamicPart);
        }

        String branchCode = permissionService.fetchBranchCodeByRole(role, email);
        webSlideBar.setRole(role);
        webSlideBar.setCreatedByEmail(email);
        webSlideBar.setBranchCode(branchCode);

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

        webSlideBar.setWebUrlMapping(webUrlMapping);

        webSlideBar.setSlideImages(new ArrayList<>());
        webSlideBar.setImageUrlIds(new ArrayList<>());

        int imageId = 1;
        if (slideImages != null) {
            for (MultipartFile file : slideImages) {
                String url = s3Service.uploadImage(file);
                webSlideBar.getSlideImages().add(url);
                webSlideBar.getImageUrlIds().add(imageId++);
            }
        }

        return repository.save(webSlideBar);
    }

    @Override
    public WebSlideBar publicCreateWebSlideBar(String slideBarColor, List<MultipartFile> slideImages,
                                               String dynamicPart) throws IOException {
        // Check if WebSlideBar already exists for this dynamicPart
        if (repository.findByWebUrlMappingDynamicPart(dynamicPart).isPresent()) {
            throw new IllegalStateException("WebSlideBar already exists for dynamicPart: " + dynamicPart);
        }

        WebSlideBar webSlideBar = new WebSlideBar();
        webSlideBar.setSlideBarColor(slideBarColor);
        webSlideBar.setRole("PUBLIC_USER");
        webSlideBar.setCreatedByEmail("public@noreply.com");
        webSlideBar.setBranchCode("PUBLIC");

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

        webSlideBar.setWebUrlMapping(webUrlMapping);

        webSlideBar.setSlideImages(new ArrayList<>());
        webSlideBar.setImageUrlIds(new ArrayList<>());

        int imageId = 1;
        if (slideImages != null) {
            for (MultipartFile file : slideImages) {
                String url = s3Service.uploadImage(file);
                webSlideBar.getSlideImages().add(url);
                webSlideBar.getImageUrlIds().add(imageId++);
            }
        }

        return repository.save(webSlideBar);
    }

    @Override
    public List<WebSlideBar> getAllWebSlideBars(String role, String email, String branchCode) {
        if (!permissionService.hasPermission(role, email, "GET")) {
            throw new AccessDeniedException("No permission to view WebSlideBar list");
        }
        return repository.findAllByBranchCode(branchCode);
    }

    @Override
    public WebSlideBar updateWebSlideBar(Long id, WebSlideBar updatedWebSlideBar,
                                         List<MultipartFile> slideImages, String role, String email) throws IOException {
        if (!permissionService.hasPermission(role, email, "PUT")) {
            throw new AccessDeniedException("No permission to update WebSlideBar");
        }

        WebSlideBar existing = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("WebSlideBar not found"));

        existing.setSlideBarColor(updatedWebSlideBar.getSlideBarColor());

        if (slideImages != null && !slideImages.isEmpty()) {
            for (MultipartFile file : slideImages) {
                String url = s3Service.uploadImage(file);
                existing.getSlideImages().add(url);
                existing.getImageUrlIds().add(existing.getImageUrlIds().size() + 1);
            }
        }

        return repository.save(existing);
    }

    @Override
    public void deleteWebSlideBar(Long id, String role, String email) {
        if (!permissionService.hasPermission(role, email, "DELETE")) {
            throw new AccessDeniedException("No permission to delete WebSlideBar");
        }

        repository.findById(id)
                .orElseThrow(() -> new RuntimeException("WebSlideBar not found"));

        repository.deleteById(id);
    }

    @Override
    public WebSlideBar getWebSlideBarById(Long id, String role, String email) {
        if (!permissionService.hasPermission(role, email, "GET")) {
            throw new AccessDeniedException("No permission to view WebSlideBar");
        }

        return repository.findById(id)
                .orElseThrow(() -> new RuntimeException("WebSlideBar not found"));
    }

    @Override
    public Optional<WebSlideBar> getWebSlideBarByEmail(String email) {
        return repository.findByCreatedByEmail(email);
    }

    @Override
    public List<WebSlideBar> getWebSlideBarsByDynamicPart(String dynamicPart) {
        return repository.findByWebUrlMapping_DynamicPart(dynamicPart);
    }

    @Override
    public WebSlideBar getUserWebSlideBarByDynamicPart(String dynamicPart) {
        return repository.findByWebUrlMappingDynamicPart(dynamicPart)
                .orElseThrow(() -> new NoSuchElementException("No WebSlideBar found for dynamicPart: " + dynamicPart));
    }
}