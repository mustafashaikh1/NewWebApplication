package com.WebApplication.Service;

import com.WebApplication.Entity.WebAwardsAndAccolades;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface WebAwardsAndAccoladesService {
    WebAwardsAndAccolades createAward(WebAwardsAndAccolades award, MultipartFile awardImage,
                                      String role, String email, String dynamicPart) throws IOException;

    WebAwardsAndAccolades publicCreateAward(String awardName, String description, String awardedBy,
                                            String awardTo, int year, MultipartFile awardImage,
                                            String dynamicPart) throws IOException;

    WebAwardsAndAccolades updateAward(Long id, WebAwardsAndAccolades award,
                                      MultipartFile awardImage, String role, String email) throws IOException;

    void deleteAward(Long id, String role, String email);

    WebAwardsAndAccolades getAwardById(Long id, String role, String email);

    List<WebAwardsAndAccolades> getAllAwards(String role, String email, String branchCode);

    List<WebAwardsAndAccolades> getAwardsByEmail(String email);

    List<WebAwardsAndAccolades> getAwardsByDynamicPart(String dynamicPart);

    WebAwardsAndAccolades getUserAwardByDynamicPart(String dynamicPart);
}