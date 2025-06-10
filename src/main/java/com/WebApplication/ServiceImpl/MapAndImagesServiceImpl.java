package com.WebApplication.ServiceImpl;

import com.WebApplication.Entity.WebMapAndImages;
import com.WebApplication.Entity.WebUrlMapping;
import com.WebApplication.Repository.MapAndImagesRepository;
import com.WebApplication.Repository.WebUrlMappingRepository;
import com.WebApplication.Service.MapAndImagesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@Service
public class MapAndImagesServiceImpl implements MapAndImagesService {

    private final MapAndImagesRepository repository;
    private final S3Service s3Service;
    private final PermissionServiceImpl permissionService;
    private final WebUrlMappingRepository webUrlMappingRepository;

    @Autowired
    public MapAndImagesServiceImpl(MapAndImagesRepository repository,
                                   S3Service s3Service,
                                   PermissionServiceImpl permissionService,
                                   WebUrlMappingRepository webUrlMappingRepository) {
        this.repository = repository;
        this.s3Service = s3Service;
        this.permissionService = permissionService;
        this.webUrlMappingRepository = webUrlMappingRepository;
    }

    @Override
    public WebMapAndImages createMapAndImages(WebMapAndImages mapAndImages, MultipartFile contactImage,
                                              String role, String email, String dynamicPart) throws IOException {
        if (!permissionService.hasPermission(role, email, "POST")) {
            throw new AccessDeniedException("No permission to create map and images");
        }

        // Check if WebMapAndImages already exists for the dynamicPart
        if (repository.existsByWebUrlMappingDynamicPart(dynamicPart)) {
            throw new IllegalStateException("Map and images already exists for dynamicPart: " + dynamicPart);
        }

        String branchCode = permissionService.fetchBranchCodeByRole(role, email);
        mapAndImages.setRole(role);
        mapAndImages.setCreatedByEmail(email);
        mapAndImages.setBranchCode(branchCode);

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

        mapAndImages.setWebUrlMapping(webUrlMapping);

        if (contactImage != null && !contactImage.isEmpty()) {
            String imageUrl = s3Service.uploadImage(contactImage);
            mapAndImages.setContactImage(imageUrl);
        }

        return repository.save(mapAndImages);
    }

    @Override
    public WebMapAndImages publicCreateMapAndImages(String maps, MultipartFile contactImage, String dynamicPart) throws IOException {
        // Check if WebMapAndImages already exists for this dynamicPart
        if (repository.existsByWebUrlMappingDynamicPart(dynamicPart)) {
            throw new IllegalStateException("Map and images already exists for dynamicPart: " + dynamicPart);
        }

        WebMapAndImages mapAndImages = new WebMapAndImages();
        mapAndImages.setMaps(maps);
        mapAndImages.setRole("PUBLIC_USER");
        mapAndImages.setCreatedByEmail("public@noreply.com");
        mapAndImages.setBranchCode("PUBLIC");

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

        mapAndImages.setWebUrlMapping(webUrlMapping);

        if (contactImage != null && !contactImage.isEmpty()) {
            String imageUrl = s3Service.uploadImage(contactImage);
            mapAndImages.setContactImage(imageUrl);
        }

        return repository.save(mapAndImages);
    }

    @Override
    public WebMapAndImages updateMapAndImages(Long id, WebMapAndImages updatedMapAndImages,
                                              MultipartFile contactImage, String role, String email) throws IOException {
        if (!permissionService.hasPermission(role, email, "PUT")) {
            throw new AccessDeniedException("No permission to update map and images");
        }

        WebMapAndImages existing = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Map and images not found"));

        if (updatedMapAndImages.getMaps() != null) {
            existing.setMaps(updatedMapAndImages.getMaps());
        }

        if (contactImage != null && !contactImage.isEmpty()) {
            String imageUrl = s3Service.uploadImage(contactImage);
            existing.setContactImage(imageUrl);
        }

        return repository.save(existing);
    }

    @Override
    public void deleteMapAndImages(Long id, String role, String email) {
        if (!permissionService.hasPermission(role, email, "DELETE")) {
            throw new AccessDeniedException("No permission to delete map and images");
        }

        repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Map and images not found"));

        repository.deleteById(id);
    }

    @Override
    public List<WebMapAndImages> getAllMapAndImages(String role, String email, String branchCode) {
        if (!permissionService.hasPermission(role, email, "GET")) {
            throw new AccessDeniedException("No permission to view map and images list");
        }
        return repository.findAllByBranchCode(branchCode);
    }

    @Override
    public Optional<WebMapAndImages> getMapAndImagesByEmail(String email) {
        return repository.findByCreatedByEmail(email);
    }

    @Override
    public List<WebMapAndImages> getMapAndImagesByDynamicPart(String dynamicPart) {
        return repository.findByWebUrlMapping_DynamicPart(dynamicPart);
    }

    @Override
    public WebMapAndImages getUserMapAndImagesByDynamicPart(String dynamicPart) {
        return repository.findByWebUrlMappingDynamicPart(dynamicPart)
                .orElseThrow(() -> new NoSuchElementException("No Map and Images found for dynamicPart: " + dynamicPart));
    }
}