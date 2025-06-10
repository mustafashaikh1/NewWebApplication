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
@Table(name = "WebFooter")
public class WebFooter {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long footerId;

    private String title;
    private String footerColor;

    private String instagramLink;
    private String facebookLink;
    private String twitterLink;
    private String youtubeLink;
    private String whatsappLink;

    @Email
    private String email;
    private String mobileNumber;
    private String address;

    @Email
    private String createdByEmail;
    private String role;
    private String branchCode;

    @OneToOne
    @JoinColumn(name = "web_url_id", nullable = false)
    @JsonBackReference
    private WebUrlMapping webUrlMapping;

}