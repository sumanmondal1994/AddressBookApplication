package com.addressbook.unit.addressbook.service;

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

import com.addressbook.fixture.TestDataFactory;
import com.project.dto.addressbook.AddressBookIdResponse;
import com.project.dto.addressbook.AddressBookRequest;
import com.project.dto.addressbook.AddressBookResponse;
import com.project.dto.contact.ContactRequest;
import com.project.dto.response.PagedResponse;
import com.project.entity.addressbook.AddressBook;
import com.project.exception.DuplicateAddressBookException;
import com.project.exception.ResourceNotFoundException;
import com.project.mapper.contact.EntityMapper;
import com.project.repository.addressbook.AddressBookRepository;
import com.project.services.addressbook.AddressBookServiceImpl;
import com.project.services.contact.ContactCreationService;
import com.project.util.PaginationHelper;

import java.util.*;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Address Book Service Unit Tests")
public class AddressBookServiceTest {

    @Mock
    private AddressBookRepository addressBookRepository;

    @Mock
    private EntityMapper<AddressBook, AddressBookResponse> addressBookMapper;

    @Mock
    private EntityMapper<AddressBook, AddressBookIdResponse> addressBookIdMapper;

    @Mock
    private ContactCreationService contactCreationService;

    @Mock
    private PaginationHelper paginationHelper;

    private AddressBookServiceImpl addressBookService;

    private AddressBook addressBook;
    private AddressBookRequest request;
    private AddressBookResponse expectedResponse;

    private String testName;
    private String testDescription;

    @BeforeEach
    void setUp() {
        // Manually construct service with mocked dependencies
        addressBookService = new AddressBookServiceImpl(
                addressBookRepository,
                addressBookMapper,
                addressBookIdMapper,
                contactCreationService,
                paginationHelper);

        testName = TestDataFactory.generateName();
        testDescription = TestDataFactory.generateDescription();

        addressBook = AddressBook.builder()
                .id(1L)
                .name(testName)
                .description(testDescription)
                .contacts(new HashSet<>())
                .build();

        request = AddressBookRequest.builder()
                .name(testName)
                .description(testDescription)
                .build();

        expectedResponse = AddressBookResponse.builder()
                .id(1L)
                .name(testName)
                .description(testDescription)
                .contactCount(0)
                .contacts(new ArrayList<>())
                .build();
    }

    @Nested
    @DisplayName("Create Address Book Tests")
    class CreateAddressBookTests {

        @Test
        @DisplayName("Should create address book successfully")
        void testCreateAddressBook() {
            when(addressBookRepository.existsByName(testName)).thenReturn(false);
            when(addressBookRepository.save(any(AddressBook.class))).thenReturn(addressBook);
            when(addressBookMapper.mapToResponse(any(AddressBook.class))).thenReturn(expectedResponse);

            AddressBookResponse response = addressBookService.createAddressBook(request);

            assertThat(response).isNotNull();
            assertThat(response.getName()).isEqualTo(testName);
            verify(addressBookRepository, times(1)).save(any(AddressBook.class));
            verify(contactCreationService, never()).addContactsToAddressBook(any(), any());
        }

        @Test
        @DisplayName("Should throw DuplicateAddressBookException when name exists")
        void testCreateAddressBookWithDuplicateName() {
            when(addressBookRepository.existsByName(testName)).thenReturn(true);

            assertThatThrownBy(() -> addressBookService.createAddressBook(request))
                    .isInstanceOf(DuplicateAddressBookException.class)
                    .hasMessageContaining("Address book with name '" + testName + "' already exists");

            verify(addressBookRepository, never()).save(any(AddressBook.class));
        }

        @Test
        @DisplayName("Should create address book with contacts")
        void testCreateAddressBookWithContacts() {
            ContactRequest contactRequest = ContactRequest.builder()
                    .name("John Doe")
                    .phoneNumber("+61412345678")
                    .build();

            AddressBookRequest requestWithContacts = AddressBookRequest.builder()
                    .name(testName)
                    .description(testDescription)
                    .contacts(List.of(contactRequest))
                    .build();

            when(addressBookRepository.existsByName(testName)).thenReturn(false);
            when(addressBookRepository.save(any(AddressBook.class))).thenReturn(addressBook);
            when(addressBookMapper.mapToResponse(any(AddressBook.class))).thenReturn(expectedResponse);

            AddressBookResponse response = addressBookService.createAddressBookWithContacts(requestWithContacts);

            assertThat(response).isNotNull();
            verify(contactCreationService, times(1)).addContactsToAddressBook(any(AddressBook.class),
                    eq(List.of(contactRequest)));
        }

