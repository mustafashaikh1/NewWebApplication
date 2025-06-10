package com.WebApplication.Service;

import com.WebApplication.Entity.WebCourse;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

public interface WebCourseService {
    WebCourse createWebCourse(WebCourse webCourse, MultipartFile courseImage,
                              String role, String email, String dynamicPart) throws IOException;

    WebCourse publicCreateWebCourse(String courseTitle, String link, String description,String courseColor,
                                    MultipartFile courseImage, String dynamicPart);

    WebCourse updateWebCourse(Long id, WebCourse webCourse, MultipartFile courseImage,
                              String role, String email) throws IOException;

    void deleteWebCourse(Long id, String role, String email);

    WebCourse getWebCourseById(Long id, String role, String email);

    List<WebCourse> getAllWebCourses(String role, String email, String branchCode);

    void addWebCourseColorByBranchCode(String branchCode, String courseColor,
                                       String role, String email);

    void updateWebCourseColorByBranchCode(String branchCode, String courseColor,
                                          String role, String email);

    boolean getWebCourseColorStatusByBranchCode(String branchCode, String courseColor);

    void deleteWebCourseColorByBranchCode(String branchCode, String role, String email);

    Optional<WebCourse> getWebCourseByEmail(String email);

    List<WebCourse> getWebCoursesByDynamicPart(String dynamicPart);

    WebCourse getUserWebCourseByDynamicPart(String dynamicPart);
}