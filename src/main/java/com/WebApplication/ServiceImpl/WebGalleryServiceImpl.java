package com.WebApplication.ServiceImpl;

import com.WebApplication.Entity.WebGallery;
import com.WebApplication.Entity.WebUrlMapping;
import com.WebApplication.Repository.WebGalleryRepository;
import com.WebApplication.Repository.WebUrlMappingRepository;
import com.WebApplication.Service.WebGalleryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.NoSuchElementException;

@Service
public class WebGalleryServiceImpl implements WebGalleryService {

    private final WebGalleryRepository repository;
    private final PermissionServiceImpl permissionService;
    private final S3Service s3Service;
    private final WebUrlMappingRepository webUrlMappingRepository;

    @Autowired
    public WebGalleryServiceImpl(WebGalleryRepository repository,
                                 PermissionServiceImpl permissionService,
                                 S3Service s3Service,
                                 WebUrlMappingRepository webUrlMappingRepository) {
        this.repository = repository;
        this.permissionService = permissionService;
        this.s3Service = s3Service;
        this.webUrlMappingRepository = webUrlMappingRepository;
    }

    @Override
    public WebGallery createWebGallery(WebGallery webGallery, List<MultipartFile> galleryImages,
                                       String role, String email, String dynamicPart) throws IOException {
        if (!permissionService.hasPermission(role, email, "POST")) {
            throw new AccessDeniedException("No permission to create WebGallery");
        }

        String branchCode = permissionService.fetchBranchCodeByRole(role, email);
        webGallery.setRole(role);
        webGallery.setCreatedByEmail(email);
        webGallery.setBranchCode(branchCode);

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

        webGallery.setWebUrlMapping(webUrlMapping);

        // Initialize collections if null
        if (webGallery.getGalleryImages() == null) {
            webGallery.setGalleryImages(new ArrayList<>());
        }
        if (webGallery.getImageUrlIds() == null) {
            webGallery.setImageUrlIds(new ArrayList<>());
        }

        // Upload and add new images
        if (galleryImages != null && !galleryImages.isEmpty()) {
            int imageUrlIdCounter = 1;
            for (MultipartFile image : galleryImages) {
                String imageUrl = s3Service.uploadImage(image);
                webGallery.getGalleryImages().add(imageUrl);
                webGallery.getImageUrlIds().add(imageUrlIdCounter++);
            }
        }

        return repository.save(webGallery);
    }

    @Override
    public WebGallery publicCreateWebGallery(String eventName, Integer year, String galleryColor,
                                             List<MultipartFile> galleryImages, String dynamicPart) throws IOException {
        WebGallery webGallery = new WebGallery();
        webGallery.setEventName(eventName);
        webGallery.setYear(year);
        webGallery.setGalleryColor(galleryColor);
        webGallery.setRole("PUBLIC_USER");
        webGallery.setCreatedByEmail("public@noreply.com");
        webGallery.setBranchCode("PUBLIC");

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

        webGallery.setWebUrlMapping(webUrlMapping);

        // Initialize collections
        webGallery.setGalleryImages(new ArrayList<>());
        webGallery.setImageUrlIds(new ArrayList<>());

        // Upload and add new images
        if (galleryImages != null && !galleryImages.isEmpty()) {
            int imageUrlIdCounter = 1;
            for (MultipartFile image : galleryImages) {
                String imageUrl = s3Service.uploadImage(image);
                webGallery.getGalleryImages().add(imageUrl);
                webGallery.getImageUrlIds().add(imageUrlIdCounter++);
            }
        }

        return repository.save(webGallery);
    }

    @Override
    public WebGallery updateWebGallery(Long id, WebGallery updatedWebGallery,
                                       List<MultipartFile> galleryImages,
                                       String role, String email) throws IOException {
        if (!permissionService.hasPermission(role, email, "PUT")) {
            throw new AccessDeniedException("No permission to update WebGallery");
        }

        WebGallery existing = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("WebGallery not found"));

        // Update fields
        existing.setEventName(updatedWebGallery.getEventName());
        existing.setYear(updatedWebGallery.getYear());
        existing.setGalleryColor(updatedWebGallery.getGalleryColor());

        // ✅ Update dynamicPart if provided
        if (updatedWebGallery.getWebUrlMapping() != null &&
                updatedWebGallery.getWebUrlMapping().getDynamicPart() != null) {
            existing.getWebUrlMapping().setDynamicPart(
                    updatedWebGallery.getWebUrlMapping().getDynamicPart()
            );
        }

        // ✅ Add new images and generate unique IDs
        if (galleryImages != null && !galleryImages.isEmpty()) {
            List<String> existingImages = existing.getGalleryImages();
            List<Integer> existingIds = existing.getImageUrlIds();

            int imageIdCounter = existingIds.isEmpty() ? 1 : Collections.max(existingIds) + 1;

            for (MultipartFile image : galleryImages) {
                String newImageUrl = s3Service.uploadImage(image);
                existingImages.add(newImageUrl);
                existingIds.add(imageIdCounter++);
            }

            existing.setGalleryImages(existingImages);
            existing.setImageUrlIds(existingIds);
        }

        return repository.save(existing);
    }


    @Override
    public void deleteWebGallery(Long id, String role, String email) {
        if (!permissionService.hasPermission(role, email, "DELETE")) {
            throw new AccessDeniedException("No permission to delete WebGallery");
        }

        repository.findById(id)
                .orElseThrow(() -> new RuntimeException("WebGallery not found"));
        repository.deleteById(id);
    }

    @Override
    public List<WebGallery> getAllWebGalleries(String role, String email, String branchCode) {
        if (!permissionService.hasPermission(role, email, "GET")) {
            throw new AccessDeniedException("No permission to view WebGallery list");
        }
        return repository.findByBranchCode(branchCode);
    }

    @Override
    public WebGallery getWebGalleryById(Long id, String role, String email) {
        if (!permissionService.hasPermission(role, email, "GET")) {
            throw new AccessDeniedException("No permission to view WebGallery");
        }
        return repository.findById(id)
                .orElseThrow(() -> new RuntimeException("WebGallery not found"));
    }

    @Override
    public List<WebGallery> getWebGalleriesByDynamicPart(String dynamicPart) {
        return repository.findByWebUrlMapping_DynamicPart(dynamicPart);
    }

    @Override
    public WebGallery getUserWebGalleryByDynamicPart(String dynamicPart) {
        List<WebGallery> results = repository.findByWebUrlMappingDynamicPart(dynamicPart);
        if (results.isEmpty()) {
            throw new NoSuchElementException("No WebGallery found for dynamicPart: " + dynamicPart);
        }
        return results.get(0); // or implement any logic to pick the correct one
    }

}