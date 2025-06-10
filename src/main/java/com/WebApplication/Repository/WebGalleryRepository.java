package com.WebApplication.Repository;

import com.WebApplication.Entity.WebGallery;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface WebGalleryRepository extends JpaRepository<WebGallery, Long> {
    List<WebGallery> findByBranchCode(String branchCode);

    List<WebGallery> findByWebUrlMappingDynamicPart(String dynamicPart);


    List<WebGallery> findByWebUrlMapping_DynamicPart(String dynamicPart);

    boolean existsByBranchCodeAndEventNameAndYear(String branchCode, String eventName, Integer year);


}