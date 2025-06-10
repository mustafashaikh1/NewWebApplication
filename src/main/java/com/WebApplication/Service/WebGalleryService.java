package com.WebApplication.Service;

import com.WebApplication.Entity.WebGallery;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface WebGalleryService {
    WebGallery createWebGallery(WebGallery webGallery, List<MultipartFile> galleryImages,
                                String role, String email, String dynamicPart) throws IOException;

    WebGallery publicCreateWebGallery(String eventName, Integer year, String galleryColor,
                                      List<MultipartFile> galleryImages, String dynamicPart) throws IOException;

    WebGallery updateWebGallery(Long id, WebGallery updatedWebGallery,
                                List<MultipartFile> galleryImages, String role, String email) throws IOException;

    void deleteWebGallery(Long id, String role, String email);

    List<WebGallery> getAllWebGalleries(String role, String email, String branchCode);

    WebGallery getWebGalleryById(Long id, String role, String email);

    List<WebGallery> getWebGalleriesByDynamicPart(String dynamicPart);

    WebGallery getUserWebGalleryByDynamicPart(String dynamicPart);
}