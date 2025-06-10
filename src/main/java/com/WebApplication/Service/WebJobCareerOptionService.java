package com.WebApplication.Service;

import com.WebApplication.Entity.WebJobCareerOption;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

public interface WebJobCareerOptionService {
    WebJobCareerOption createWebJobCareerOption(WebJobCareerOption job, MultipartFile resume,
                                                String role, String email, String dynamicPart) throws IOException;
    WebJobCareerOption publicCreateWebJobCareerOption(String title, String description, String location,
                                                      String salaryRange, String responsibilities,
                                                      MultipartFile resume, String dynamicPart) throws IOException;
    WebJobCareerOption updateWebJobCareerOption(Long id, WebJobCareerOption updatedJob,
                                                MultipartFile resume, String role, String email) throws IOException;
    void deleteWebJobCareerOptionById(Long id, String role, String email);
    List<WebJobCareerOption> getAllWebJobCareerOptions(String role, String email, String branchCode);
    WebJobCareerOption getWebJobCareerOptionById(Long id, String role, String email);
    Optional<WebJobCareerOption> getWebJobCareerOptionByEmail(String email);
    List<WebJobCareerOption> getJobsByDynamicPart(String dynamicPart);
    WebJobCareerOption getJobByDynamicPart(String dynamicPart) throws NoSuchElementException;
    void updateJobColorByBranchCode(String branchCode, String color, String role, String email);
    void deleteJobColorByBranchCode(String branchCode, String role, String email);
}