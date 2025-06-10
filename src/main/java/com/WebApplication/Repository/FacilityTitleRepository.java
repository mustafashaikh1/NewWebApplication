package com.WebApplication.Repository;

import com.WebApplication.Entity.FacilityTitle;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FacilityTitleRepository extends JpaRepository<FacilityTitle, Long> {
    List<FacilityTitle> findByBranchCode(String branchCode);
    Optional<FacilityTitle> findByCreatedByEmail(String email);
    List<FacilityTitle> findByWebUrlMapping_DynamicPart(String dynamicPart);
    Optional<FacilityTitle> findFirstByWebUrlMapping_DynamicPart(String dynamicPart);
    Optional<FacilityTitle> findByFacilityTitleAndBranchCode(String facilityTitle, String branchCode);
}