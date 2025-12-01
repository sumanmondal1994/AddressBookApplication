package com.addressbook.project.services;

import com.addressbook.project.dto.AddressBookRequest;
import com.addressbook.project.dto.AddressBookResponse;

public interface AddressBookWriteService {

    AddressBookResponse createAddressBook(AddressBookRequest request);

    AddressBookResponse createAddressBookWithContacts(AddressBookRequest request);

    AddressBookResponse updateAddressBook(Long id, AddressBookRequest request);

    void deleteAddressBook(Long id);
}