        @Test
        @DisplayName("Should not call contactCreationService when contacts list is empty")
        void testCreateAddressBookWithEmptyContacts() {
            AddressBookRequest requestWithEmptyContacts = AddressBookRequest.builder()
                    .name(testName)
                    .description(testDescription)
                    .contacts(new ArrayList<>())
                    .build();

            when(addressBookRepository.existsByName(testName)).thenReturn(false);
            when(addressBookRepository.save(any(AddressBook.class))).thenReturn(addressBook);
            when(addressBookMapper.mapToResponse(any(AddressBook.class))).thenReturn(expectedResponse);

            addressBookService.createAddressBookWithContacts(requestWithEmptyContacts);

            verify(contactCreationService, never()).addContactsToAddressBook(any(), any());
        }
    }

    @Nested
    @DisplayName("Get Address Book Tests")
    class GetAddressBookTests {

        @Test
        @DisplayName("Should get address book by id")
        void testGetAddressBookById() {
            when(addressBookRepository.findByIdWithContacts(1L)).thenReturn(Optional.of(addressBook));
            when(addressBookMapper.mapToResponse(addressBook)).thenReturn(expectedResponse);

            AddressBookResponse response = addressBookService.getAddressBookById(1L);

            assertThat(response).isNotNull();
            assertThat(response.getId()).isEqualTo(1L);
            verify(addressBookRepository, times(1)).findByIdWithContacts(1L);
        }

        @Test
        @DisplayName("Should throw exception when address book not found")
        void testGetAddressBookByIdNotFound() {
            when(addressBookRepository.findByIdWithContacts(99L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> addressBookService.getAddressBookById(99L))
                    .isInstanceOf(ResourceNotFoundException.class)
                    .hasMessageContaining("Address book not found with id: 99");
        }

        @Test
        @DisplayName("Should get address book by name")
        void testGetAddressBookByName() {
            when(addressBookRepository.findByNameWithContacts(testName)).thenReturn(Optional.of(addressBook));
            when(addressBookMapper.mapToResponse(addressBook)).thenReturn(expectedResponse);

            AddressBookResponse response = addressBookService.getAddressBookByName(testName);

            assertThat(response).isNotNull();
            assertThat(response.getName()).isEqualTo(testName);
            verify(addressBookRepository, times(1)).findByNameWithContacts(testName);
        }

        @Test
        @DisplayName("Should throw exception when address book not found by name")
        void testGetAddressBookByNameNotFound() {
            String nonExistentName = "Non Existent Book";
            when(addressBookRepository.findByNameWithContacts(nonExistentName)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> addressBookService.getAddressBookByName(nonExistentName))
                    .isInstanceOf(ResourceNotFoundException.class)
                    .hasMessageContaining("Address book not found with name " + nonExistentName);
        }

        @Test
        @DisplayName("Should search address books by partial name")
        void testSearchByName() {
            String searchTerm = "test";
            Pageable pageable = PageRequest.of(0, 10);
            Page<AddressBook> page = new PageImpl<>(Arrays.asList(addressBook), pageable, 1);
            PagedResponse<AddressBookResponse> pagedResponse = PagedResponse.<AddressBookResponse>builder()
                    .content(List.of(expectedResponse))
                    .page(0)
                    .size(10)
                    .totalElements(1)
                    .totalPages(1)
                    .first(true)
                    .last(true)
                    .empty(false)
                    .build();

            when(paginationHelper.sanitizePageable(pageable)).thenReturn(pageable);
            when(addressBookRepository.findByNameContainingIgnoreCaseWithContacts(searchTerm, pageable))
                    .thenReturn(page);
            doReturn(pagedResponse).when(paginationHelper).createPagedResponse(eq(page), any());

            PagedResponse<AddressBookResponse> response = addressBookService.searchByName(searchTerm, pageable);

            assertThat(response).isNotNull();
            assertThat(response.getContent()).hasSize(1);
            verify(addressBookRepository).findByNameContainingIgnoreCaseWithContacts(searchTerm, pageable);
        }

        @Test
        @DisplayName("Should return empty result when no address books match search")
        void testSearchByNameNoResults() {
            String searchTerm = "nonexistent";
            Pageable pageable = PageRequest.of(0, 10);
            Page<AddressBook> emptyPage = new PageImpl<>(Collections.emptyList(), pageable, 0);
            PagedResponse<AddressBookResponse> emptyPagedResponse = PagedResponse.<AddressBookResponse>builder()
                    .content(Collections.emptyList())
                    .page(0)
                    .size(10)
                    .totalElements(0)
                    .totalPages(0)
                    .first(true)
                    .last(true)
                    .empty(true)
                    .build();

            when(paginationHelper.sanitizePageable(pageable)).thenReturn(pageable);
            when(addressBookRepository.findByNameContainingIgnoreCaseWithContacts(searchTerm, pageable))
                    .thenReturn(emptyPage);
            doReturn(emptyPagedResponse).when(paginationHelper).createPagedResponse(eq(emptyPage), any());

            PagedResponse<AddressBookResponse> response = addressBookService.searchByName(searchTerm, pageable);

            assertThat(response).isNotNull();
            assertThat(response.getContent()).isEmpty();
            assertThat(response.getTotalElements()).isEqualTo(0);
        }
    }

