package com.WebApplication.ServiceImpl;

import com.WebApplication.Entity.WebCourse;
import com.WebApplication.Entity.WebUrlMapping;
import com.WebApplication.Repository.WebCourseRepository;
import com.WebApplication.Repository.WebUrlMappingRepository;
import com.WebApplication.Service.WebCourseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@Service
public class WebCourseServiceImpl implements WebCourseService {

    private final WebCourseRepository repository;
    private final S3Service s3Service;
    private final PermissionServiceImpl permissionService;
    private final WebUrlMappingRepository webUrlMappingRepository;

    @Autowired
    public WebCourseServiceImpl(WebCourseRepository repository,
                                S3Service s3Service,
                                PermissionServiceImpl permissionService,
                                WebUrlMappingRepository webUrlMappingRepository) {
        this.repository = repository;
        this.s3Service = s3Service;
        this.permissionService = permissionService;
        this.webUrlMappingRepository = webUrlMappingRepository;
    }

    @Override
    public WebCourse createWebCourse(WebCourse webCourse, MultipartFile courseImage,
                                     String role, String email, String dynamicPart) throws IOException {
        if (!permissionService.hasPermission(role, email, "POST")) {
            throw new AccessDeniedException("No permission to create WebCourse");
        }

        try {
            String branchCode = permissionService.fetchBranchCodeByRole(role, email);
            webCourse.setRole(role);
            webCourse.setCreatedByEmail(email);
            webCourse.setBranchCode(branchCode);

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

            webCourse.setWebUrlMapping(webUrlMapping);

            // Set course color if exists for the branch
            List<String> existingCourseColors = repository.findWebCourseColorByBranchCode(branchCode);
            if (!existingCourseColors.isEmpty()) {
                webCourse.setCourseColor(existingCourseColors.get(0));
            }

            if (courseImage != null && !courseImage.isEmpty()) {
                String imageUrl = s3Service.uploadImage(courseImage);
                webCourse.setCourseImage(imageUrl);
            }

            return repository.save(webCourse);
        } catch (IOException e) {
            throw new RuntimeException("Failed to upload image", e);
        }
    }

    @Override
    public WebCourse publicCreateWebCourse(String courseTitle, String link, String description,String courseColor,
                                           MultipartFile courseImage, String dynamicPart) {
        try {
            WebCourse webCourse = new WebCourse();
            webCourse.setCourseTitle(courseTitle);
            webCourse.setLink(link);
            webCourse.setDescription(description);
            webCourse.setCourseColor(courseColor);
            webCourse.setRole("PUBLIC_USER");
            webCourse.setCreatedByEmail("public@noreply.com");
            webCourse.setBranchCode("PUBLIC");

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

            webCourse.setWebUrlMapping(webUrlMapping);

            if (courseImage != null && !courseImage.isEmpty()) {
                String imageUrl = s3Service.uploadImage(courseImage);
                webCourse.setCourseImage(imageUrl);
            }

            return repository.save(webCourse);
        } catch (IOException e) {
            throw new RuntimeException("Failed to upload image", e);
        }
    }

    @Override
    public List<WebCourse> getAllWebCourses(String role, String email, String branchCode) {
        if (!permissionService.hasPermission(role, email, "GET")) {
            throw new AccessDeniedException("No permission to view WebCourse list");
        }
        return repository.findByBranchCode(branchCode);
    }

    @Override
    public WebCourse updateWebCourse(Long id, WebCourse updatedWebCourse, MultipartFile courseImage,
                                     String role, String email) throws IOException {
        if (!permissionService.hasPermission(role, email, "PUT")) {
            throw new AccessDeniedException("No permission to update WebCourse");
        }

        WebCourse existing = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("WebCourse not found"));

        existing.setCourseTitle(updatedWebCourse.getCourseTitle());
        existing.setLink(updatedWebCourse.getLink());
        existing.setDescription(updatedWebCourse.getDescription());
        existing.setCourseColor(updatedWebCourse.getCourseColor());

        if (courseImage != null && !courseImage.isEmpty()) {
            String imageUrl = s3Service.uploadImage(courseImage);
            existing.setCourseImage(imageUrl);
        }

        return repository.save(existing);
    }

    @Override
    public void deleteWebCourse(Long id, String role, String email) {
        if (!permissionService.hasPermission(role, email, "DELETE")) {
            throw new AccessDeniedException("No permission to delete WebCourse");
        }

        WebCourse webCourse = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("WebCourse not found"));

        if (webCourse.getCourseImage() != null) {
            s3Service.deleteImage(webCourse.getCourseImage());
        }

        repository.deleteById(id);
    }

    @Override
    public WebCourse getWebCourseById(Long id, String role, String email) {
        if (!permissionService.hasPermission(role, email, "GET")) {
            throw new AccessDeniedException("No permission to view WebCourse");
        }

        return repository.findById(id)
                .orElseThrow(() -> new RuntimeException("WebCourse not found"));
    }

    @Override
    public void addWebCourseColorByBranchCode(String branchCode, String courseColor,
                                              String role, String email) {
        if (!permissionService.hasPermission(role, email, "POST")) {
            throw new AccessDeniedException("No permission to add WebCourse color");
        }
        repository.addWebCourseColorByBranchCode(branchCode, courseColor);
    }

    @Override
    public void updateWebCourseColorByBranchCode(String branchCode, String courseColor,
                                                 String role, String email) {
        if (!permissionService.hasPermission(role, email, "PUT")) {
            throw new AccessDeniedException("No permission to update WebCourse color");
        }
        repository.updateWebCourseColorByBranchCode(branchCode, courseColor);
    }

    @Override
    public boolean getWebCourseColorStatusByBranchCode(String branchCode, String courseColor) {
        List<WebCourse> webCourses = repository.findByBranchCodeAndCourseColor(branchCode, courseColor);
        return !webCourses.isEmpty();
    }

    @Override
    public void deleteWebCourseColorByBranchCode(String branchCode, String role, String email) {
        if (!permissionService.hasPermission(role, email, "DELETE")) {
            throw new AccessDeniedException("No permission to delete WebCourse color");
        }
        repository.deleteWebCourseColorByBranchCode(branchCode);
    }

    @Override
    public Optional<WebCourse> getWebCourseByEmail(String email) {
        return repository.findByCreatedByEmail(email);
    }

    @Override
    public List<WebCourse> getWebCoursesByDynamicPart(String dynamicPart) {
        return repository.findByWebUrlMapping_DynamicPart(dynamicPart);
    }

    @Override
    public WebCourse getUserWebCourseByDynamicPart(String dynamicPart) {
        return repository.findByWebUrlMappingDynamicPart(dynamicPart)
                .orElseThrow(() -> new NoSuchElementException("No WebCourse found for dynamicPart: " + dynamicPart));
    }
}