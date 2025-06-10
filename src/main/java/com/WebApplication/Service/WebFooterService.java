package com.WebApplication.Service;

import com.WebApplication.Entity.WebFooter;

import java.util.List;
import java.util.Optional;

public interface WebFooterService {
    WebFooter createWebFooter(WebFooter footer, String role, String email, String dynamicPart);
    WebFooter publicCreateWebFooter(WebFooter footer, String dynamicPart);
    List<WebFooter> getAllWebFooters(String role, String email, String branchCode);
    WebFooter updateWebFooter(Long id, WebFooter updatedFooter, String role, String email);
    void deleteWebFooter(Long id, String role, String email);
    WebFooter getWebFooterById(Long id, String role, String email);
    Optional<WebFooter> getWebFooterByEmail(String email);
    List<WebFooter> getWebFootersByDynamicPart(String dynamicPart);
    WebFooter getWebFooterByDynamicPart(String dynamicPart);
}