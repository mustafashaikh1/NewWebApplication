package com.WebApplication.ServiceImpl;

import com.WebApplication.Entity.WebTestimonials;
import com.WebApplication.Entity.WebUrlMapping;
import com.WebApplication.Repository.WebTestimonialsRepository;
import com.WebApplication.Repository.WebUrlMappingRepository;
import com.WebApplication.Service.WebTestimonialsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@Service
public class WebTestimonialsServiceImpl implements WebTestimonialsService {

    private final WebTestimonialsRepository repository;
    private final PermissionServiceImpl permissionService;
    private final S3Service s3Service;
    private final WebUrlMappingRepository webUrlMappingRepository;

    @Autowired
    public WebTestimonialsServiceImpl(WebTestimonialsRepository repository,
                                      PermissionServiceImpl permissionService,
                                      S3Service s3Service,
                                      WebUrlMappingRepository webUrlMappingRepository) {
        this.repository = repository;
        this.permissionService = permissionService;
        this.s3Service = s3Service;
        this.webUrlMappingRepository = webUrlMappingRepository;
    }

    @Override
    public WebTestimonials createWebTestimonial(WebTestimonials testimonial, MultipartFile image,
                                                String role, String email, String dynamicPart) throws IOException {
        if (!permissionService.hasPermission(role, email, "POST")) {
            throw new AccessDeniedException("No permission to create testimonial");
        }

        try {
            String branchCode = permissionService.fetchBranchCodeByRole(role, email);
            testimonial.setRole(role);
            testimonial.setCreatedByEmail(email);
            testimonial.setBranchCode(branchCode);

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

            testimonial.setWebUrlMapping(webUrlMapping);

            if (image != null && !image.isEmpty()) {
                String imageUrl = s3Service.uploadImage(image);
                testimonial.setTestimonialImage(imageUrl);
            }

            return repository.save(testimonial);
        } catch (IOException e) {
            throw new RuntimeException("Failed to upload image", e);
        }
    }

    @Override
    public WebTestimonials publicCreateWebTestimonial(String testimonialTitle, String testimonialName,
                                                      String exam, String post, String description,
                                                      MultipartFile testimonialImage, String dynamicPart,
                                                      String testimonialColor) throws IOException {
        try {
            WebTestimonials testimonial = new WebTestimonials();
            testimonial.setTestimonialTitle(testimonialTitle);
            testimonial.setTestimonialName(testimonialName);
            testimonial.setExam(exam);
            testimonial.setPost(post);
            testimonial.setDescription(description);
            testimonial.setTestimonialColor(testimonialColor);
            testimonial.setRole("PUBLIC_USER");
            testimonial.setCreatedByEmail("public@noreply.com");
            testimonial.setBranchCode("PUBLIC");

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

            testimonial.setWebUrlMapping(webUrlMapping);

            if (testimonialImage != null && !testimonialImage.isEmpty()) {
                String imageUrl = s3Service.uploadImage(testimonialImage);
                testimonial.setTestimonialImage(imageUrl);
            }

            return repository.save(testimonial);
        } catch (IOException e) {
            throw new RuntimeException("Failed to upload image", e);
        }
    }

    @Override
    public WebTestimonials updateWebTestimonial(Long id, WebTestimonials updated,
                                                MultipartFile image, String role, String email) throws IOException {
        if (!permissionService.hasPermission(role, email, "PUT")) {
            throw new AccessDeniedException("No permission to update testimonial");
        }

        WebTestimonials existing = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Testimonial not found"));

        // Update fields
        existing.setTestimonialTitle(updated.getTestimonialTitle());
        existing.setTestimonialName(updated.getTestimonialName());
        existing.setExam(updated.getExam());
        existing.setPost(updated.getPost());
        existing.setDescription(updated.getDescription());
        existing.setTestimonialColor(updated.getTestimonialColor());

        if (image != null && !image.isEmpty()) {
            String imageUrl = s3Service.uploadImage(image);
            existing.setTestimonialImage(imageUrl);
        }

        return repository.save(existing);
    }

    @Override
    public void deleteWebTestimonialById(Long id, String role, String email) {
        if (!permissionService.hasPermission(role, email, "DELETE")) {
            throw new AccessDeniedException("No permission to delete testimonial");
        }

        repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Testimonial not found"));

        repository.deleteById(id);
    }

    @Override
    public List<WebTestimonials> getAllWebTestimonials(String role, String email, String branchCode) {
        if (!permissionService.hasPermission(role, email, "GET")) {
            throw new AccessDeniedException("No permission to view testimonials");
        }

        return repository.findAllByBranchCode(branchCode);
    }

    @Override
    public WebTestimonials getWebTestimonialById(Long id, String role, String email) {
        if (!permissionService.hasPermission(role, email, "GET")) {
            throw new AccessDeniedException("No permission to view testimonial");
        }

        return repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Testimonial not found"));
    }

    @Override
    public Optional<WebTestimonials> getWebTestimonialByEmail(String email) {
        return repository.findByCreatedByEmail(email);
    }

    @Override
    public List<WebTestimonials> getTestimonialsByDynamicPart(String dynamicPart) {
        return repository.findByWebUrlMapping_DynamicPart(dynamicPart);
    }

    @Override
    public WebTestimonials getUserTestimonialByDynamicPart(String dynamicPart) throws NoSuchElementException {
        return repository.findFirstByWebUrlMapping_DynamicPart(dynamicPart)
                .orElseThrow(() -> new NoSuchElementException("No testimonial found for dynamicPart: " + dynamicPart));
    }

    @Override
    public void updateTestimonialColorByBranchCode(String branchCode, String testimonialColor, String role, String email) {
        if (!permissionService.hasPermission(role, email, "PUT")) {
            throw new AccessDeniedException("No permission to update testimonial color");
        }
        repository.updateTestimonialColorByBranchCode(branchCode, testimonialColor);
    }

    @Override
    public void deleteTestimonialColorByBranchCode(String branchCode, String role, String email) {
        if (!permissionService.hasPermission(role, email, "DELETE")) {
            throw new AccessDeniedException("No permission to delete testimonial color");
        }
        repository.deleteTestimonialColorByBranchCode(branchCode);
    }
}