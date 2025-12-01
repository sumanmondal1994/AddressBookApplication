package com.addressbook.project.services;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.stereotype.Service;

import com.addressbook.project.dto.ContactRequest;
import com.addressbook.project.entity.AddressBook;
import com.addressbook.project.entity.Contact;
import com.addressbook.project.exception.DuplicateContactException;

import lombok.extern.slf4j.Slf4j;


@Service
@Slf4j
public class ContactCreationServiceImpl implements ContactCreationService {

    @Override
    public void addContactsToAddressBook(AddressBook addressBook, List<ContactRequest> contacts) {
        log.info("Adding {} contacts to address book: {}", contacts.size(), addressBook.getName());

        validateNoDuplicatePhoneNumbers(contacts);

        for (ContactRequest contactRequest : contacts) {
            Contact contact = Contact.builder()
                    .name(contactRequest.getName())
                    .phoneNumber(contactRequest.getPhoneNumber())
                    .addressBook(addressBook)
                    .build();
            addressBook.getContacts().add(contact);
        }
    }

    private void validateNoDuplicatePhoneNumbers(List<ContactRequest> contacts) {
        Set<String> phoneNumbers = new HashSet<>();
        for (ContactRequest contactRequest : contacts) {
            if (!phoneNumbers.add(contactRequest.getPhoneNumber())) {
                throw new DuplicateContactException(
                        "Duplicate phone number in request: " + contactRequest.getPhoneNumber());
            }
        }
    }
}
