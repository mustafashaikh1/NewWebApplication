package com.WebApplication.Repository;

import com.WebApplication.Entity.WebSlideBar;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface WebSlideBarRepository extends JpaRepository<WebSlideBar, Long> {

    @Query("SELECT s FROM WebSlideBar s WHERE s.branchCode = :branchCode")
    List<WebSlideBar> findAllByBranchCode(@Param("branchCode") String branchCode);

    @Query("SELECT s FROM WebSlideBar s WHERE s.createdByEmail = :email")
    Optional<WebSlideBar> findByCreatedByEmail(@Param("email") String email);

//    List<WebSlideBar> findByCreatedByEmail(String email);
//    List<WebSlideBar> findAllByBranchCode(String branchCode);
    List<WebSlideBar> findByWebUrlMapping_DynamicPart(String dynamicPart);
    Optional<WebSlideBar> findByWebUrlMappingDynamicPart(String dynamicPart);
}
