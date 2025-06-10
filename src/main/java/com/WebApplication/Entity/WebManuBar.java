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
@Table(name="WebManuBar")
public class WebManuBar {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long webManuBarId;

    private String manuBarColor;
    private String menubarImage;

    @Email
    private String createdByEmail;
    private String role;
    private String branchCode;

    @ManyToOne
    @JoinColumn(name = "web_url_id") // FK in FacilityTitle table
    @JsonBackReference
    private WebUrlMapping webUrlMapping;
}