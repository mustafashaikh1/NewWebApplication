
package com.WebApplication.Entity;

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
@Table(name = "WebUrl")
public class WebUrlMapping {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String dynamicPart;

    @Email
    private String createdByEmail;
    private String role;
    private String branchCode;

    @OneToMany(mappedBy = "webUrlMapping", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<FacilityTitle> facilityTitles;

    @OneToMany(mappedBy = "webUrlMapping", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<WebAboutUs> aboutUsEntries;

    @OneToMany(mappedBy = "webUrlMapping", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<WebAwardsAndAccolades> awardsAndAccolades;

    @OneToMany(mappedBy = "webUrlMapping", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<WebCounter> webCounters;

    @OneToMany(mappedBy = "webUrlMapping", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<WebCourse> courses;


    @OneToMany(mappedBy = "webUrlMapping", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<WebFacility> facilities;

    @OneToOne(mappedBy = "webUrlMapping", cascade = CascadeType.ALL, orphanRemoval = true)
    private WebFooter webFooter;

    @OneToMany(mappedBy = "webUrlMapping", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<WebGallery> webGalleries;

    @OneToMany(mappedBy = "webUrlMapping", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<WebHRDetails> webHRDetails;

    @OneToMany(mappedBy = "webUrlMapping", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<WebManuBar> webManuBars; // One WebUrlMapping can have multiple menu bars

    @OneToMany(mappedBy = "webUrlMapping", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<WebMapAndImages> webMapAndImages;

    @OneToMany(mappedBy = "webUrlMapping", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<WebSlideBar> webSlideBars;

    @OneToMany(mappedBy = "webUrlMapping", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<WebTestimonials> webTestimonials;

    @OneToMany(mappedBy = "webUrlMapping", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<WebTopper> webToppers;

    @OneToMany(mappedBy = "webUrlMapping", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<WebVisionMission> webVisionMissions;

    @OneToMany(mappedBy = "webUrlMapping", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<WebJobCareerOption> webJobCareerOptions;


    @OneToMany(mappedBy = "webUrlMapping", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<WebFacility> webFacility;


    @OneToMany(mappedBy = "webUrlMapping", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ContactForm> contactForms;



}
