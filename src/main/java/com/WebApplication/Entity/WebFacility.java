package com.WebApplication.Entity;


import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Entity
@Table(name="WebFacility")
public class WebFacility {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long webFacilityId;

    private String facilityName;
    private Byte experienceInYear;
    private String subject;
    private String facilityEducation;
    private String facilityImage;

    @Lob
    @Column(length = 5000)
    private String description;

    private String facilityColor;

    @Email
    private String createdByEmail;
    private String role;
    private String branchCode;

    @ManyToOne
    @JoinColumn(name = "web_url_id", nullable = false)
    @JsonBackReference
    private WebUrlMapping webUrlMapping;

}