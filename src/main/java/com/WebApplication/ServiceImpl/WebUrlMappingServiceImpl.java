package com.WebApplication.ServiceImpl;

import com.WebApplication.Entity.WebUrlMapping;
import com.WebApplication.Repository.WebUrlMappingRepository;
import com.WebApplication.Service.WebUrlMappingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class WebUrlMappingServiceImpl implements WebUrlMappingService {

    @Autowired
    private WebUrlMappingRepository repository;

    @Autowired
    private PermissionServiceImpl permissionService;

    @Override
    public WebUrlMapping create(String dynamicPart, String role, String email) {
        if (!permissionService.hasPermission(role, email, "POST")) {
            throw new AccessDeniedException("No permission to create URL mapping");
        }

        // Check if dynamicPart already exists
        Optional<WebUrlMapping> existing = repository.findByDynamicPart(dynamicPart);
        if (existing.isPresent()) {
            throw new RuntimeException("Dynamic part already exists: " + dynamicPart);
        }

        WebUrlMapping mapping = new WebUrlMapping();
        mapping.setDynamicPart(dynamicPart);
        mapping.setCreatedByEmail(email);
        mapping.setRole(role);
        mapping.setBranchCode(permissionService.fetchBranchCodeByRole(role, email));
        return repository.save(mapping);
    }

    @Override
    public WebUrlMapping publicCreate(String dynamicPart) {
        // Check if dynamicPart already exists
        Optional<WebUrlMapping> existing = repository.findByDynamicPart(dynamicPart);
        if (existing.isPresent()) {
            throw new RuntimeException("Dynamic part already exists: " + dynamicPart);
        }

        WebUrlMapping mapping = new WebUrlMapping();
        mapping.setDynamicPart(dynamicPart);
        mapping.setCreatedByEmail("public-user@" + dynamicPart + ".com");
        mapping.setRole("PUBLIC_USER");
        mapping.setBranchCode("PUBLIC");

        return repository.save(mapping);
    }
    @Override
    public WebUrlMapping update(Long id, String dynamicPart, String role, String email) {
        if (!permissionService.hasPermission(role, email, "PUT")) {
            throw new AccessDeniedException("No permission to update URL mapping");
        }

        WebUrlMapping mapping = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Mapping not found"));
        mapping.setDynamicPart(dynamicPart);
        return repository.save(mapping);
    }

    @Override
    public void delete(Long id, String role, String email) {
        if (!permissionService.hasPermission(role, email, "DELETE")) {
            throw new AccessDeniedException("No permission to delete URL mapping");
        }

        repository.findById(id).orElseThrow(() -> new RuntimeException("Mapping not found"));
        repository.deleteById(id);
    }

    @Override
    public WebUrlMapping getById(Long id, String role, String email) {
        if (!permissionService.hasPermission(role, email, "GET")) {
            throw new AccessDeniedException("No permission to view URL mapping");
        }

        return repository.findById(id).orElseThrow(() -> new RuntimeException("Mapping not found"));
    }

    @Override
    public List<WebUrlMapping> getAll(String role, String email, String branchCode) {
        if (!permissionService.hasPermission(role, email, "GET")) {
            throw new AccessDeniedException("No permission to view mappings");
        }

        return repository.findAllByBranchCode(branchCode);
    }

    @Override
    public Optional<WebUrlMapping> getByEmail(String email) {
        return repository.findByCreatedByEmail(email);
    }

    @Override
    public Optional<WebUrlMapping> findByDynamicPart(String dynamicPart) {
        return repository.findByDynamicPart(dynamicPart);
    }

}