package com.WebApplication.Repository;

import com.WebApplication.Entity.WebVisionMission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface WebVisionMissionRepository extends JpaRepository<WebVisionMission, Long> {
    List<WebVisionMission> findAllByBranchCode(String branchCode);

    Optional<WebVisionMission> findByCreatedByEmail(String email);

    Optional<WebVisionMission> findByWebUrlMappingDynamicPart(String dynamicPart);

    List<WebVisionMission> findByWebUrlMapping_DynamicPart(String dynamicPart);
}