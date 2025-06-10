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
@Table(name = "WebCounter")
public class WebCounter {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String counterName1;
    private int countValue1;
    private String counterColor1;

    private String counterName2;
    private int countValue2;
    private String counterColor2;

    private String counterName3;
    private int countValue3;
    private String counterColor3;

    @Email
    private String createdByEmail;
    private String role;
    private String branchCode;

    @ManyToOne
    @JoinColumn(name = "web_url_id", nullable = false)
    @JsonBackReference
    private WebUrlMapping webUrlMapping;
}