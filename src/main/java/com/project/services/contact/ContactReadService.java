package com.project.services.contact;

import java.util.List;

import org.springframework.data.domain.Pageable;

import com.project.dto.contact.ContactResponse;
import com.project.dto.response.PagedResponse;

public interface ContactReadService {

    ContactResponse getContactById(Long addressBookId, Long contactId);

    List<ContactResponse> getAllContacts(Long addressBookId);

    PagedResponse<ContactResponse> getAllContactsPaged(Long addressBookId, Pageable pageable);

    List<ContactResponse> getUniqueContactsAcrossAllAddressBooks();

    PagedResponse<ContactResponse> getUniqueContactsPaged(Pageable pageable);

    long getContactCount(Long addressBookId);

    long getUniqueContactCount();
}
