package com.addressbook.project;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import com.addressbook.project.dto.ContactRequest;
import com.addressbook.project.dto.ContactResponse;
import com.addressbook.project.dto.PagedResponse;
import com.addressbook.project.entity.AddressBook;
import com.addressbook.project.entity.Contact;
import com.addressbook.project.exception.DuplicateContactException;
import com.addressbook.project.exception.ResourceNotFoundException;
import com.addressbook.project.factory.TestDataFactory;
import com.addressbook.project.mapper.EntityMapper;
import com.addressbook.project.repository.AddressBookRepository;
import com.addressbook.project.repository.ContactRepository;
import com.addressbook.project.services.ContactServiceImpl;
import com.addressbook.project.util.PaginationHelper;

import java.util.*;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Contact Service Unit Tests")
class ContactServiceTest {

        @Mock
        private ContactRepository contactRepository;

        @Mock
        private AddressBookRepository addressBookRepository;

        @Mock
        private EntityMapper<Contact, ContactResponse> contactMapper;

        @Mock
        private PaginationHelper paginationHelper;

        private ContactServiceImpl contactService;

        private AddressBook addressBook;
        private Contact contact;
        private ContactRequest request;
        private ContactResponse response;

        private String testName;
        private String testPhoneNumber;
        private String addressBookName;

        @BeforeEach
        void setUp() {
                // Manually construct service with mocked dependencies
                contactService = new ContactServiceImpl(
                                contactRepository,
                                addressBookRepository,
                                contactMapper,
                                paginationHelper);

                testName = TestDataFactory.generateName();
                testPhoneNumber = TestDataFactory.generateAustralianPhoneNumber();
                addressBookName = TestDataFactory.generateAddressBookName();

                addressBook = AddressBook.builder()
                                .id(1L)
                                .name(addressBookName)
                                .contacts(new HashSet<>())
                                .build();

                contact = Contact.builder()
                                .id(1L)
                                .name(testName)
                                .phoneNumber(testPhoneNumber)
                                .addressBook(addressBook)
                                .build();

                request = ContactRequest.builder()
                                .name(testName)
                                .phoneNumber(testPhoneNumber)
                                .build();

                response = ContactResponse.builder()
                                .id(1L)
                                .name(testName)
                                .phoneNumber(testPhoneNumber)
                                .addressBookId(1L)
                                .build();
        }

        @Nested
        @DisplayName("Add Contact Tests")
        class AddContactTests {

                @Test
                @DisplayName("Should add contact successfully")
                void testAddContact() {
                        when(addressBookRepository.findById(1L)).thenReturn(Optional.of(addressBook));
                        when(contactRepository.existsByPhoneNumberAndAddressBookId(anyString(), anyLong()))
                                        .thenReturn(false);
                        when(contactRepository.save(any(Contact.class))).thenReturn(contact);
                        when(contactMapper.mapToResponse(any(Contact.class))).thenReturn(response);

                        ContactResponse result = contactService.addContact(1L, request);

                        assertThat(result).isNotNull();
                        assertThat(result.getName()).isEqualTo(testName);
                        verify(contactRepository, times(1)).save(any(Contact.class));
                }

                @Test
                @DisplayName("Should throw DuplicateContactException when phone number already exists in same address book")
                void testAddDuplicateContactByPhoneNumber() {
                        String duplicatePhone = TestDataFactory.generateAustralianPhoneNumber();
                        ContactRequest duplicateRequest = TestDataFactory.createContactRequest(testName,
                                        duplicatePhone);

                        when(addressBookRepository.findById(1L)).thenReturn(Optional.of(addressBook));
                        when(contactRepository.existsByPhoneNumberAndAddressBookId(duplicatePhone, 1L))
                                        .thenReturn(true);

                        assertThatThrownBy(() -> contactService.addContact(1L, duplicateRequest))
                                        .isInstanceOf(DuplicateContactException.class)
                                        .hasMessageContaining("Contact with phone number " + duplicatePhone
                                                        + " already exists");

                        verify(contactRepository, never()).save(any(Contact.class));
                }

