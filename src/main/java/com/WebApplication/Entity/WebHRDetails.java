package com.WebApplication.Entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Entity
@Table(name = "WebHRDetails")
public class WebHRDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String hrName;
    private String email;
    private String contact;


    @Email
    private String branchCode;
    private String createdByEmail;
    private String role;

    @OneToOne
    @JoinColumn(name = "web_job_career_option_id", referencedColumnName = "webJobCareerOptionId")
    @JsonManagedReference
    private WebJobCareerOption webJobCareerOption;

    // Getters and Setters
    public WebJobCareerOption getWebJobCareerOption() {
        return webJobCareerOption;
    }

    public void setWebJobCareerOption(WebJobCareerOption webJobCareerOption) {
        this.webJobCareerOption = webJobCareerOption;
    }


    @ManyToOne
    @JoinColumn(name = "web_url_id") // FK in FacilityTitle table
    @JsonBackReference
    private WebUrlMapping webUrlMapping;
}