package com.WebApplication.Repository;

import com.WebApplication.Entity.WebManuBar;
import com.WebApplication.Entity.WebUrlMapping;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface WebManuBarRepository extends JpaRepository<WebManuBar, Long> {
    @Query("SELECT m FROM WebManuBar m WHERE m.branchCode = :branchCode")
    List<WebManuBar> findAllByBranchCode(@Param("branchCode") String branchCode);

    @Query("SELECT m FROM WebManuBar m WHERE m.createdByEmail = :email")
    Optional<WebManuBar> findByCreatedByEmail(@Param("email") String email);

    List<WebManuBar> findByWebUrlMapping_DynamicPart(String dynamicPart);
    Optional<WebManuBar> findByWebUrlMapping(WebUrlMapping webUrlMapping);
    Optional<WebManuBar> findByWebUrlMappingDynamicPart(String dynamicPart);


}