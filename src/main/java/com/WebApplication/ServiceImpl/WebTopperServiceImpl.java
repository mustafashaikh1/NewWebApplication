package com.WebApplication.ServiceImpl;

import com.WebApplication.Entity.WebTopper;
import com.WebApplication.Entity.WebUrlMapping;
import com.WebApplication.Repository.WebTopperRepository;
import com.WebApplication.Repository.WebUrlMappingRepository;
import com.WebApplication.Service.WebTopperService;
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
public class WebTopperServiceImpl implements WebTopperService {

    private final WebTopperRepository repository;
    private final PermissionServiceImpl permissionService;
    private final S3Service s3Service;
    private final WebUrlMappingRepository webUrlMappingRepository;

    @Autowired
    public WebTopperServiceImpl(WebTopperRepository repository,
                                PermissionServiceImpl permissionService,
                                S3Service s3Service,
                                WebUrlMappingRepository webUrlMappingRepository) {
        this.repository = repository;
        this.permissionService = permissionService;
        this.s3Service = s3Service;
        this.webUrlMappingRepository = webUrlMappingRepository;
    }

    @Override
    public WebTopper createWebTopper(WebTopper topper, List<MultipartFile> images, String role, String email, String dynamicPart) throws IOException {
        if (!permissionService.hasPermission(role, email, "POST")) {
            throw new AccessDeniedException("No permission to create topper");
        }

        try {
            String branchCode = permissionService.fetchBranchCodeByRole(role, email);
            topper.setRole(role);
            topper.setCreatedByEmail(email);
            topper.setBranchCode(branchCode);

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

            topper.setWebUrlMapping(webUrlMapping);

            topper.setTopperImages(new ArrayList<>());
            topper.setImageUrlIds(new ArrayList<>());

            int imageId = 1;
            if (images != null) {
                for (MultipartFile file : images) {
                    if (!file.isEmpty()) {
                        String imageUrl = s3Service.uploadImage(file);
                        topper.getTopperImages().add(imageUrl);
                        topper.getImageUrlIds().add(imageId++);
                    }
                }
            }

            return repository.save(topper);
        } catch (IOException e) {
            throw new RuntimeException("Failed to upload image", e);
        }
    }

    @Override
    public WebTopper publicCreateWebTopper(String name, Double totalMarks, String post,
                                           Integer rank, Integer year, String topperColor,
                                           List<MultipartFile> topperImages,
                                           String dynamicPart) throws IOException {
        try {
            WebTopper topper = new WebTopper();
            topper.setName(name);
            topper.setTotalMarks(totalMarks);
            topper.setPost(post);
            topper.setRank(rank);
            topper.setYear(year);
            topper.setTopperColor(topperColor);
            topper.setRole("PUBLIC_USER");
            topper.setCreatedByEmail("public@noreply.com");
            topper.setBranchCode("PUBLIC");

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

            topper.setWebUrlMapping(webUrlMapping);

            topper.setTopperImages(new ArrayList<>());
            topper.setImageUrlIds(new ArrayList<>());

            int imageId = 1;
            if (topperImages != null) {
                for (MultipartFile file : topperImages) {
                    if (!file.isEmpty()) {
                        String imageUrl = s3Service.uploadImage(file);
                        topper.getTopperImages().add(imageUrl);
                        topper.getImageUrlIds().add(imageId++);
                    }
                }
            }

            return repository.save(topper);
        } catch (IOException e) {
            throw new RuntimeException("Failed to upload image", e);
        }
    }

    @Override
    public WebTopper updateWebTopper(Long id, WebTopper updated, List<MultipartFile> images, String role, String email) throws IOException {
        if (!permissionService.hasPermission(role, email, "PUT")) {
            throw new AccessDeniedException("No permission to update topper");
        }

        WebTopper existing = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Topper not found"));

        // Update fields
        existing.setName(updated.getName());
        existing.setTotalMarks(updated.getTotalMarks());
        existing.setPost(updated.getPost());
        existing.setRank(updated.getRank());
        existing.setYear(updated.getYear());
        existing.setTopperColor(updated.getTopperColor());

        if (images != null && !images.isEmpty()) {
            for (MultipartFile file : images) {
                if (!file.isEmpty()) {
                    String imageUrl = s3Service.uploadImage(file);
                    existing.getTopperImages().add(imageUrl);
                    existing.getImageUrlIds().add(existing.getImageUrlIds().size() + 1);
                }
            }
        }

        return repository.save(existing);
    }

    @Override
    public void deleteWebTopperById(Long id, String role, String email) {
        if (!permissionService.hasPermission(role, email, "DELETE")) {
            throw new AccessDeniedException("No permission to delete topper");
        }

        repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Topper not found"));

        repository.deleteById(id);
    }

    @Override
    public List<WebTopper> getAllWebToppers(String role, String email, String branchCode) {
        if (!permissionService.hasPermission(role, email, "GET")) {
            throw new AccessDeniedException("No permission to view toppers");
        }

        return repository.findAllByBranchCode(branchCode);
    }

    @Override
    public WebTopper getWebTopperById(Long id, String role, String email) {
        if (!permissionService.hasPermission(role, email, "GET")) {
            throw new AccessDeniedException("No permission to view topper");
        }

        return repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Topper not found"));
    }

    @Override
    public Optional<WebTopper> getWebTopperByEmail(String email) {
        return repository.findByCreatedByEmail(email);
    }

    @Override
    public List<WebTopper> getToppersByDynamicPart(String dynamicPart) {
        return repository.findByWebUrlMapping_DynamicPart(dynamicPart);
    }

    @Override
    public WebTopper getUserTopperByDynamicPart(String dynamicPart) throws NoSuchElementException {
        return repository.findFirstByWebUrlMapping_DynamicPart(dynamicPart)
                .orElseThrow(() -> new NoSuchElementException("No topper found for dynamicPart: " + dynamicPart));
    }
}