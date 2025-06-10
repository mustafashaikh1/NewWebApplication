package com.WebApplication.Service;

import com.WebApplication.Entity.WebSlideBar;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

public interface WebSlideBarService {
    WebSlideBar createWebSlideBar(WebSlideBar webSlideBar, List<MultipartFile> slideImages,
                                  String role, String email, String dynamicPart) throws IOException;

    WebSlideBar publicCreateWebSlideBar(String slideBarColor, List<MultipartFile> slideImages,
                                        String dynamicPart) throws IOException;

    List<WebSlideBar> getAllWebSlideBars(String role, String email, String branchCode);

    WebSlideBar updateWebSlideBar(Long id, WebSlideBar updatedWebSlideBar,
                                  List<MultipartFile> slideImages, String role, String email) throws IOException;

    void deleteWebSlideBar(Long id, String role, String email);

    WebSlideBar getWebSlideBarById(Long id, String role, String email);

    Optional<WebSlideBar> getWebSlideBarByEmail(String email);

    List<WebSlideBar> getWebSlideBarsByDynamicPart(String dynamicPart);

    WebSlideBar getUserWebSlideBarByDynamicPart(String dynamicPart);
}