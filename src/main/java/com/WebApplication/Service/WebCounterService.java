package com.WebApplication.Service;

import com.WebApplication.Entity.WebCounter;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

public interface WebCounterService {
    WebCounter createWebCounter(WebCounter counter, String role, String email, String dynamicPart);
    WebCounter publicCreateWebCounter(String counterName1, int countValue1, String counterColor1,
                                      String counterName2, int countValue2, String counterColor2,
                                      String counterName3, int countValue3, String counterColor3,
                                      String dynamicPart);
    WebCounter updateWebCounter(Long id, WebCounter updated, String role, String email);
    void deleteWebCounterById(Long id, String role, String email);
    List<WebCounter> getAllWebCounters(String role, String email, String branchCode);
    WebCounter getWebCounterById(Long id, String role, String email);
    Optional<WebCounter> getWebCounterByEmail(String email);
    List<WebCounter> getCountersByDynamicPart(String dynamicPart);
    WebCounter getCounterByDynamicPart(String dynamicPart) throws NoSuchElementException;
}