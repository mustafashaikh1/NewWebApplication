package com.WebApplication.ServiceImpl;

import com.WebApplication.Entity.WebUrlMapping;
import com.WebApplication.Entity.WebVisionMission;
import com.WebApplication.Repository.WebUrlMappingRepository;
import com.WebApplication.Repository.WebVisionMissionRepository;
import com.WebApplication.Service.WebVisionMissionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@Service
public class WebVisionMissionServiceImpl implements WebVisionMissionService {

    private final WebVisionMissionRepository repository;
    private final PermissionServiceImpl permissionService;
    private final S3Service s3Service;
    private final WebUrlMappingRepository webUrlMappingRepository;

    @Autowired
    public WebVisionMissionServiceImpl(WebVisionMissionRepository repository,
                                       PermissionServiceImpl permissionService,
                                       S3Service s3Service,
                                       WebUrlMappingRepository webUrlMappingRepository) {
        this.repository = repository;
        this.permissionService = permissionService;
        this.s3Service = s3Service;
        this.webUrlMappingRepository = webUrlMappingRepository;
    }

    @Override
    public WebVisionMission createWebVisionMission(WebVisionMission vm, MultipartFile directorImage,
                                                   String role, String email, String dynamicPart) throws IOException {
        if (!permissionService.hasPermission(role, email, "POST")) {
            throw new AccessDeniedException("No permission to create vision mission");
        }

        // Check if WebVisionMission already exists for the dynamicPart
        if (repository.findByWebUrlMappingDynamicPart(dynamicPart).isPresent()) {
            throw new IllegalStateException("WebVisionMission already exists for dynamicPart: " + dynamicPart);
        }

        String branchCode = permissionService.fetchBranchCodeByRole(role, email);
        vm.setRole(role);
        vm.setCreatedByEmail(email);
        vm.setBranchCode(branchCode);

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

        vm.setWebUrlMapping(webUrlMapping);

        if (directorImage != null && !directorImage.isEmpty()) {
            String imageUrl = s3Service.uploadImage(directorImage);
            vm.setDirectorImage(imageUrl);
        }

        return repository.save(vm);
    }

    @Override
    public WebVisionMission publicCreateWebVisionMission(String vision, String mission, String visionmissionColor,
                                                         String directorMessage, String directorName, String description,
                                                         MultipartFile directorImage, String dynamicPart) throws IOException {
        // Check if WebVisionMission already exists for this dynamicPart
        if (repository.findByWebUrlMappingDynamicPart(dynamicPart).isPresent()) {
            throw new IllegalStateException("WebVisionMission already exists for dynamicPart: " + dynamicPart);
        }

        WebVisionMission vm = new WebVisionMission();
        vm.setVision(vision);
        vm.setMission(mission);
        vm.setVisionmissionColor(visionmissionColor);
        vm.setDirectorMessage(directorMessage);
        vm.setDirectorName(directorName);
        vm.setDescription(description);
        vm.setRole("PUBLIC_USER");
        vm.setCreatedByEmail("public@noreply.com");
        vm.setBranchCode("PUBLIC");

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

        vm.setWebUrlMapping(webUrlMapping);

        if (directorImage != null && !directorImage.isEmpty()) {
            String imageUrl = s3Service.uploadImage(directorImage);
            vm.setDirectorImage(imageUrl);
        }

        return repository.save(vm);
    }

    @Override
    public WebVisionMission updateWebVisionMission(Long id, WebVisionMission updated,
                                                   MultipartFile directorImage, String role, String email) throws IOException {
        if (!permissionService.hasPermission(role, email, "PUT")) {
            throw new AccessDeniedException("No permission to update vision mission");
        }

        WebVisionMission existing = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("VisionMission not found"));

        if (updated.getVision() != null) existing.setVision(updated.getVision());
        if (updated.getMission() != null) existing.setMission(updated.getMission());
        if (updated.getVisionmissionColor() != null) existing.setVisionmissionColor(updated.getVisionmissionColor());
        if (updated.getDirectorMessage() != null) existing.setDirectorMessage(updated.getDirectorMessage());
        if (updated.getDirectorName() != null) existing.setDirectorName(updated.getDirectorName());
        if (updated.getDescription() != null) existing.setDescription(updated.getDescription());

        if (directorImage != null && !directorImage.isEmpty()) {
            String imageUrl = s3Service.uploadImage(directorImage);
            existing.setDirectorImage(imageUrl);
        }

        return repository.save(existing);
    }

    @Override
    public void deleteWebVisionMission(Long id, String role, String email) {
        if (!permissionService.hasPermission(role, email, "DELETE")) {
            throw new AccessDeniedException("No permission to delete vision mission");
        }

        repository.findById(id).orElseThrow(() -> new RuntimeException("VisionMission not found"));
        repository.deleteById(id);
    }

    @Override
    public List<WebVisionMission> getAllWebVisionMissions(String role, String email, String branchCode) {
        if (!permissionService.hasPermission(role, email, "GET")) {
            throw new AccessDeniedException("No permission to view vision missions");
        }
        return repository.findAllByBranchCode(branchCode);
    }

    @Override
    public WebVisionMission getWebVisionMissionById(Long id, String role, String email) {
        if (!permissionService.hasPermission(role, email, "GET")) {
            throw new AccessDeniedException("No permission to view vision mission");
        }
        return repository.findById(id).orElseThrow(() -> new RuntimeException("VisionMission not found"));
    }

    @Override
    public Optional<WebVisionMission> getWebVisionMissionByEmail(String email) {
        return repository.findByCreatedByEmail(email);
    }

    @Override
    public List<WebVisionMission> getWebVisionMissionsByDynamicPart(String dynamicPart) {
        return repository.findByWebUrlMapping_DynamicPart(dynamicPart);
    }

    @Override
    public WebVisionMission getUserWebVisionMissionByDynamicPart(String dynamicPart) {
        return repository.findByWebUrlMappingDynamicPart(dynamicPart)
                .orElseThrow(() -> new NoSuchElementException("No WebVisionMission found for dynamicPart: " + dynamicPart));
    }
}