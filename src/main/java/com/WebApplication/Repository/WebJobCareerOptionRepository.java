package com.WebApplication.Repository;

import com.WebApplication.Entity.WebJobCareerOption;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface WebJobCareerOptionRepository extends JpaRepository<WebJobCareerOption, Long> {
    List<WebJobCareerOption> findAllByBranchCode(String branchCode);
    Optional<WebJobCareerOption> findByCreatedByEmail(String email);
    List<WebJobCareerOption> findByWebUrlMapping_DynamicPart(String dynamicPart);
    Optional<WebJobCareerOption> findFirstByWebUrlMapping_DynamicPart(String dynamicPart);
}