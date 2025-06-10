package com.WebApplication.Repository;

import com.WebApplication.Entity.WebTestimonials;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface WebTestimonialsRepository extends JpaRepository<WebTestimonials, Long> {

    List<WebTestimonials> findAllByBranchCode(String branchCode);

    Optional<WebTestimonials> findByCreatedByEmail(String email);

    List<WebTestimonials> findByWebUrlMapping_DynamicPart(String dynamicPart);

    Optional<WebTestimonials> findFirstByWebUrlMapping_DynamicPart(String dynamicPart);

    @Modifying
    @Transactional
    @Query("UPDATE WebTestimonials w SET w.testimonialColor = ?2 WHERE w.branchCode = ?1")
    void updateTestimonialColorByBranchCode(String branchCode, String testimonialColor);

    @Modifying
    @Transactional
    @Query("UPDATE WebTestimonials w SET w.testimonialColor = ?2 WHERE w.branchCode = ?1")
    void addTestimonialColorByBranchCode(String branchCode, String testimonialColor);

    @Modifying
    @Transactional
    @Query("UPDATE WebTestimonials w SET w.testimonialColor = NULL WHERE w.branchCode = ?1")
    void deleteTestimonialColorByBranchCode(String branchCode);
}