    @Nested
    @DisplayName("Get All Address Books Tests")
    class GetAllAddressBooksTests {

        @Test
        @DisplayName("Should get all address books (non-paginated)")
        void testGetAllAddressBooks() {
            List<AddressBook> addressBooks = Arrays.asList(addressBook);
            when(addressBookRepository.findAll()).thenReturn(addressBooks);
            when(addressBookMapper.mapToResponse(any(AddressBook.class))).thenReturn(expectedResponse);

            List<AddressBookResponse> responses = addressBookService.getAllAddressBooks();

            assertThat(responses).hasSize(1);
            verify(addressBookRepository, times(1)).findAll();
        }

        @Test
        @DisplayName("Should return empty list when no address books exist")
        void testGetAllAddressBooksEmpty() {
            when(addressBookRepository.findAll()).thenReturn(Collections.emptyList());

            List<AddressBookResponse> responses = addressBookService.getAllAddressBooks();

            assertThat(responses).isEmpty();
        }

        @Test
        @DisplayName("Should get all address books paginated")
        void testGetAllAddressBooksPaginated() {
            Pageable pageable = PageRequest.of(0, 10);
            Page<AddressBook> page = new PageImpl<>(Arrays.asList(addressBook), pageable, 1);
            PagedResponse<AddressBookResponse> pagedResponse = PagedResponse.<AddressBookResponse>builder()
                    .content(List.of(expectedResponse))
                    .page(0)
                    .size(10)
                    .totalElements(1)
                    .totalPages(1)
                    .first(true)
                    .last(true)
                    .empty(false)
                    .build();

            when(paginationHelper.sanitizePageable(pageable)).thenReturn(pageable);
            when(addressBookRepository.findAllWithContacts(pageable)).thenReturn(page);
            doReturn(pagedResponse).when(paginationHelper).createPagedResponse(eq(page), any());

            PagedResponse<AddressBookResponse> response = addressBookService.getAllAddressBooks(pageable);

            assertThat(response).isNotNull();
            assertThat(response.getContent()).hasSize(1);
            assertThat(response.getTotalElements()).isEqualTo(1);
            verify(paginationHelper).sanitizePageable(pageable);
        }

        @Test
        @DisplayName("Should sanitize pageable with large page size")
        void testGetAllAddressBooksSanitizesPageable() {
            Pageable largePageable = PageRequest.of(0, 500);
            Pageable sanitizedPageable = PageRequest.of(0, 100);
            Page<AddressBook> emptyPage = new PageImpl<>(Collections.emptyList(), sanitizedPageable, 0);
            PagedResponse<AddressBookResponse> emptyPagedResponse = PagedResponse.<AddressBookResponse>builder()
                    .content(Collections.emptyList())
                    .page(0)
                    .size(100)
                    .totalElements(0)
                    .totalPages(0)
                    .first(true)
                    .last(true)
                    .empty(true)
                    .build();

            when(paginationHelper.sanitizePageable(largePageable)).thenReturn(sanitizedPageable);
            when(addressBookRepository.findAllWithContacts(sanitizedPageable)).thenReturn(emptyPage);
            doReturn(emptyPagedResponse).when(paginationHelper).createPagedResponse(eq(emptyPage), any());

            addressBookService.getAllAddressBooks(largePageable);

            verify(paginationHelper).sanitizePageable(largePageable);
            verify(addressBookRepository).findAllWithContacts(sanitizedPageable);
        }
    }

