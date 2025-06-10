package com.WebApplication.Entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Entity
@Table(name = "WebSlideBar")
public class WebSlideBar {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long webSlideBarId;

    private String slideBarColor;

    @ElementCollection
    @CollectionTable(name = "slide_images", joinColumns = @JoinColumn(name = "slide_id"))
    @Column(name = "image_url")
    private List<String> slideImages;

    @ElementCollection
    @CollectionTable(name = "slide_image_ids", joinColumns = @JoinColumn(name = "slide_id"))
    @Column(name = "image_id")
    private List<Integer> imageUrlIds;

    @Email
    private String createdByEmail;
    private String role;
    private String branchCode;

    @ManyToOne
    @JoinColumn(name = "web_url_id")
    @JsonBackReference
    private WebUrlMapping webUrlMapping;
}