                @Test
                @DisplayName("Should throw DuplicateContactException with exact phone number message")
                void testAddDuplicateContactExactPhoneNumber() {
                        String existingPhone = "+61412345678";
                        ContactRequest duplicateRequest = ContactRequest.builder()
                                        .name("John Doe")
                                        .phoneNumber(existingPhone)
                                        .build();

                        when(addressBookRepository.findById(1L)).thenReturn(Optional.of(addressBook));
                        when(contactRepository.existsByPhoneNumberAndAddressBookId(existingPhone, 1L))
                                        .thenReturn(true);

                        assertThatThrownBy(() -> contactService.addContact(1L, duplicateRequest))
                                        .isInstanceOf(DuplicateContactException.class)
                                        .hasMessageContaining(existingPhone);

                        verify(contactRepository, never()).save(any(Contact.class));
                }

                @Test
                @DisplayName("Should throw ResourceNotFoundException when adding contact to non-existent address book")
                void testAddContactToNonExistentAddressBook() {
                        Long nonExistentId = 999L;
                        when(addressBookRepository.findById(nonExistentId)).thenReturn(Optional.empty());

                        assertThatThrownBy(() -> contactService.addContact(nonExistentId, request))
                                        .isInstanceOf(ResourceNotFoundException.class)
                                        .hasMessageContaining("Address book not found with id: " + nonExistentId);

                        verify(contactRepository, never()).save(any(Contact.class));
                }

                @Test
                @DisplayName("Should allow same phone number in different address books")
                void testAddSamePhoneNumberDifferentAddressBook() {
                        AddressBook anotherAddressBook = AddressBook.builder()
                                        .id(2L)
                                        .name("Another Book")
                                        .contacts(new HashSet<>())
                                        .build();

                        when(addressBookRepository.findById(2L)).thenReturn(Optional.of(anotherAddressBook));
                        when(contactRepository.existsByPhoneNumberAndAddressBookId(testPhoneNumber, 2L))
                                        .thenReturn(false);
                        when(contactRepository.save(any(Contact.class))).thenReturn(contact);
                        when(contactMapper.mapToResponse(any(Contact.class))).thenReturn(response);

                        ContactResponse result = contactService.addContact(2L, request);

                        assertThat(result).isNotNull();
                        verify(contactRepository, times(1)).save(any(Contact.class));
                }
        }

        @Nested
        @DisplayName("Get Contact Tests")
        class GetContactTests {

                @Test
                @DisplayName("Should get contact by id")
                void testGetContactById() {
                        when(contactRepository.findByIdAndAddressBookId(1L, 1L))
                                        .thenReturn(Optional.of(contact));
                        when(contactMapper.mapToResponse(any(Contact.class))).thenReturn(response);

                        ContactResponse result = contactService.getContactById(1L, 1L);

                        assertThat(result).isNotNull();
                        assertThat(result.getId()).isEqualTo(1L);
                }

                @Test
                @DisplayName("Should throw ResourceNotFoundException when contact not found by id")
                void testGetContactByIdNotFound() {
                        Long nonExistentContactId = 999L;
                        when(contactRepository.findByIdAndAddressBookId(nonExistentContactId, 1L))
                                        .thenReturn(Optional.empty());

                        assertThatThrownBy(() -> contactService.getContactById(1L, nonExistentContactId))
                                        .isInstanceOf(ResourceNotFoundException.class)
                                        .hasMessageContaining("Contact not found with id: " + nonExistentContactId);
                }

                @Test
                @DisplayName("Should throw ResourceNotFoundException when contact exists but in different address book")
                void testGetContactFromWrongAddressBook() {
                        when(contactRepository.findByIdAndAddressBookId(1L, 2L))
                                        .thenReturn(Optional.empty());

                        assertThatThrownBy(() -> contactService.getContactById(2L, 1L))
                                        .isInstanceOf(ResourceNotFoundException.class)
                                        .hasMessageContaining("Contact not found with id: 1");
                }
        }

        @Nested
        @DisplayName("Get All Contacts Tests")
        class GetAllContactsTests {

                @Test
                @DisplayName("Should get all contacts in address book (non-paginated)")
                void testGetAllContacts() {
                        Pageable limitedPage = PageRequest.of(0, 100);
                        Page<Contact> page = new PageImpl<>(Arrays.asList(contact), limitedPage, 1);

                        when(addressBookRepository.findById(1L)).thenReturn(Optional.of(addressBook));
                        when(paginationHelper.getMaxPageSize()).thenReturn(100);
                        when(contactRepository.findByAddressBookId(eq(1L), any(Pageable.class))).thenReturn(page);
                        when(contactMapper.mapToResponse(any(Contact.class))).thenReturn(response);

                        List<ContactResponse> results = contactService.getAllContacts(1L);

                        assertThat(results).hasSize(1);
                        verify(contactRepository, times(1)).findByAddressBookId(eq(1L), any(Pageable.class));
                }