    @Nested
    @DisplayName("Update Address Book Tests")
    class UpdateAddressBookTests {

        @Test
        @DisplayName("Should update address book successfully")
        void testUpdateAddressBook() {
            String updatedName = TestDataFactory.generateName();
            String updatedDescription = TestDataFactory.generateDescription();

            AddressBookResponse updatedResponse = AddressBookResponse.builder()
                    .id(1L)
                    .name(updatedName)
                    .description(updatedDescription)
                    .contactCount(0)
                    .contacts(new ArrayList<>())
                    .build();

            when(addressBookRepository.findById(1L)).thenReturn(Optional.of(addressBook));
            when(addressBookRepository.save(any(AddressBook.class))).thenReturn(addressBook);
            when(addressBookMapper.mapToResponse(any(AddressBook.class))).thenReturn(updatedResponse);

            AddressBookRequest updateRequest = AddressBookRequest.builder()
                    .name(updatedName)
                    .description(updatedDescription)
                    .build();

            AddressBookResponse response = addressBookService.updateAddressBook(1L, updateRequest);

            assertThat(response).isNotNull();
            assertThat(response.getName()).isEqualTo(updatedName);
            verify(addressBookRepository, times(1)).save(any(AddressBook.class));
        }

        @Test
        @DisplayName("Should throw exception when updating non-existent address book")
        void testUpdateAddressBookNotFound() {
            when(addressBookRepository.findById(99L)).thenReturn(Optional.empty());

            AddressBookRequest updateRequest = AddressBookRequest.builder()
                    .name("Updated Name")
                    .build();

            assertThatThrownBy(() -> addressBookService.updateAddressBook(99L, updateRequest))
                    .isInstanceOf(ResourceNotFoundException.class)
                    .hasMessageContaining("Address book not found with id: 99");

            verify(addressBookRepository, never()).save(any(AddressBook.class));
        }
    }

    @Nested
    @DisplayName("Delete Address Book Tests")
    class DeleteAddressBookTests {

        @Test
        @DisplayName("Should delete address book successfully")
        void testDeleteAddressBook() {
            when(addressBookRepository.findById(1L)).thenReturn(Optional.of(addressBook));

            addressBookService.deleteAddressBook(1L);

            verify(addressBookRepository, times(1)).deleteById(1L);
        }

        @Test
        @DisplayName("Should throw exception when deleting non-existent address book")
        void testDeleteAddressBookNotFound() {
            when(addressBookRepository.findById(99L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> addressBookService.deleteAddressBook(99L))
                    .isInstanceOf(ResourceNotFoundException.class)
                    .hasMessageContaining("Address book not found with id: 99");

            verify(addressBookRepository, never()).deleteById(anyLong());
        }
    }

    @Nested
    @DisplayName("Get Address Book IDs Tests")
    class GetAddressBookIdsTests {

        @Test
        @DisplayName("Should get all address book IDs (non-paginated)")
        void testGetAllAddressBookIds() {
            AddressBookIdResponse idResponse = AddressBookIdResponse.builder()
                    .id(1L)
                    .name(testName)
                    .build();

            List<AddressBook> addressBooks = Arrays.asList(addressBook);
            when(addressBookRepository.findAll()).thenReturn(addressBooks);
            when(addressBookIdMapper.mapToResponse(any(AddressBook.class))).thenReturn(idResponse);

            List<AddressBookIdResponse> responses = addressBookService.getAllAddressBookIds();

            assertThat(responses).hasSize(1);
            assertThat(responses.get(0).getId()).isEqualTo(1L);
            assertThat(responses.get(0).getName()).isEqualTo(testName);
            verify(addressBookRepository, times(1)).findAll();
        }

