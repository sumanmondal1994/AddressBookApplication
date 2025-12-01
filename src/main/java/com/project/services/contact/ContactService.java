package com.project.services.contact;

import java.util.List;

import org.springframework.data.domain.Pageable;

import com.project.dto.contact.ContactResponse;
import com.project.dto.response.PagedResponse;


public interface ContactService extends ContactReadService, ContactWriteService {

     List<ContactResponse> getAllContacts(Long addressBookId);

     PagedResponse<ContactResponse> getAllContactsPaged(Long addressBookId, Pageable pageable);
}