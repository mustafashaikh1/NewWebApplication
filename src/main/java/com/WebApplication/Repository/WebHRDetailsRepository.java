package com.WebApplication.Repository;

import com.WebApplication.Entity.WebHRDetails;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface WebHRDetailsRepository extends JpaRepository<WebHRDetails, Long> {
    // Replace this method:
    // Optional<WebHRDetails> findByWebJobCareerOption_Id(Long id);

    // With this one that uses the correct field name:
    Optional<WebHRDetails> findByWebJobCareerOption_WebJobCareerOptionId(Long webJobCareerOptionId);

    // Other methods you likely have based on the implementations seen:
    List<WebHRDetails> findAllByBranchCode(String branchCode);
    Optional<WebHRDetails> findByCreatedByEmail(String email);
    List<WebHRDetails> findByWebUrlMapping_DynamicPart(String dynamicPart);
    Optional<WebHRDetails> findFirstByWebUrlMapping_DynamicPart(String dynamicPart);
}