                @Test
                @DisplayName("Should get all contacts paginated")
                void testGetAllContactsPaged() {
                        Pageable pageable = PageRequest.of(0, 10);
                        Page<Contact> page = new PageImpl<>(Arrays.asList(contact), pageable, 1);
                        PagedResponse<ContactResponse> pagedResponse = PagedResponse.<ContactResponse>builder()
                                        .content(List.of(response))
                                        .page(0)
                                        .size(10)
                                        .totalElements(1)
                                        .totalPages(1)
                                        .first(true)
                                        .last(true)
                                        .empty(false)
                                        .build();

                        when(addressBookRepository.findById(1L)).thenReturn(Optional.of(addressBook));
                        when(paginationHelper.sanitizePageable(pageable)).thenReturn(pageable);
                        when(contactRepository.findByAddressBookId(1L, pageable)).thenReturn(page);
                        doReturn(pagedResponse).when(paginationHelper).createPagedResponse(eq(page), any());

                        PagedResponse<ContactResponse> results = contactService.getAllContactsPaged(1L, pageable);

                        assertThat(results).isNotNull();
                        assertThat(results.getTotalElements()).isEqualTo(1);
                        verify(paginationHelper).sanitizePageable(pageable);
                }

                @Test
                @DisplayName("Should throw ResourceNotFoundException when getting contacts from non-existent address book")
                void testGetAllContactsFromNonExistentAddressBook() {
                        Long nonExistentId = 999L;
                        when(addressBookRepository.findById(nonExistentId)).thenReturn(Optional.empty());

                        assertThatThrownBy(() -> contactService.getAllContacts(nonExistentId))
                                        .isInstanceOf(ResourceNotFoundException.class)
                                        .hasMessageContaining("Address book not found with id: " + nonExistentId);
                }

                @Test
                @DisplayName("Should throw ResourceNotFoundException when getting paginated contacts from non-existent address book")
                void testGetAllContactsPagedFromNonExistentAddressBook() {
                        Long nonExistentId = 999L;
                        Pageable pageable = PageRequest.of(0, 10);
                        when(addressBookRepository.findById(nonExistentId)).thenReturn(Optional.empty());

                        assertThatThrownBy(() -> contactService.getAllContactsPaged(nonExistentId, pageable))
                                        .isInstanceOf(ResourceNotFoundException.class)
                                        .hasMessageContaining("Address book not found with id: " + nonExistentId);
                }

                @Test
                @DisplayName("Should return empty list when address book has no contacts")
                void testGetAllContactsEmpty() {
                        Pageable limitedPage = PageRequest.of(0, 100);
                        Page<Contact> emptyPage = new PageImpl<>(Collections.emptyList(), limitedPage, 0);

                        when(addressBookRepository.findById(1L)).thenReturn(Optional.of(addressBook));
                        when(paginationHelper.getMaxPageSize()).thenReturn(100);
                        when(contactRepository.findByAddressBookId(eq(1L), any(Pageable.class))).thenReturn(emptyPage);

                        List<ContactResponse> results = contactService.getAllContacts(1L);

                        assertThat(results).isEmpty();
                }
        }

        @Nested
        @DisplayName("Remove Contact Tests")
        class RemoveContactTests {

                @Test
                @DisplayName("Should remove contact successfully")
                void testRemoveContact() {
                        when(contactRepository.findByIdAndAddressBookId(1L, 1L))
                                        .thenReturn(Optional.of(contact));

                        contactService.removeContact(1L, 1L);

                        verify(contactRepository, times(1)).delete(contact);
                }

                @Test
                @DisplayName("Should throw ResourceNotFoundException when removing non-existent contact")
                void testRemoveContactNotFound() {
                        Long nonExistentContactId = 999L;
                        when(contactRepository.findByIdAndAddressBookId(nonExistentContactId, 1L))
                                        .thenReturn(Optional.empty());

                        assertThatThrownBy(() -> contactService.removeContact(1L, nonExistentContactId))
                                        .isInstanceOf(ResourceNotFoundException.class)
                                        .hasMessageContaining("Contact not found with id: " + nonExistentContactId);

                        verify(contactRepository, never()).delete(any(Contact.class));
                }

