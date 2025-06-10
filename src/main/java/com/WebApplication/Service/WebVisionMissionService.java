package com.WebApplication.Service;

import com.WebApplication.Entity.WebVisionMission;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

public interface WebVisionMissionService {
    WebVisionMission createWebVisionMission(WebVisionMission visionMission, MultipartFile directorImage,
                                            String role, String email, String dynamicPart) throws IOException;

    WebVisionMission publicCreateWebVisionMission(String vision, String mission, String visionmissionColor,
                                                  String directorMessage, String directorName, String description,
                                                  MultipartFile directorImage, String dynamicPart) throws IOException;

    WebVisionMission updateWebVisionMission(Long id, WebVisionMission updatedVisionMission,
                                            MultipartFile directorImage, String role, String email) throws IOException;

    void deleteWebVisionMission(Long id, String role, String email);

    List<WebVisionMission> getAllWebVisionMissions(String role, String email, String branchCode);

    WebVisionMission getWebVisionMissionById(Long id, String role, String email);

    Optional<WebVisionMission> getWebVisionMissionByEmail(String email);

    List<WebVisionMission> getWebVisionMissionsByDynamicPart(String dynamicPart);

    WebVisionMission getUserWebVisionMissionByDynamicPart(String dynamicPart);
}