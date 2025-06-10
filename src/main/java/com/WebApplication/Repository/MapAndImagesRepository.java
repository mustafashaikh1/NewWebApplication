package com.WebApplication.Repository;

import com.WebApplication.Entity.WebMapAndImages;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MapAndImagesRepository extends JpaRepository<WebMapAndImages, Long> {
    Optional<WebMapAndImages> findByWebUrlMappingDynamicPart(String dynamicPart);
    List<WebMapAndImages> findByWebUrlMapping_DynamicPart(String dynamicPart);
    Optional<WebMapAndImages> findByCreatedByEmail(String email);
    List<WebMapAndImages> findAllByBranchCode(String branchCode);
    boolean existsByWebUrlMappingDynamicPart(String dynamicPart);
}