package com.WebApplication.Service;

import com.WebApplication.Entity.WebTestimonials;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

public interface WebTestimonialsService {

    WebTestimonials createWebTestimonial(WebTestimonials testimonial, MultipartFile image,
                                         String role, String email, String dynamicPart) throws IOException;

    WebTestimonials publicCreateWebTestimonial(String testimonialTitle, String testimonialName,
                                               String exam, String post, String description,
                                               MultipartFile testimonialImage, String dynamicPart,
                                               String testimonialColor) throws IOException;

    WebTestimonials updateWebTestimonial(Long id, WebTestimonials updated,
                                         MultipartFile image, String role, String email) throws IOException;

    void deleteWebTestimonialById(Long id, String role, String email);

    List<WebTestimonials> getAllWebTestimonials(String role, String email, String branchCode);

    WebTestimonials getWebTestimonialById(Long id, String role, String email);

    Optional<WebTestimonials> getWebTestimonialByEmail(String email);

    List<WebTestimonials> getTestimonialsByDynamicPart(String dynamicPart);

    WebTestimonials getUserTestimonialByDynamicPart(String dynamicPart) throws NoSuchElementException;

    void updateTestimonialColorByBranchCode(String branchCode, String testimonialColor, String role, String email);

    void deleteTestimonialColorByBranchCode(String branchCode, String role, String email);
}