package com.WebApplication.ServiceImpl;

import com.WebApplication.Entity.WebCounter;
import com.WebApplication.Entity.WebUrlMapping;
import com.WebApplication.Repository.WebCounterRepository;
import com.WebApplication.Repository.WebUrlMappingRepository;
import com.WebApplication.Service.WebCounterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@Service
public class WebCounterServiceImpl implements WebCounterService {

    private final WebCounterRepository repository;
    private final PermissionServiceImpl permissionService;
    private final WebUrlMappingRepository webUrlMappingRepository;

    @Autowired
    public WebCounterServiceImpl(WebCounterRepository repository,
                                 PermissionServiceImpl permissionService,
                                 WebUrlMappingRepository webUrlMappingRepository) {
        this.repository = repository;
        this.permissionService = permissionService;
        this.webUrlMappingRepository = webUrlMappingRepository;
    }

    @Override
    public WebCounter createWebCounter(WebCounter counter, String role, String email, String dynamicPart) {
        if (!permissionService.hasPermission(role, email, "POST")) {
            throw new AccessDeniedException("No permission to create counter");
        }

        try {
            String branchCode = permissionService.fetchBranchCodeByRole(role, email);
            counter.setRole(role);
            counter.setCreatedByEmail(email);
            counter.setBranchCode(branchCode);

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

            counter.setWebUrlMapping(webUrlMapping);

            return repository.save(counter);
        } catch (Exception e) {
            throw new RuntimeException("Failed to create counter", e);
        }
    }

    @Override
    public WebCounter publicCreateWebCounter(String counterName1, int countValue1, String counterColor1,
                                             String counterName2, int countValue2, String counterColor2,
                                             String counterName3, int countValue3, String counterColor3,
                                             String dynamicPart) {
        try {
            WebCounter counter = new WebCounter();
            counter.setCounterName1(counterName1);
            counter.setCountValue1(countValue1);
            counter.setCounterColor1(counterColor1);
            counter.setCounterName2(counterName2);
            counter.setCountValue2(countValue2);
            counter.setCounterColor2(counterColor2);
            counter.setCounterName3(counterName3);
            counter.setCountValue3(countValue3);
            counter.setCounterColor3(counterColor3);
            counter.setRole("PUBLIC_USER");
            counter.setCreatedByEmail("public@noreply.com");
            counter.setBranchCode("PUBLIC");

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

            counter.setWebUrlMapping(webUrlMapping);

            return repository.save(counter);
        } catch (Exception e) {
            throw new RuntimeException("Failed to create public counter", e);
        }
    }

    @Override
    public WebCounter updateWebCounter(Long id, WebCounter updated, String role, String email) {
        if (!permissionService.hasPermission(role, email, "PUT")) {
            throw new AccessDeniedException("No permission to update counter");
        }

        WebCounter existing = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Counter not found"));

        // Update fields
        existing.setCounterName1(updated.getCounterName1());
        existing.setCountValue1(updated.getCountValue1());
        existing.setCounterColor1(updated.getCounterColor1());
        existing.setCounterName2(updated.getCounterName2());
        existing.setCountValue2(updated.getCountValue2());
        existing.setCounterColor2(updated.getCounterColor2());
        existing.setCounterName3(updated.getCounterName3());
        existing.setCountValue3(updated.getCountValue3());
        existing.setCounterColor3(updated.getCounterColor3());

        return repository.save(existing);
    }

    @Override
    public void deleteWebCounterById(Long id, String role, String email) {
        if (!permissionService.hasPermission(role, email, "DELETE")) {
            throw new AccessDeniedException("No permission to delete counter");
        }

        repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Counter not found"));

        repository.deleteById(id);
    }

    @Override
    public List<WebCounter> getAllWebCounters(String role, String email, String branchCode) {
        if (!permissionService.hasPermission(role, email, "GET")) {
            throw new AccessDeniedException("No permission to view counters");
        }

        return repository.findAllByBranchCode(branchCode);
    }

    @Override
    public WebCounter getWebCounterById(Long id, String role, String email) {
        if (!permissionService.hasPermission(role, email, "GET")) {
            throw new AccessDeniedException("No permission to view counter");
        }

        return repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Counter not found"));
    }

    @Override
    public Optional<WebCounter> getWebCounterByEmail(String email) {
        return repository.findByCreatedByEmail(email);
    }

    @Override
    public List<WebCounter> getCountersByDynamicPart(String dynamicPart) {
        return repository.findByWebUrlMapping_DynamicPart(dynamicPart);
    }

    @Override
    public WebCounter getCounterByDynamicPart(String dynamicPart) throws NoSuchElementException {
        return repository.findFirstByWebUrlMapping_DynamicPart(dynamicPart)
                .orElseThrow(() -> new NoSuchElementException("No counter found for dynamicPart: " + dynamicPart));
    }
}