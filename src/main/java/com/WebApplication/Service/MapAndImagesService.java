package com.WebApplication.Service;

import com.WebApplication.Entity.WebMapAndImages;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

public interface MapAndImagesService {
    WebMapAndImages createMapAndImages(WebMapAndImages mapAndImages, MultipartFile contactImage,
                                       String role, String email, String dynamicPart) throws IOException;
    WebMapAndImages publicCreateMapAndImages(String maps, MultipartFile contactImage, String dynamicPart) throws IOException;
    WebMapAndImages updateMapAndImages(Long id, WebMapAndImages mapAndImages,
                                       MultipartFile contactImage, String role, String email) throws IOException;
    void deleteMapAndImages(Long id, String role, String email);
    List<WebMapAndImages> getAllMapAndImages(String role, String email, String branchCode);
    Optional<WebMapAndImages> getMapAndImagesByEmail(String email);
    List<WebMapAndImages> getMapAndImagesByDynamicPart(String dynamicPart);
    WebMapAndImages getUserMapAndImagesByDynamicPart(String dynamicPart);
}