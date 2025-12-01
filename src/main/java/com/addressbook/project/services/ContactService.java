package com.addressbook.project.services;

import java.util.List;

import org.springframework.data.domain.Pageable;

import com.addressbook.project.dto.ContactResponse;
import com.addressbook.project.dto.PagedResponse;


public interface ContactService extends ContactReadService, ContactWriteService {

     List<ContactResponse> getAllContacts(Long addressBookId);

     PagedResponse<ContactResponse> getAllContactsPaged(Long addressBookId, Pageable pageable);
}