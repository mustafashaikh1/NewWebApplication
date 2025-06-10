package com.WebApplication.Repository;

import com.WebApplication.Entity.WebCounter;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface WebCounterRepository extends JpaRepository<WebCounter, Long> {
    List<WebCounter> findByBranchCode(String branchCode);
    Optional<WebCounter> findByCreatedByEmail(String email);
    List<WebCounter> findByWebUrlMapping_DynamicPart(String dynamicPart);
    Optional<WebCounter> findFirstByWebUrlMapping_DynamicPart(String dynamicPart);

    List<WebCounter> findAllByBranchCode(String branchCode);
}