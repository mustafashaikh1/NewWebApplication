package com.WebApplication.ServiceImpl;

import com.WebApplication.Entity.FacilityTitle;
import com.WebApplication.Entity.WebUrlMapping;
import com.WebApplication.Repository.FacilityTitleRepository;
import com.WebApplication.Repository.WebUrlMappingRepository;
import com.WebApplication.Service.FacilityTitleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@Service
public class FacilityTitleServiceImpl implements FacilityTitleService {

    private final FacilityTitleRepository repository;
    private final PermissionServiceImpl permissionService;
    private final WebUrlMappingRepository webUrlMappingRepository;

    @Autowired
    public FacilityTitleServiceImpl(FacilityTitleRepository repository,
                                    PermissionServiceImpl permissionService,
                                    WebUrlMappingRepository webUrlMappingRepository) {
        this.repository = repository;
        this.permissionService = permissionService;
        this.webUrlMappingRepository = webUrlMappingRepository;
    }

    @Override
    public FacilityTitle createFacilityTitle(FacilityTitle facilityTitle, String role, String email, String dynamicPart) {
        if (!permissionService.hasPermission(role, email, "POST")) {
            throw new AccessDeniedException("No permission to create facility title");
        }

        try {
            String branchCode = permissionService.fetchBranchCodeByRole(role, email);
            facilityTitle.setRole(role);
            facilityTitle.setCreatedByEmail(email);
            facilityTitle.setBranchCode(branchCode);

            // Check for existing facility title with same name and branch
            Optional<FacilityTitle> existing = repository.findByFacilityTitleAndBranchCode(
                    facilityTitle.getFacilityTitle(), branchCode);
            if (existing.isPresent()) {
                throw new RuntimeException("Facility Title already exists for this branch");
            }

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

            facilityTitle.setWebUrlMapping(webUrlMapping);

            return repository.save(facilityTitle);
        } catch (Exception e) {
            throw new RuntimeException("Failed to create facility title", e);
        }
    }

    @Override
    public FacilityTitle publicCreateFacilityTitle(String facilityTitle, String dynamicPart) {
        try {
            FacilityTitle newTitle = new FacilityTitle();
            newTitle.setFacilityTitle(facilityTitle);
            newTitle.setRole("PUBLIC_USER");
            newTitle.setCreatedByEmail("public@noreply.com");
            newTitle.setBranchCode("PUBLIC");

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

            newTitle.setWebUrlMapping(webUrlMapping);

            return repository.save(newTitle);
        } catch (Exception e) {
            throw new RuntimeException("Failed to create public facility title", e);
        }
    }

    @Override
    public FacilityTitle updateFacilityTitle(Long id, FacilityTitle facilityTitle, String role, String email) {
        if (!permissionService.hasPermission(role, email, "PUT")) {
            throw new AccessDeniedException("No permission to update facility title");
        }

        FacilityTitle existing = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Facility Title not found"));

        existing.setFacilityTitle(facilityTitle.getFacilityTitle());
        return repository.save(existing);
    }

    @Override
    public void deleteFacilityTitle(Long id, String role, String email) {
        if (!permissionService.hasPermission(role, email, "DELETE")) {
            throw new AccessDeniedException("No permission to delete facility title");
        }

        repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Facility Title not found"));

        repository.deleteById(id);
    }

    @Override
    public List<FacilityTitle> getAllFacilityTitles(String role, String email, String branchCode) {
        if (!permissionService.hasPermission(role, email, "GET")) {
            throw new AccessDeniedException("No permission to view facility titles");
        }

        return repository.findByBranchCode(branchCode);
    }

    @Override
    public FacilityTitle getFacilityTitleById(Long id, String role, String email) {
        if (!permissionService.hasPermission(role, email, "GET")) {
            throw new AccessDeniedException("No permission to view facility title");
        }

        return repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Facility Title not found"));
    }

    @Override
    public Optional<FacilityTitle> getFacilityTitleByEmail(String email) {
        return repository.findByCreatedByEmail(email);
    }

    @Override
    public List<FacilityTitle> getFacilityTitlesByDynamicPart(String dynamicPart) {
        return repository.findByWebUrlMapping_DynamicPart(dynamicPart);
    }

    @Override
    public FacilityTitle getFacilityTitleByDynamicPart(String dynamicPart) throws NoSuchElementException {
        return repository.findFirstByWebUrlMapping_DynamicPart(dynamicPart)
                .orElseThrow(() -> new NoSuchElementException("No facility title found for dynamicPart: " + dynamicPart));
    }
}