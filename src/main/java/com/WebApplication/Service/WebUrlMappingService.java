package com.WebApplication.Service;

import com.WebApplication.Entity.WebUrlMapping;

import java.util.List;
import java.util.Optional;

public interface WebUrlMappingService {
    WebUrlMapping create(String dynamicPart, String role, String email);
    WebUrlMapping publicCreate(String dynamicPart); // New method for public creation

    WebUrlMapping update(Long id, String dynamicPart, String role, String email);
    void delete(Long id, String role, String email);

    WebUrlMapping getById(Long id, String role, String email);
    List<WebUrlMapping> getAll(String role, String email, String branchCode);

    //USER//
    Optional<WebUrlMapping> getByEmail(String email);
    Optional<WebUrlMapping> findByDynamicPart(String dynamicPart);
}
