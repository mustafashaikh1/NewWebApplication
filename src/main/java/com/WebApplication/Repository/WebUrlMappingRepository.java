package com.WebApplication.Repository;

import com.WebApplication.Entity.WebUrlMapping;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface WebUrlMappingRepository extends JpaRepository<WebUrlMapping, Long> {

    Optional<WebUrlMapping> findByDynamicPart(String dynamicPart);

    List<WebUrlMapping> findAllByBranchCode(String branchCode);

    Optional<WebUrlMapping> findByCreatedByEmail(String email);


}
