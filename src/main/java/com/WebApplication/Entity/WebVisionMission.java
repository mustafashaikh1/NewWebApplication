// Updated WebVisionMission.java
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
@Table(name = "WebVisionMission")
public class WebVisionMission {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "vision", nullable = false, length = 1500)
    private String vision;

    @Column(name = "mission", nullable = false, length = 1500)
    private String mission;

    private String visionmissionColor;

    @Column(name = "directorMessage", length = 2000)
    private String directorMessage;

    @Column(name = "directorName", length = 255)
    private String directorName;

    @Column(name = "directorImage")
    private String directorImage;

    @Column(name = "description", length = 3000)
    private String description;

    @Email
    private String createdByEmail;
    private String role;
    private String branchCode;


    @ManyToOne
    @JoinColumn(name = "web_url_id")
    @JsonBackReference
    private WebUrlMapping webUrlMapping;
}
