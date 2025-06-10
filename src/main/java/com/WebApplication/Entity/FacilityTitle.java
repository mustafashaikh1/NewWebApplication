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
@Table(name = "WebFacilitytitle")
public class FacilityTitle {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long facilityTitleId;

    private String facilityTitle;

    @Email
    private String createdByEmail;
    private String role;
    private String branchCode;


    @ManyToOne
    @JoinColumn(name = "web_url_id")
    @JsonBackReference
    private WebUrlMapping webUrlMapping;

}