package com.project.services.contact;

import java.util.List;

import com.project.dto.contact.ContactRequest;
import com.project.dto.contact.ContactResponse;

public interface ContactWriteService {

    ContactResponse addContact(Long addressBookId, ContactRequest request);

    ContactResponse updateContact(Long addressBookId, Long contactId, ContactRequest request);

    void removeContact(Long addressBookId, Long contactId);

    int removeContacts(Long addressBookId, List<Long> contactIds);

    int removeAllContacts(Long addressBookId);
}
