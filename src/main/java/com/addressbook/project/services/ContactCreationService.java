package com.addressbook.project.services;

import java.util.List;

import com.addressbook.project.dto.ContactRequest;
import com.addressbook.project.entity.AddressBook;

public interface ContactCreationService {

    /**
     * Validates and adds contacts to an address book entity.
     * 
     * @param addressBook the address book to add contacts to
     * @param contacts    list of contact requests to add
     */
    void addContactsToAddressBook(AddressBook addressBook, List<ContactRequest> contacts);
}
