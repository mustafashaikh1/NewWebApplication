package com.WebApplication.Repository;

import com.WebApplication.Entity.WebAboutUs;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface WebAboutUsRepository extends JpaRepository<WebAboutUs, Long> {
    @Query("SELECT w FROM WebAboutUs w WHERE w.branchCode = :branchCode")
    List<WebAboutUs> findAllByBranchCode(@Param("branchCode") String branchCode);

    @Query("SELECT w FROM WebAboutUs w WHERE w.createdByEmail = :email")
    Optional<WebAboutUs> findByCreatedByEmail(@Param("email") String email);


    List<WebAboutUs> findByWebUrlMapping_DynamicPart(String dynamicPart);
    Optional<WebAboutUs> findByWebUrlMappingDynamicPart(String dynamicPart);
}