package com.WebApplication.Service;

import com.WebApplication.Entity.WebFacility;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

public interface WebFacilityService {
    // Admin methods
    WebFacility createWebFacility(WebFacility webFacility, MultipartFile facilityImage,
                                  String role, String email, String dynamicPart) throws IOException;

    WebFacility updateWebFacility(Long id, WebFacility webFacility, MultipartFile facilityImage,
                                  String role, String email) throws IOException;

    void deleteWebFacility(Long id, String role, String email);

    WebFacility getWebFacilityById(Long id, String role, String email);

    List<WebFacility> getAllWebFacilities(String role, String email, String branchCode);

    Optional<WebFacility> getWebFacilityByEmail(String email);

    // Public user methods
    WebFacility publicCreateWebFacility(String facilityName, Byte experienceInYear, String subject,
                                        String facilityEducation, String description, String facilityColor,
                                        MultipartFile facilityImage, String dynamicPart) throws IOException;

    List<WebFacility> getWebFacilitiesByDynamicPart(String dynamicPart);

    // Color management methods
    void addFacilityColorByBranchCode(String branchCode, String facilityColor);
    void updateFacilityColorByBranchCode(String branchCode, String facilityColor);
    void deleteFacilityColorByBranchCode(String branchCode);

    List<WebFacility> getFacilitiesByDynamicPart(String dynamicPart);
    WebFacility getUserFacilityByDynamicPart(String dynamicPart);
}