package com.WebApplication.ServiceImpl;

import com.WebApplication.Entity.WebJobCareerOption;
import com.WebApplication.Entity.WebUrlMapping;
import com.WebApplication.Repository.WebJobCareerOptionRepository;
import com.WebApplication.Repository.WebUrlMappingRepository;
import com.WebApplication.Service.WebJobCareerOptionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@Service
public class WebJobCareerOptionServiceImpl implements WebJobCareerOptionService {

    private final WebJobCareerOptionRepository repository;
    private final PermissionServiceImpl permissionService;
    private final S3Service s3Service;
    private final WebUrlMappingRepository webUrlMappingRepository;

    @Autowired
    public WebJobCareerOptionServiceImpl(WebJobCareerOptionRepository repository,
                                         PermissionServiceImpl permissionService,
                                         S3Service s3Service,
                                         WebUrlMappingRepository webUrlMappingRepository) {
        this.repository = repository;
        this.permissionService = permissionService;
        this.s3Service = s3Service;
        this.webUrlMappingRepository = webUrlMappingRepository;
    }

    @Override
    public WebJobCareerOption createWebJobCareerOption(WebJobCareerOption job, MultipartFile resume,
                                                       String role, String email, String dynamicPart) throws IOException {
        if (!permissionService.hasPermission(role, email, "POST")) {
            throw new AccessDeniedException("No permission to create job");
        }

        try {
            String branchCode = permissionService.fetchBranchCodeByRole(role, email);
            job.setRole(role);
            job.setCreatedByEmail(email);
            job.setBranchCode(branchCode);
            job.setPostDate(LocalDate.now());

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

            job.setWebUrlMapping(webUrlMapping);

            if (resume != null && !resume.isEmpty()) {
                String resumeUrl = s3Service.uploadImage(resume);
                job.setResumeUrl(resumeUrl);
            }

            return repository.save(job);
        } catch (IOException e) {
            throw new RuntimeException("Failed to upload resume", e);
        }
    }

    @Override
    public WebJobCareerOption publicCreateWebJobCareerOption(String title, String description, String location,
                                                             String salaryRange, String responsibilities,
                                                             MultipartFile resume, String dynamicPart) throws IOException {
        try {
            WebJobCareerOption job = new WebJobCareerOption();
            job.setTitle(title);
            job.setDescription(description);
            job.setLocation(location);
            job.setSalaryRange(salaryRange);
            job.setResponsibilities(responsibilities);
            job.setRole("PUBLIC_USER");
            job.setCreatedByEmail("public@noreply.com");
            job.setBranchCode("PUBLIC");
            job.setPostDate(LocalDate.now());

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

            job.setWebUrlMapping(webUrlMapping);

            if (resume != null && !resume.isEmpty()) {
                String resumeUrl = s3Service.uploadImage(resume);
                job.setResumeUrl(resumeUrl);
            }

            return repository.save(job);
        } catch (IOException e) {
            throw new RuntimeException("Failed to upload resume", e);
        }
    }

    @Override
    public WebJobCareerOption updateWebJobCareerOption(Long id, WebJobCareerOption updated,
                                                       MultipartFile resume, String role, String email) throws IOException {
        if (!permissionService.hasPermission(role, email, "PUT")) {
            throw new AccessDeniedException("No permission to update job");
        }

        WebJobCareerOption existing = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Job not found"));

        // Update fields
        existing.setTitle(updated.getTitle());
        existing.setDescription(updated.getDescription());
        existing.setLocation(updated.getLocation());
        existing.setSalaryRange(updated.getSalaryRange());
        existing.setJobCareerOptionColor(updated.getJobCareerOptionColor());
        existing.setResponsibilities(updated.getResponsibilities());

        if (updated.getLastDateToApply() != null) {
            existing.setLastDateToApply(updated.getLastDateToApply());
        }

        if (resume != null && !resume.isEmpty()) {
            String resumeUrl = s3Service.uploadImage(resume);
            existing.setResumeUrl(resumeUrl);
        }

        return repository.save(existing);
    }

    @Override
    public void deleteWebJobCareerOptionById(Long id, String role, String email) {
        if (!permissionService.hasPermission(role, email, "DELETE")) {
            throw new AccessDeniedException("No permission to delete job");
        }

        repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Job not found"));

        repository.deleteById(id);
    }

    @Override
    public List<WebJobCareerOption> getAllWebJobCareerOptions(String role, String email, String branchCode) {
        if (!permissionService.hasPermission(role, email, "GET")) {
            throw new AccessDeniedException("No permission to view jobs");
        }

        return repository.findAllByBranchCode(branchCode);
    }

    @Override
    public WebJobCareerOption getWebJobCareerOptionById(Long id, String role, String email) {
        if (!permissionService.hasPermission(role, email, "GET")) {
            throw new AccessDeniedException("No permission to view job");
        }

        return repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Job not found"));
    }

    @Override
    public Optional<WebJobCareerOption> getWebJobCareerOptionByEmail(String email) {
        return repository.findByCreatedByEmail(email);
    }


    @Override
    public void updateJobColorByBranchCode(String branchCode, String color, String role, String email) {
        if (!permissionService.hasPermission(role, email, "PUT")) {
            throw new AccessDeniedException("No permission to update job color");
        }
        // Implementation depends on your requirements
    }

    @Override
    public void deleteJobColorByBranchCode(String branchCode, String role, String email) {
        if (!permissionService.hasPermission(role, email, "DELETE")) {
            throw new AccessDeniedException("No permission to delete job color");
        }

    }

    @Override
    public List<WebJobCareerOption> getJobsByDynamicPart(String dynamicPart) {
        return repository.findByWebUrlMapping_DynamicPart(dynamicPart);
    }

    @Override
    public WebJobCareerOption getJobByDynamicPart(String dynamicPart) throws NoSuchElementException {
        return repository.findFirstByWebUrlMapping_DynamicPart(dynamicPart)
                .orElseThrow(() -> new NoSuchElementException("No job found for dynamicPart: " + dynamicPart));
    }
}