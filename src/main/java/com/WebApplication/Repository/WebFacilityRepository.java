package com.WebApplication.Repository;

import com.WebApplication.Entity.WebFacility;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
public interface WebFacilityRepository extends JpaRepository<WebFacility, Long> {
    List<WebFacility> findAllByBranchCode(String branchCode);
    Optional<WebFacility> findByCreatedByEmail(String email);

    // Find facilities by dynamic part
    List<WebFacility> findByWebUrlMapping_DynamicPart(String dynamicPart);

    @Modifying
    @Transactional
    @Query("UPDATE WebFacility w SET w.facilityColor = ?2 WHERE w.branchCode = ?1 AND w.facilityColor IS NULL")
    void addFacilityColorByBranchCode(String branchCode, String facilityColor);

    @Modifying
    @Transactional
    @Query("UPDATE WebFacility w SET w.facilityColor = ?2 WHERE w.branchCode = ?1")
    void updateFacilityColorByBranchCode(String branchCode, String facilityColor);

    @Modifying
    @Transactional
    @Query("UPDATE WebFacility w SET w.facilityColor = NULL WHERE w.branchCode = ?1")
    void deleteFacilityColorByBranchCode(String branchCode);




    Optional<WebFacility> findFirstByWebUrlMapping_DynamicPart(String dynamicPart);

}