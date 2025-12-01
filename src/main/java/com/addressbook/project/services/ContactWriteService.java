package com.addressbook.project.services;

import java.util.List;

import com.addressbook.project.dto.ContactRequest;
import com.addressbook.project.dto.ContactResponse;

public interface ContactWriteService {

    ContactResponse addContact(Long addressBookId, ContactRequest request);

    ContactResponse updateContact(Long addressBookId, Long contactId, ContactRequest request);

    void removeContact(Long addressBookId, Long contactId);

    int removeContacts(Long addressBookId, List<Long> contactIds);

    int removeAllContacts(Long addressBookId);
}
