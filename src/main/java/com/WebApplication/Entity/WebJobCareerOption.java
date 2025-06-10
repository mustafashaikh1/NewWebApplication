package com.WebApplication.Entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Entity
@Table(name ="WebJob")
public class WebJobCareerOption {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long webJobCareerOptionId;

    private String title;
    private String description;
    private String location;
    private String salaryRange;
    private String jobCareerOptionColor;

    @Column(length = 500)
    private String responsibilities;



    private LocalDate postDate;

    private String resumeUrl;

    private String lastDateToApply;

    @Email
    private String createdByEmail;
    private String role;
    private String branchCode;

    @OneToOne(mappedBy = "webJobCareerOption", cascade = CascadeType.ALL)
    @JsonBackReference
    private WebHRDetails webHRDetails;

    @ManyToOne
    @JoinColumn(name = "web_url_id")
    @JsonBackReference
    private WebUrlMapping webUrlMapping;
}