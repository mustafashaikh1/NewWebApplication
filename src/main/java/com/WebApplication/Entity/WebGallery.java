package com.WebApplication.Entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Entity
@Table(name = "WebGallery")
public class WebGallery {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long webGalleryId;

    private String eventName;
    private Integer year;
    private String galleryColor;

    @Email
    private String createdByEmail;
    private String role;
    private String branchCode;

    @ElementCollection
    @CollectionTable(name = "web_gallery_images", joinColumns = @JoinColumn(name = "web_gallery_id"))
    @Column(name = "image_url")
    private List<String> galleryImages = new ArrayList<>();

    @ElementCollection
    @CollectionTable(name = "web_gallery_image_url_ids", joinColumns = @JoinColumn(name = "web_gallery_id"))
    @Column(name = "image_url_id")
    private List<Integer> imageUrlIds = new ArrayList<>();

    @ManyToOne
    @JoinColumn(name = "web_url_id")
    @JsonBackReference
    private WebUrlMapping webUrlMapping;
}