package com.WebApplication.ServiceImpl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Map;

@Service
public class PermissionServiceImpl {

    private final WebClient webClient;
    private final StaffService staffService;

    @Value("${client.superadmin.base-url}")
    private String superAdminBaseUrl;

    @Autowired
    public PermissionServiceImpl(WebClient webClient, StaffService staffService) {
        this.webClient = webClient;
        this.staffService = staffService;
    }

    public boolean hasPermission(String role, String email, String action) {
        String normalizedRole = role.toUpperCase();

        // USER role has limited permissions
        if ("USER".equals(normalizedRole)) {
            return switch (action.toUpperCase()) {
                case "GET", "POST" -> true;  // Allow read and create
                case "PUT", "DELETE" -> false; // Restrict modifications
                default -> false;
            };
        }

        // BRANCH role permission check via API
        if ("BRANCH".equals(normalizedRole)) {
            try {
                Boolean exists = webClient.get()
                        .uri(uriBuilder -> uriBuilder
                                .path("/existBranchbyemail")
                                .queryParam("email", email)
                                .build())
                        .retrieve()
                        .bodyToMono(Boolean.class)
                        .block();
                return Boolean.TRUE.equals(exists);
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
        }

        // STAFF permission check
        switch (normalizedRole) {
            case "STAFF":
                Map<String, Boolean> staffPerms = staffService.getPermissionsByEmail(email);
                return switch (action.toUpperCase()) {
                    case "GET" -> Boolean.TRUE.equals(staffPerms.get("cansGet"));
                    case "POST" -> Boolean.TRUE.equals(staffPerms.get("cansPost"));
                    case "PUT" -> Boolean.TRUE.equals(staffPerms.get("cansPut"));
                    case "DELETE" -> Boolean.TRUE.equals(staffPerms.get("cansDelete"));
                    default -> false;
                };

            case "DEPARTMENT":
                Map<String, Object> deptPerms = staffService.getCrudPermissionForDepartmentByEmail(email);
                return switch (action.toUpperCase()) {
                    case "GET" -> Boolean.TRUE.equals(deptPerms.get("candGet"));
                    case "POST" -> Boolean.TRUE.equals(deptPerms.get("candPost"));
                    case "PUT" -> Boolean.TRUE.equals(deptPerms.get("candPut"));
                    case "DELETE" -> Boolean.TRUE.equals(deptPerms.get("candDelete"));
                    default -> false;
                };

            default:
                return false;
        }
    }

    public String fetchBranchCodeByRole(String role, String email) {
        String normalizedRole = role.toLowerCase();

        // Default branch code for USER
        if ("user".equals(normalizedRole)) {
            return "PUBLIC_USER";
        }

        String endpoint = switch (normalizedRole) {
            case "branch" -> "/branch/getbranchcode";
            case "department" -> "/department/getbranchcode";
            case "staff" -> "/staff/getbranchcode";
            default -> throw new IllegalArgumentException("Invalid role: " + role);
        };

        try {
            return webClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .path(endpoint)
                            .queryParam("email", email)
                            .build())
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
