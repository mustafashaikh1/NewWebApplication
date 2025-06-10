package com.WebApplication.Repository;

import com.WebApplication.Entity.WebTopper;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface WebTopperRepository extends JpaRepository<WebTopper, Long> {

    List<WebTopper> findAllByBranchCode(String branchCode);

    Optional<WebTopper> findByCreatedByEmail(String email);

    List<WebTopper> findByWebUrlMapping_DynamicPart(String dynamicPart);

    Optional<WebTopper> findFirstByWebUrlMapping_DynamicPart(String dynamicPart);
}