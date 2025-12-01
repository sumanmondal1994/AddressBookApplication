package com.project.services.addressbook;

import com.project.dto.addressbook.AddressBookRequest;
import com.project.dto.addressbook.AddressBookResponse;

public interface AddressBookWriteService {

    AddressBookResponse createAddressBook(AddressBookRequest request);

    AddressBookResponse createAddressBookWithContacts(AddressBookRequest request);

    AddressBookResponse updateAddressBook(Long id, AddressBookRequest request);

    void deleteAddressBook(Long id);
}
