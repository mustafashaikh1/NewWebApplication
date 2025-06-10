// DTO
package com.WebApplication.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class WebHRDetailsDTO {
    private Long id;
    private String hrName;
    private String email;
    private String contact;
    private Long webJobCareerOptionId; // Only store WebJobCareerOption ID
    private String branchCode;
    private String createdByEmail;
    private String role;
}