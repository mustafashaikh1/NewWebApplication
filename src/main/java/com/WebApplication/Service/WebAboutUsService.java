package com.WebApplication.Service;

import com.WebApplication.Entity.WebAboutUs;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;

public interface WebAboutUsService {
    WebAboutUs createWebAboutUs(WebAboutUs webAboutUs, MultipartFile aboutUsImage,
                                String role, String email, String dynamicPart);

    WebAboutUs updateWebAboutUs(Long id, WebAboutUs webAboutUs, MultipartFile aboutUsImage,
                                String role, String email);

    void deleteWebAboutUs(Long id, String role, String email);

    WebAboutUs getWebAboutUsById(Long id, String role, String email);

    List<WebAboutUs> getAllWebAboutUs(String role, String email, String branchCode);

    Optional<WebAboutUs> getWebAboutUsByEmail(String email);

    // Public user methods
    WebAboutUs publicCreateWebAboutUs(String aboutUsTitle, String description,
                                      MultipartFile aboutUsImage, String dynamicPart);

    List<WebAboutUs> getWebAboutUsByDynamicPart(String dynamicPart);

    WebAboutUs getUserWebAboutUsByDynamicPart(String dynamicPart);
}