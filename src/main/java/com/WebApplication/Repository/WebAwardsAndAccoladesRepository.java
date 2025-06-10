package com.WebApplication.Repository;

import com.WebApplication.Entity.WebAwardsAndAccolades;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface WebAwardsAndAccoladesRepository extends JpaRepository<WebAwardsAndAccolades, Long> {
    List<WebAwardsAndAccolades> findByCreatedByEmail(String email);
    List<WebAwardsAndAccolades> findAllByBranchCode(String branchCode);
    List<WebAwardsAndAccolades> findByWebUrlMapping_DynamicPart(String dynamicPart);
    Optional<WebAwardsAndAccolades> findFirstByWebUrlMapping_DynamicPart(String dynamicPart);
}