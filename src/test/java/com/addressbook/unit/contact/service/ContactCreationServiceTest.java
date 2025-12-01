package com.addressbook.unit.contact.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import com.project.dto.contact.ContactRequest;
import com.project.entity.addressbook.AddressBook;
import com.project.exception.DuplicateContactException;
import com.project.services.contact.ContactCreationServiceImpl;
import com.addressbook.fixture.TestDataFactory;

import java.util.*;
import static org.assertj.core.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Contact Creation Service Unit Tests")
class ContactCreationServiceTest {

    private ContactCreationServiceImpl contactCreationService;

    private AddressBook addressBook;

    @BeforeEach
    void setUp() {
        contactCreationService = new ContactCreationServiceImpl();

        addressBook = AddressBook.builder()
                .id(1L)
                .name(TestDataFactory.generateAddressBookName())
                .contacts(new HashSet<>())
                .build();
    }

    @Nested
    @DisplayName("Add Contacts To Address Book Tests")
    class AddContactsToAddressBookTests {

        @Test
        @DisplayName("Should add single contact to address book")
        void testAddSingleContact() {
            ContactRequest contactRequest = ContactRequest.builder()
                    .name(TestDataFactory.generateName())
                    .phoneNumber(TestDataFactory.generateAustralianPhoneNumber())
                    .build();

            contactCreationService.addContactsToAddressBook(addressBook, List.of(contactRequest));

            assertThat(addressBook.getContacts()).hasSize(1);
            assertThat(addressBook.getContacts().iterator().next().getName())
                    .isEqualTo(contactRequest.getName());
        }

        @Test
        @DisplayName("Should add multiple contacts to address book")
        void testAddMultipleContacts() {
            ContactRequest contact1 = ContactRequest.builder()
                    .name(TestDataFactory.generateName())
                    .phoneNumber(TestDataFactory.generateAustralianPhoneNumber())
                    .build();

            ContactRequest contact2 = ContactRequest.builder()
                    .name(TestDataFactory.generateName())
                    .phoneNumber(TestDataFactory.generateAustralianPhoneNumber())
                    .build();

            ContactRequest contact3 = ContactRequest.builder()
                    .name(TestDataFactory.generateName())
                    .phoneNumber(TestDataFactory.generateAustralianPhoneNumber())
                    .build();

            contactCreationService.addContactsToAddressBook(addressBook, List.of(contact1, contact2, contact3));

            assertThat(addressBook.getContacts()).hasSize(3);
        }

        @Test
        @DisplayName("Should throw DuplicateContactException when contacts have duplicate phone numbers")
        void testAddContactsWithDuplicatePhoneNumbers() {
            String duplicatePhone = "+61412345678";

            ContactRequest contact1 = ContactRequest.builder()
                    .name("John Doe")
                    .phoneNumber(duplicatePhone)
                    .build();

            ContactRequest contact2 = ContactRequest.builder()
                    .name("Jane Doe")
                    .phoneNumber(duplicatePhone)
                    .build();

            assertThatThrownBy(() -> contactCreationService.addContactsToAddressBook(
                    addressBook, List.of(contact1, contact2)))
                    .isInstanceOf(DuplicateContactException.class)
                    .hasMessageContaining("Duplicate phone number in request: " + duplicatePhone);

            assertThat(addressBook.getContacts()).isEmpty();
        }

        @Test
        @DisplayName("Should add empty list without error")
        void testAddEmptyContactsList() {
            contactCreationService.addContactsToAddressBook(addressBook, Collections.emptyList());

            assertThat(addressBook.getContacts()).isEmpty();
        }

        @Test
        @DisplayName("Should set correct address book reference on each contact")
        void testContactsHaveCorrectAddressBookReference() {
            ContactRequest contactRequest = ContactRequest.builder()
                    .name(TestDataFactory.generateName())
                    .phoneNumber(TestDataFactory.generateAustralianPhoneNumber())
                    .build();

            contactCreationService.addContactsToAddressBook(addressBook, List.of(contactRequest));

            assertThat(addressBook.getContacts())
                    .allSatisfy(contact -> assertThat(contact.getAddressBook()).isEqualTo(addressBook));
        }

        @Test
        @DisplayName("Should detect duplicate at third position in list")
        void testDetectsDuplicateInMiddleOfList() {
            String duplicatePhone = "+61400000000";

            ContactRequest contact1 = ContactRequest.builder()
                    .name("Person 1")
                    .phoneNumber(duplicatePhone)
                    .build();

            ContactRequest contact2 = ContactRequest.builder()
                    .name("Person 2")
                    .phoneNumber("+61411111111")
                    .build();

            ContactRequest contact3 = ContactRequest.builder()
                    .name("Person 3")
                    .phoneNumber(duplicatePhone)
                    .build();

            assertThatThrownBy(() -> contactCreationService.addContactsToAddressBook(
                    addressBook, List.of(contact1, contact2, contact3)))
                    .isInstanceOf(DuplicateContactException.class)
                    .hasMessageContaining(duplicatePhone);
        }
    }
}
