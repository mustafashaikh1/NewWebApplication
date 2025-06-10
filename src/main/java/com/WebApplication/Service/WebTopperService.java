package com.WebApplication.Service;

import com.WebApplication.Entity.WebTopper;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

public interface WebTopperService {

    WebTopper createWebTopper(WebTopper topper, List<MultipartFile> images, String role, String email, String dynamicPart) throws IOException;

    WebTopper publicCreateWebTopper(String name, Double totalMarks, String post,
                                    Integer rank, Integer year, String topperColor,
                                    List<MultipartFile> topperImages,
                                    String dynamicPart) throws IOException;

    WebTopper updateWebTopper(Long id, WebTopper updated, List<MultipartFile> images, String role, String email) throws IOException;

    void deleteWebTopperById(Long id, String role, String email);

    WebTopper getWebTopperById(Long id, String role, String email);

    List<WebTopper> getAllWebToppers(String role, String email, String branchCode);

    Optional<WebTopper> getWebTopperByEmail(String email);

    List<WebTopper> getToppersByDynamicPart(String dynamicPart);

    WebTopper getUserTopperByDynamicPart(String dynamicPart) throws NoSuchElementException;
}