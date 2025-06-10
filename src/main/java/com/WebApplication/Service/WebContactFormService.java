package com.WebApplication.Service;

import com.WebApplication.Entity.ContactForm;

import java.util.List;

public interface WebContactFormService {

    ContactForm createContactForm(ContactForm contactForm, String role, String createdByEmail, String dynamicPart);
    ContactForm publicCreateContactForm(ContactForm contactForm, String dynamicPart);
    ContactForm updateContactForm(Long id, ContactForm contactForm, String role, String createdByEmail);
    void deleteContactForm(Long id, String role, String email);
    ContactForm getContactFormById(Long id, String role, String email);
    List<ContactForm> getAllContactForms(String role, String createdByEmail, String branchCode);
    List<ContactForm> getContactFormsByEmail(String email);
    List<ContactForm> getContactFormsByDynamicPart(String dynamicPart);
    ContactForm getUserContactFormByDynamicPart(String dynamicPart);
}
