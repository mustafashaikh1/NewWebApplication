package com.WebApplication.Service;

import com.WebApplication.Entity.FacilityTitle;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

public interface FacilityTitleService {
    FacilityTitle createFacilityTitle(FacilityTitle facilityTitle, String role, String email, String dynamicPart);
    FacilityTitle publicCreateFacilityTitle(String facilityTitle, String dynamicPart);
    FacilityTitle updateFacilityTitle(Long id, FacilityTitle facilityTitle, String role, String email);
    void deleteFacilityTitle(Long id, String role, String email);
    List<FacilityTitle> getAllFacilityTitles(String role, String email, String branchCode);
    FacilityTitle getFacilityTitleById(Long id, String role, String email);
    Optional<FacilityTitle> getFacilityTitleByEmail(String email);
    List<FacilityTitle> getFacilityTitlesByDynamicPart(String dynamicPart);
    FacilityTitle getFacilityTitleByDynamicPart(String dynamicPart) throws NoSuchElementException;
}