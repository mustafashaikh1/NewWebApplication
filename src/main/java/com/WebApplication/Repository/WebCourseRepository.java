package com.WebApplication.Repository;

import com.WebApplication.Entity.WebCourse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface WebCourseRepository extends JpaRepository<WebCourse, Long> {
    List<WebCourse> findByBranchCode(String branchCode);

    @Query("SELECT w.courseColor FROM WebCourse w WHERE w.branchCode = ?1")
    List<String> findWebCourseColorByBranchCode(String branchCode);

    @Query("SELECT w FROM WebCourse w WHERE w.branchCode = ?1 AND w.courseColor = ?2")
    List<WebCourse> findByBranchCodeAndCourseColor(String branchCode, String courseColor);

    @Modifying
    @Query("UPDATE WebCourse w SET w.courseColor = ?2 WHERE w.branchCode = ?1")
    void updateWebCourseColorByBranchCode(String branchCode, String courseColor);

    @Modifying
    @Query("UPDATE WebCourse w SET w.courseColor = ?2 WHERE w.branchCode = ?1 AND w.courseColor IS NULL")
    void addWebCourseColorByBranchCode(String branchCode, String courseColor);

    @Modifying
    @Query("UPDATE WebCourse w SET w.courseColor = NULL WHERE w.branchCode = ?1")
    void deleteWebCourseColorByBranchCode(String branchCode);

    Optional<WebCourse> findByCreatedByEmail(String email);
    List<WebCourse> findByWebUrlMapping_DynamicPart(String dynamicPart);
    Optional<WebCourse> findByWebUrlMappingDynamicPart(String dynamicPart);
}