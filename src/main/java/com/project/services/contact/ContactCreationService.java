package com.project.services.contact;

import java.util.List;

import com.project.dto.contact.ContactRequest;
import com.project.entity.addressbook.AddressBook;

public interface ContactCreationService {

    /**
     * Validates and adds contacts to an address book entity.
     * 
     * @param addressBook the address book to add contacts to
     * @param contacts    list of contact requests to add
     */
    void addContactsToAddressBook(AddressBook addressBook, List<ContactRequest> contacts);
}