                @Test
                @DisplayName("Should throw ResourceNotFoundException when removing contact from wrong address book")
                void testRemoveContactFromWrongAddressBook() {
                        when(contactRepository.findByIdAndAddressBookId(1L, 2L))
                                        .thenReturn(Optional.empty());

                        assertThatThrownBy(() -> contactService.removeContact(2L, 1L))
                                        .isInstanceOf(ResourceNotFoundException.class)
                                        .hasMessageContaining("Contact not found with id: 1");

                        verify(contactRepository, never()).delete(any(Contact.class));
                }
        }

        @Nested
        @DisplayName("Unique Contacts Tests")
        class UniqueContactsTests {

                @Test
                @DisplayName("Should get unique contacts across all address books (non-paginated)")
                void testGetUniqueContactsAcrossAllAddressBooks() {
                        String secondContactName = TestDataFactory.generateName();
                        String secondContactPhone = TestDataFactory.generateAustralianPhoneNumber();

                        Contact contact2 = Contact.builder()
                                        .id(2L)
                                        .name(secondContactName)
                                        .phoneNumber(secondContactPhone)
                                        .addressBook(addressBook)
                                        .build();

                        ContactResponse response2 = ContactResponse.builder()
                                        .id(2L)
                                        .name(secondContactName)
                                        .phoneNumber(secondContactPhone)
                                        .build();

                        Pageable limitedPage = PageRequest.of(0, 100);
                        Page<Contact> page = new PageImpl<>(Arrays.asList(contact, contact2), limitedPage, 2);

                        when(paginationHelper.getMaxPageSize()).thenReturn(100);
                        when(contactRepository.findUniqueContacts(any(Pageable.class))).thenReturn(page);
                        when(contactMapper.mapToResponse(contact)).thenReturn(response);
                        when(contactMapper.mapToResponse(contact2)).thenReturn(response2);

                        List<ContactResponse> results = contactService.getUniqueContactsAcrossAllAddressBooks();

                        assertThat(results).hasSize(2);
                }

                @Test
                @DisplayName("Should get unique contacts paginated")
                void testGetUniqueContactsPaged() {
                        Pageable pageable = PageRequest.of(0, 10);
                        Page<Contact> page = new PageImpl<>(Arrays.asList(contact), pageable, 1);
                        PagedResponse<ContactResponse> pagedResponse = PagedResponse.<ContactResponse>builder()
                                        .content(List.of(response))
                                        .page(0)
                                        .size(10)
                                        .totalElements(1)
                                        .totalPages(1)
                                        .first(true)
                                        .last(true)
                                        .empty(false)
                                        .build();

                        when(paginationHelper.sanitizePageable(pageable)).thenReturn(pageable);
                        when(contactRepository.findUniqueContacts(pageable)).thenReturn(page);
                        doReturn(pagedResponse).when(paginationHelper).createPagedResponse(eq(page), any());

                        PagedResponse<ContactResponse> results = contactService.getUniqueContactsPaged(pageable);

                        assertThat(results).isNotNull();
                        verify(paginationHelper).sanitizePageable(pageable);
                }

                @Test
                @DisplayName("Should return empty list when no contacts exist")
                void testGetUniqueContactsEmpty() {
                        Pageable limitedPage = PageRequest.of(0, 100);
                        Page<Contact> emptyPage = new PageImpl<>(Collections.emptyList(), limitedPage, 0);

                        when(paginationHelper.getMaxPageSize()).thenReturn(100);
                        when(contactRepository.findUniqueContacts(any(Pageable.class))).thenReturn(emptyPage);

                        List<ContactResponse> results = contactService.getUniqueContactsAcrossAllAddressBooks();

                        assertThat(results).isEmpty();
                }

                @Test
                @DisplayName("Should get contact count for address book")
                void testGetContactCount() {
                        when(addressBookRepository.findById(1L)).thenReturn(Optional.of(addressBook));
                        when(contactRepository.countByAddressBookId(1L)).thenReturn(5L);

                        long count = contactService.getContactCount(1L);

                        assertThat(count).isEqualTo(5L);
                        verify(contactRepository).countByAddressBookId(1L);
                }

                @Test
                @DisplayName("Should get unique contact count")
                void testGetUniqueContactCount() {
                        when(contactRepository.countDistinctPhoneNumbers()).thenReturn(10L);

                        long count = contactService.getUniqueContactCount();

                        assertThat(count).isEqualTo(10L);
                        verify(contactRepository).countDistinctPhoneNumbers();
                }
        }
}