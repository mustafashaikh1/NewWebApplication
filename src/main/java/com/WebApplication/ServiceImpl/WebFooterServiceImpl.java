package com.WebApplication.ServiceImpl;

import com.WebApplication.Entity.WebFooter;
import com.WebApplication.Entity.WebUrlMapping;
import com.WebApplication.Repository.WebFooterRepository;
import com.WebApplication.Repository.WebUrlMappingRepository;
import com.WebApplication.Service.WebFooterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@Service
public class WebFooterServiceImpl implements WebFooterService {

    private final WebFooterRepository repository;
    private final PermissionServiceImpl permissionService;
    private final WebUrlMappingRepository webUrlMappingRepository;

    @Autowired
    public WebFooterServiceImpl(WebFooterRepository repository,
                                PermissionServiceImpl permissionService,
                                WebUrlMappingRepository webUrlMappingRepository) {
        this.repository = repository;
        this.permissionService = permissionService;
        this.webUrlMappingRepository = webUrlMappingRepository;
    }

    @Override
    public WebFooter createWebFooter(WebFooter footer, String role, String email, String dynamicPart) {
        if (!permissionService.hasPermission(role, email, "POST")) {
            throw new AccessDeniedException("No permission to create footer");
        }

        if (repository.existsByWebUrlMappingDynamicPart(dynamicPart)) {
            throw new IllegalStateException("Footer already exists for dynamicPart: " + dynamicPart);
        }

        String branchCode = permissionService.fetchBranchCodeByRole(role, email);
        footer.setRole(role);
        footer.setCreatedByEmail(email);
        footer.setBranchCode(branchCode);

        WebUrlMapping webUrlMapping = webUrlMappingRepository.findByDynamicPart(dynamicPart)
                .orElseGet(() -> {
                    WebUrlMapping newMapping = new WebUrlMapping();
                    newMapping.setDynamicPart(dynamicPart);
                    newMapping.setCreatedByEmail(email);
                    newMapping.setRole(role);
                    newMapping.setBranchCode(branchCode);
                    return webUrlMappingRepository.save(newMapping);
                });

        footer.setWebUrlMapping(webUrlMapping);
        return repository.save(footer);
    }

    @Override
    public WebFooter publicCreateWebFooter(WebFooter footer, String dynamicPart) {
        if (repository.existsByWebUrlMappingDynamicPart(dynamicPart)) {
            throw new IllegalStateException("Footer already exists for dynamicPart: " + dynamicPart);
        }

        footer.setRole("PUBLIC_USER");
        footer.setCreatedByEmail("public@noreply.com");
        footer.setBranchCode("PUBLIC");

        WebUrlMapping webUrlMapping = webUrlMappingRepository.findByDynamicPart(dynamicPart)
                .orElseGet(() -> {
                    WebUrlMapping newMapping = new WebUrlMapping();
                    newMapping.setDynamicPart(dynamicPart);
                    newMapping.setCreatedByEmail("public@noreply.com");
                    newMapping.setRole("PUBLIC_USER");
                    newMapping.setBranchCode("PUBLIC");
                    return webUrlMappingRepository.save(newMapping);
                });

        footer.setWebUrlMapping(webUrlMapping);
        return repository.save(footer);
    }

    @Override
    public List<WebFooter> getAllWebFooters(String role, String email, String branchCode) {
        if (!permissionService.hasPermission(role, email, "GET")) {
            throw new AccessDeniedException("No permission to view footer list");
        }
        return repository.findAllByBranchCode(branchCode);
    }

    @Override
    public WebFooter updateWebFooter(Long id, WebFooter updatedFooter, String role, String email) {
        if (!permissionService.hasPermission(role, email, "PUT")) {
            throw new AccessDeniedException("No permission to update footer");
        }

        WebFooter existing = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Footer not found"));

        // Update all fields
        existing.setTitle(updatedFooter.getTitle());
        existing.setFooterColor(updatedFooter.getFooterColor());
        existing.setEmail(updatedFooter.getEmail());
        existing.setMobileNumber(updatedFooter.getMobileNumber());
        existing.setAddress(updatedFooter.getAddress());
        existing.setInstagramLink(updatedFooter.getInstagramLink());
        existing.setFacebookLink(updatedFooter.getFacebookLink());
        existing.setTwitterLink(updatedFooter.getTwitterLink());
        existing.setYoutubeLink(updatedFooter.getYoutubeLink());
        existing.setWhatsappLink(updatedFooter.getWhatsappLink());

        return repository.save(existing);
    }

    @Override
    public void deleteWebFooter(Long id, String role, String email) {
        if (!permissionService.hasPermission(role, email, "DELETE")) {
            throw new AccessDeniedException("No permission to delete footer");
        }

        repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Footer not found"));

        repository.deleteById(id);
    }

    @Override
    public WebFooter getWebFooterById(Long id, String role, String email) {
        if (!permissionService.hasPermission(role, email, "GET")) {
            throw new AccessDeniedException("No permission to view footer");
        }

        return repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Footer not found"));
    }

    @Override
    public Optional<WebFooter> getWebFooterByEmail(String email) {
        return repository.findByCreatedByEmail(email);
    }

    @Override
    public List<WebFooter> getWebFootersByDynamicPart(String dynamicPart) {
        return repository.findByWebUrlMapping_DynamicPart(dynamicPart);
    }

    @Override
    public WebFooter getWebFooterByDynamicPart(String dynamicPart) {
        return repository.findByWebUrlMappingDynamicPart(dynamicPart)
                .orElseThrow(() -> new NoSuchElementException("No footer found for dynamicPart: " + dynamicPart));
    }


}