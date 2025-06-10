package com.WebApplication.Service;

import com.WebApplication.Entity.WebManuBar;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

public interface WebManuBarService {
    WebManuBar createWebManuBar(WebManuBar webManuBar, MultipartFile menubarImage, String role, String email,String dynamicPart);

    WebManuBar publicCreateWebManuBar(String manuBarColor, MultipartFile menubarImage, String dynamicPart);
    List<WebManuBar> getAllWebManuBars(String role, String email, String branchCode);
    WebManuBar updateWebManuBar(Long id, WebManuBar updatedWebManuBar, MultipartFile menubarImage, String role, String email) throws IOException;
    void deleteWebManuBar(Long id, String role, String email);
    WebManuBar getWebManuBarById(Long id, String role, String email);
    Optional<WebManuBar> getWebManuBarByEmail(String email);

    List<WebManuBar> getWebManuBarsByDynamicPart(String dynamicPart);
    WebManuBar getUserWebManuBarByDynamicPart(String dynamicPart);






}