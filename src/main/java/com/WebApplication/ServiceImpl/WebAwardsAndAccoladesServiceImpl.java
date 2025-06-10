package com.WebApplication.ServiceImpl;

import com.WebApplication.Entity.WebAwardsAndAccolades;
import com.WebApplication.Entity.WebUrlMapping;
import com.WebApplication.Repository.WebAwardsAndAccoladesRepository;
import com.WebApplication.Repository.WebUrlMappingRepository;
import com.WebApplication.Service.WebAwardsAndAccoladesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.NoSuchElementException;

@Service
public class WebAwardsAndAccoladesServiceImpl implements WebAwardsAndAccoladesService {

    private final WebAwardsAndAccoladesRepository repository;
    private final WebUrlMappingRepository webUrlMappingRepository;
    private final PermissionServiceImpl permissionService;
    private final S3Service s3Service;

    @Autowired
    public WebAwardsAndAccoladesServiceImpl(WebAwardsAndAccoladesRepository repository,
                                            WebUrlMappingRepository webUrlMappingRepository,
                                            PermissionServiceImpl permissionService,
                                            S3Service s3Service) {
        this.repository = repository;
        this.webUrlMappingRepository = webUrlMappingRepository;
        this.permissionService = permissionService;
        this.s3Service = s3Service;
    }

    @Override
    public WebAwardsAndAccolades createAward(WebAwardsAndAccolades award, MultipartFile awardImage,
                                             String role, String email, String dynamicPart) throws IOException {
        if (!permissionService.hasPermission(role, email, "POST")) {
            throw new AccessDeniedException("No permission to create award");
        }

        try {
            String branchCode = permissionService.fetchBranchCodeByRole(role, email);
            award.setRole(role);
            award.setCreatedByEmail(email);
            award.setBranchCode(branchCode);

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

            award.setWebUrlMapping(webUrlMapping);

            if (awardImage != null && !awardImage.isEmpty()) {
                String imageUrl = s3Service.uploadImage(awardImage);
                award.setAwardImage(imageUrl);
            }

            return repository.save(award);
        } catch (IOException e) {
            throw new RuntimeException("Failed to upload image", e);
        }
    }

    @Override
    public WebAwardsAndAccolades publicCreateAward(String awardName, String description, String awardedBy,
                                                   String awardTo, int year, MultipartFile awardImage,
                                                   String dynamicPart) throws IOException {
        try {
            WebAwardsAndAccolades award = new WebAwardsAndAccolades();
            award.setAwardName(awardName);
            award.setDescription(description);
            award.setAwardedBy(awardedBy);
            award.setAwardTo(awardTo);
            award.setYear(year);
            award.setRole("PUBLIC_USER");
            award.setCreatedByEmail("public@noreply.com");
            award.setBranchCode("PUBLIC");

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

            award.setWebUrlMapping(webUrlMapping);

            if (awardImage != null && !awardImage.isEmpty()) {
                String imageUrl = s3Service.uploadImage(awardImage);
                award.setAwardImage(imageUrl);
            }

            return repository.save(award);
        } catch (IOException e) {
            throw new RuntimeException("Failed to upload image", e);
        }
    }

    @Override
    public WebAwardsAndAccolades updateAward(Long id, WebAwardsAndAccolades updatedAward,
                                             MultipartFile awardImage, String role, String email) throws IOException {
        if (!permissionService.hasPermission(role, email, "PUT")) {
            throw new AccessDeniedException("No permission to update award");
        }

        WebAwardsAndAccolades existing = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Award not found"));

        // Update fields
        existing.setAwardName(updatedAward.getAwardName());
        existing.setDescription(updatedAward.getDescription());
        existing.setAwardedBy(updatedAward.getAwardedBy());
        existing.setYear(updatedAward.getYear());
        existing.setAwardTo(updatedAward.getAwardTo());

        if (awardImage != null && !awardImage.isEmpty()) {
            String imageUrl = s3Service.uploadImage(awardImage);
            existing.setAwardImage(imageUrl);
        }

        return repository.save(existing);
    }

    @Override
    public void deleteAward(Long id, String role, String email) {
        if (!permissionService.hasPermission(role, email, "DELETE")) {
            throw new AccessDeniedException("No permission to delete award");
        }

        repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Award not found"));

        repository.deleteById(id);
    }

    @Override
    public WebAwardsAndAccolades getAwardById(Long id, String role, String email) {
        if (!permissionService.hasPermission(role, email, "GET")) {
            throw new AccessDeniedException("No permission to view award");
        }

        return repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Award not found"));
    }

    @Override
    public List<WebAwardsAndAccolades> getAllAwards(String role, String email, String branchCode) {
        if (!permissionService.hasPermission(role, email, "GET")) {
            throw new AccessDeniedException("No permission to view award list");
        }
        return repository.findAllByBranchCode(branchCode);
    }

    @Override
    public List<WebAwardsAndAccolades> getAwardsByEmail(String email) {
        return repository.findByCreatedByEmail(email);
    }

    @Override
    public List<WebAwardsAndAccolades> getAwardsByDynamicPart(String dynamicPart) {
        return repository.findByWebUrlMapping_DynamicPart(dynamicPart);
    }

    @Override
    public WebAwardsAndAccolades getUserAwardByDynamicPart(String dynamicPart) {
        return repository.findFirstByWebUrlMapping_DynamicPart(dynamicPart)
                .orElseThrow(() -> new NoSuchElementException("No award found for dynamicPart: " + dynamicPart));
    }
}