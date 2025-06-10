package com.WebApplication.Repository;

import com.WebApplication.Entity.WebFooter;
import com.WebApplication.Entity.WebUrlMapping;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface WebFooterRepository extends JpaRepository<WebFooter, Long> {
    Optional<WebFooter> findByWebUrlMappingDynamicPart(String dynamicPart);
    List<WebFooter> findByWebUrlMapping_DynamicPart(String dynamicPart);
    Optional<WebFooter> findByWebUrlMapping(WebUrlMapping webUrlMapping);
    boolean existsByWebUrlMappingDynamicPart(String dynamicPart);
    Optional<WebFooter> findByBranchCode(String branchCode);
    Optional<WebFooter> findByCreatedByEmail(String email);

    List<WebFooter> findAllByBranchCode(String branchCode);
}