        @Test
        @DisplayName("Should return empty list when no address books exist for IDs")
        void testGetAllAddressBookIdsEmpty() {
            when(addressBookRepository.findAll()).thenReturn(Collections.emptyList());

            List<AddressBookIdResponse> responses = addressBookService.getAllAddressBookIds();

            assertThat(responses).isEmpty();
        }

        @Test
        @DisplayName("Should get all address book IDs paginated")
        void testGetAllAddressBookIdsPaginated() {
            AddressBookIdResponse idResponse = AddressBookIdResponse.builder()
                    .id(1L)
                    .name(testName)
                    .build();

            Pageable pageable = PageRequest.of(0, 20);
            Page<AddressBook> page = new PageImpl<>(Arrays.asList(addressBook), pageable, 1);
            PagedResponse<AddressBookIdResponse> pagedResponse = PagedResponse.<AddressBookIdResponse>builder()
                    .content(List.of(idResponse))
                    .page(0)
                    .size(20)
                    .totalElements(1)
                    .totalPages(1)
                    .first(true)
                    .last(true)
                    .empty(false)
                    .build();

            when(paginationHelper.sanitizePageable(pageable)).thenReturn(pageable);
            when(addressBookRepository.findAll(pageable)).thenReturn(page);
            doReturn(pagedResponse).when(paginationHelper).createPagedResponse(eq(page), any());

            PagedResponse<AddressBookIdResponse> response = addressBookService.getAllAddressBookIds(pageable);

            assertThat(response).isNotNull();
            assertThat(response.getContent()).hasSize(1);
            assertThat(response.getContent().get(0).getId()).isEqualTo(1L);
            assertThat(response.getContent().get(0).getName()).isEqualTo(testName);
            assertThat(response.getTotalElements()).isEqualTo(1);
            verify(paginationHelper).sanitizePageable(pageable);
        }

        @Test
        @DisplayName("Should return empty paginated results when no address books exist for IDs")
        void testGetAllAddressBookIdsPaginatedEmpty() {
            Pageable pageable = PageRequest.of(0, 20);
            Page<AddressBook> emptyPage = new PageImpl<>(Collections.emptyList(), pageable, 0);
            PagedResponse<AddressBookIdResponse> emptyPagedResponse = PagedResponse.<AddressBookIdResponse>builder()
                    .content(Collections.emptyList())
                    .page(0)
                    .size(20)
                    .totalElements(0)
                    .totalPages(0)
                    .first(true)
                    .last(true)
                    .empty(true)
                    .build();

            when(paginationHelper.sanitizePageable(pageable)).thenReturn(pageable);
            when(addressBookRepository.findAll(pageable)).thenReturn(emptyPage);
            doReturn(emptyPagedResponse).when(paginationHelper).createPagedResponse(eq(emptyPage), any());

            PagedResponse<AddressBookIdResponse> response = addressBookService.getAllAddressBookIds(pageable);

            assertThat(response).isNotNull();
            assertThat(response.getContent()).isEmpty();
            assertThat(response.getTotalElements()).isEqualTo(0);
        }

        @Test
        @DisplayName("Should sanitize pageable for ID retrieval")
        void testGetAllAddressBookIdsSanitizesPageable() {
            Pageable largePageable = PageRequest.of(0, 500);
            Pageable sanitizedPageable = PageRequest.of(0, 100);
            Page<AddressBook> page = new PageImpl<>(Collections.emptyList(), sanitizedPageable, 0);
            PagedResponse<AddressBookIdResponse> pagedResponse = PagedResponse.<AddressBookIdResponse>builder()
                    .content(Collections.emptyList())
                    .page(0)
                    .size(100)
                    .totalElements(0)
                    .totalPages(0)
                    .first(true)
                    .last(true)
                    .empty(true)
                    .build();

            when(paginationHelper.sanitizePageable(largePageable)).thenReturn(sanitizedPageable);
            when(addressBookRepository.findAll(sanitizedPageable)).thenReturn(page);
            doReturn(pagedResponse).when(paginationHelper).createPagedResponse(eq(page), any());

            addressBookService.getAllAddressBookIds(largePageable);

            verify(paginationHelper).sanitizePageable(largePageable);
            verify(addressBookRepository).findAll(sanitizedPageable);
        }
    }
}
