package com.WebApplication.Service;

import com.WebApplication.Entity.WebHRDetails;

import java.io.IOException;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

public interface WebHRDetailsService {
    WebHRDetails createWebHRDetails(WebHRDetails hrDetails, String role, String email, String dynamicPart) throws IOException;
    WebHRDetails publicCreateWebHRDetails(String hrName, String email, String contact, String dynamicPart) throws IOException;
    WebHRDetails updateWebHRDetails(Long id, WebHRDetails updated, String role, String email) throws IOException;
    void deleteWebHRDetailsById(Long id, String role, String email);
    List<WebHRDetails> getAllWebHRDetails(String role, String email, String branchCode);
    WebHRDetails getWebHRDetailsById(Long id, String role, String email);
    Optional<WebHRDetails> getWebHRDetailsByEmail(String email);
    List<WebHRDetails> getHRDetailsByDynamicPart(String dynamicPart);
    WebHRDetails getHRDetailByDynamicPart(String dynamicPart) throws NoSuchElementException;
    void updateHRDetailsColorByBranchCode(String branchCode, String color, String role, String email);
    void deleteHRDetailsColorByBranchCode(String branchCode, String role, String email);
}