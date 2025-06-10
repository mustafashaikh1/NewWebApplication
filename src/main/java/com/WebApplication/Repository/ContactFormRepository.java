package com.WebApplication.Repository;

import com.WebApplication.Entity.ContactForm;
import com.WebApplication.Entity.WebUrlMapping;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ContactFormRepository extends JpaRepository<ContactForm, Long> {

    List<ContactForm> findByBranchCode(String branchCode);

    List<ContactForm> findByContactEmail(String contactEmail);

    List<ContactForm> findByWebUrlMapping(WebUrlMapping webUrlMapping);

    List<ContactForm> findByWebUrlMapping_DynamicPart(String dynamicPart);

    @Query("SELECT c FROM ContactForm c WHERE c.webUrlMapping.dynamicPart = :dynamicPart ORDER BY c.contactId DESC")
    Optional<ContactForm> findByWebUrlMappingDynamicPart(String dynamicPart);

    List<ContactForm> findByCreatedByEmail(String createdByEmail);

    List<ContactForm> findAllByBranchCode(String branchCode);

    Optional<Object> findFirstByWebUrlMapping_DynamicPart(String dynamicPart);
}