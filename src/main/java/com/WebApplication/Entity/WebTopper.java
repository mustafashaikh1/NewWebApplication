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
@Table(name = "WebTopper")
public class WebTopper {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long topperId;

    private String name;
    private Double totalMarks;
    private String post;

    @Column(name = "`rank`")
    private Integer rank;

    private Integer year;
    private String topperColor;

    @Email
    private String createdByEmail;
    private String role;
    private String branchCode;

    @ElementCollection
    @CollectionTable(name = "topper_images", joinColumns = @JoinColumn(name = "topper_id"))
    @Column(name = "image_url")
    private List<String> topperImages;

    @ElementCollection
    @CollectionTable(name = "topper_image_ids", joinColumns = @JoinColumn(name = "topper_id"))
    @Column(name = "image_url_id")
    private List<Integer> imageUrlIds;

    @ManyToOne
    @JoinColumn(name = "web_url_id")
    @JsonBackReference
    private WebUrlMapping webUrlMapping;
}