package com.addressbook.project.integration;

import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import com.addressbook.project.dto.AddressBookRequest;
import com.addressbook.project.dto.AddressBookResponse;
import com.addressbook.project.dto.ApiResponse;
import com.addressbook.project.dto.ContactRequest;
import com.addressbook.project.dto.ContactResponse;
import com.addressbook.project.dto.PagedResponse;
import com.addressbook.project.factory.TestDataFactory;
import com.addressbook.project.repository.AddressBookRepository;
import com.addressbook.project.repository.ContactRepository;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@DisplayName("Address Book Integration Tests")
class AddressBookIntegrationTest {

        @Autowired
        private MockMvc mockMvc;

        @Autowired
        private ObjectMapper objectMapper;

        @Autowired
        private AddressBookRepository addressBookRepository;

        @Autowired
        private ContactRepository contactRepository;

        @BeforeEach
        void setUp() {
                contactRepository.deleteAll();
                addressBookRepository.deleteAll();
        }

        @Test
        @Order(1)
        @DisplayName("Should create a new address book")
        void testCreateAddressBook() throws Exception {
                String name = TestDataFactory.generateAddressBookName();
                String description = TestDataFactory.generateDescription();

                AddressBookRequest request = TestDataFactory.createAddressBookRequest(name, description);

                String responseJson = mockMvc.perform(post("/api/v1/addressbooks")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request)))
                                .andExpect(status().isCreated())
                                .andReturn()
                                .getResponse()
                                .getContentAsString();

                ApiResponse<AddressBookResponse> apiResponse = objectMapper.readValue(responseJson,
                                new TypeReference<ApiResponse<AddressBookResponse>>() {
                                });

                assertThat(apiResponse.isSuccess()).isTrue();
                AddressBookResponse response = apiResponse.getResponse();

                assertThat(response.getId()).isNotNull();
                assertThat(response.getName()).isEqualTo(name);
                assertThat(response.getDescription()).isEqualTo(description);
                assertThat(response.getContactCount()).isEqualTo(0);
                assertThat(response.getContacts()).isEmpty();
                assertThat(response.getCreatedAt()).isNotNull();
                assertThat(response.getUpdatedAt()).isNotNull();
        }

        @Test
        @Order(2)
        @DisplayName("Should get all address books")
        void testGetAllAddressBooks() throws Exception {
                String name1 = TestDataFactory.generateAddressBookName();
                String name2 = TestDataFactory.generateAddressBookName();

                createTestAddressBook(name1);
                createTestAddressBook(name2);

                String responseJson = mockMvc.perform(get("/api/v1/addressbooks"))
                                .andExpect(status().isOk())
                                .andReturn()
                                .getResponse()
                                .getContentAsString();

                ApiResponse<PagedResponse<AddressBookResponse>> apiResponse = objectMapper.readValue(responseJson,
                                new TypeReference<ApiResponse<PagedResponse<AddressBookResponse>>>() {
                                });

                assertThat(apiResponse.isSuccess()).isTrue();
                PagedResponse<AddressBookResponse> pagedResponse = apiResponse.getResponse();

                assertThat(pagedResponse.getContent()).hasSize(2);
                assertThat(pagedResponse.getTotalElements()).isEqualTo(2);
                assertThat(pagedResponse.isFirst()).isTrue();
                assertThat(pagedResponse.isLast()).isTrue();
                assertThat(pagedResponse.getContent()).extracting(AddressBookResponse::getName)
                                .containsExactlyInAnyOrder(name1, name2);
        }

        @Test
        @Order(3)
        @DisplayName("Should add contact to address book")
        void testAddContact() throws Exception {
                Long addressBookId = createTestAddressBook(TestDataFactory.generateAddressBookName());

                String contactName = TestDataFactory.generateName();
                String contactPhone = TestDataFactory.generateAustralianPhoneNumber();

                ContactRequest request = TestDataFactory.createContactRequest(contactName, contactPhone);

                String responseJson = mockMvc.perform(post("/api/v1/addressbooks/" + addressBookId + "/contacts")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request)))
                                .andExpect(status().isCreated())
                                .andReturn()
                                .getResponse()
                                .getContentAsString();

                ApiResponse<ContactResponse> apiResponse = objectMapper.readValue(responseJson,
                                new TypeReference<ApiResponse<ContactResponse>>() {
                                });

                assertThat(apiResponse.isSuccess()).isTrue();
                ContactResponse response = apiResponse.getResponse();

                assertThat(response.getId()).isNotNull();
                assertThat(response.getName()).isEqualTo(contactName);
                assertThat(response.getPhoneNumber()).isEqualTo(contactPhone);
                assertThat(response.getCreatedAt()).isNotNull();

        }

        @Test
        @Order(4)
        @DisplayName("Should prevent duplicate contacts in same address book")
        void testPreventDuplicateContacts() throws Exception {
                Long addressBookId = createTestAddressBook(TestDataFactory.generateAddressBookName());

                String contactName = TestDataFactory.generateName();
                String contactPhone = TestDataFactory.generateAustralianPhoneNumber();

                ContactRequest request = TestDataFactory.createContactRequest(contactName, contactPhone);

                String firstResponseJson = mockMvc.perform(post("/api/v1/addressbooks/" + addressBookId + "/contacts")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request)))
                                .andExpect(status().isCreated())
                                .andReturn()
                                .getResponse()
                                .getContentAsString();

                ApiResponse<ContactResponse> apiResponse = objectMapper.readValue(firstResponseJson,
                                new TypeReference<ApiResponse<ContactResponse>>() {
                                });

                assertThat(apiResponse.isSuccess()).isTrue();
                ContactResponse firstResponse = apiResponse.getResponse();
                assertThat(firstResponse.getId()).isNotNull();
                assertThat(firstResponse.getName()).isEqualTo(contactName);

                mockMvc.perform(post("/api/v1/addressbooks/" + addressBookId + "/contacts")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request)))
                                .andExpect(status().isConflict());
        }

        @Test
        @Order(5)
        @DisplayName("Should print all contacts in address book")
        void testPrintAllContactsInAddressBook() throws Exception {
                Long addressBookId = createTestAddressBook(TestDataFactory.generateAddressBookName());

                String name1 = TestDataFactory.generateFirstName();
                String phone1 = TestDataFactory.generateAustralianPhoneNumber();
                String name2 = TestDataFactory.generateFirstName();
                String phone2 = TestDataFactory.generateAustralianPhoneNumber();

                addTestContact(addressBookId, name1, phone1);
                addTestContact(addressBookId, name2, phone2);

                String responseJson = mockMvc.perform(get("/api/v1/addressbooks/" + addressBookId + "/contacts"))
                                .andExpect(status().isOk())
                                .andReturn()
                                .getResponse()
                                .getContentAsString();

                ApiResponse<PagedResponse<ContactResponse>> apiResponse = objectMapper.readValue(responseJson,
                                new TypeReference<ApiResponse<PagedResponse<ContactResponse>>>() {
                                });

                assertThat(apiResponse.isSuccess()).isTrue();
                PagedResponse<ContactResponse> pagedContacts = apiResponse.getResponse();

                assertThat(pagedContacts.getContent()).hasSize(2);
                assertThat(pagedContacts.getContent()).extracting(ContactResponse::getName)
                                .containsExactlyInAnyOrder(name1, name2);
                assertThat(pagedContacts.getContent()).extracting(ContactResponse::getPhoneNumber)
                                .containsExactlyInAnyOrder(phone1, phone2);
        }

        @Test
        @Order(6)
        @DisplayName("Should remove contact from address book")
        void testRemoveContact() throws Exception {
                Long addressBookId = createTestAddressBook(TestDataFactory.generateAddressBookName());
                Long contactId = addTestContact(addressBookId, TestDataFactory.generateName(),
                                TestDataFactory.generateAustralianPhoneNumber());

                mockMvc.perform(delete("/api/v1/addressbooks/" + addressBookId +
                                "/contacts/" + contactId))
                                .andExpect(status().isOk());

                String responseJson = mockMvc.perform(get("/api/v1/addressbooks/" + addressBookId + "/contacts"))
                                .andExpect(status().isOk())
                                .andReturn()
                                .getResponse()
                                .getContentAsString();

                ApiResponse<PagedResponse<ContactResponse>> apiResponse = objectMapper.readValue(responseJson,
                                new TypeReference<ApiResponse<PagedResponse<ContactResponse>>>() {
                                });

                assertThat(apiResponse.isSuccess()).isTrue();
                PagedResponse<ContactResponse> pagedContacts = apiResponse.getResponse();

                assertThat(pagedContacts.getContent()).isEmpty();
        }

        @Test
        @Order(7)
        @DisplayName("Should get unique contacts across multiple address books")
        void testGetUniqueContactsAcrossMultipleAddressBooks() throws Exception {
                Long addressBook1 = createTestAddressBook(TestDataFactory.generateAddressBookName());
                Long addressBook2 = createTestAddressBook(TestDataFactory.generateAddressBookName());

                String sharedName = TestDataFactory.generateName();
                String sharedPhone = TestDataFactory.generateAustralianPhoneNumber();
                String uniqueName1 = TestDataFactory.generateName();
                String uniquePhone1 = TestDataFactory.generateAustralianPhoneNumber();
                String uniqueName2 = TestDataFactory.generateName();
                String uniquePhone2 = TestDataFactory.generateAustralianPhoneNumber();

                addTestContact(addressBook1, sharedName, sharedPhone);
                addTestContact(addressBook1, uniqueName1, uniquePhone1);
                addTestContact(addressBook2, sharedName, sharedPhone);
                addTestContact(addressBook2, uniqueName2, uniquePhone2);

                String responseJson = mockMvc.perform(get("/api/v1/addressbooks/" + addressBook1 + "/contacts/unique"))
                                .andExpect(status().isOk())
                                .andReturn()
                                .getResponse()
                                .getContentAsString();

                ApiResponse<PagedResponse<ContactResponse>> apiResponse = objectMapper.readValue(responseJson,
                                new TypeReference<ApiResponse<PagedResponse<ContactResponse>>>() {
                                });

                assertThat(apiResponse.isSuccess()).isTrue();
                PagedResponse<ContactResponse> uniqueContacts = apiResponse.getResponse();

                assertThat(uniqueContacts.getContent()).hasSize(3);
                assertThat(uniqueContacts.getContent()).extracting(ContactResponse::getName)
                                .containsExactlyInAnyOrder(sharedName, uniqueName1, uniqueName2);
        }

        @Test
        @Order(8)
        @DisplayName("Should reject address book creation with blank name")
        void testCreateAddressBookWithBlankName() throws Exception {
                AddressBookRequest request = AddressBookRequest.builder()
                                .name("")
                                .description("Valid description")
                                .build();

                mockMvc.perform(post("/api/v1/addressbooks")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request)))
                                .andExpect(status().isBadRequest());
        }

        @Test
        @Order(9)
        @DisplayName("Should reject address book creation with null name")
        void testCreateAddressBookWithNullName() throws Exception {
                AddressBookRequest request = AddressBookRequest.builder()
                                .name(null)
                                .description("Valid description")
                                .build();

                mockMvc.perform(post("/api/v1/addressbooks")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request)))
                                .andExpect(status().isBadRequest());
        }

        @Test
        @Order(10)
        @DisplayName("Should reject address book creation with name shorter than 2 characters")
        void testCreateAddressBookWithNameTooShort() throws Exception {
                AddressBookRequest request = AddressBookRequest.builder()
                                .name("A")
                                .description("Valid description")
                                .build();

                mockMvc.perform(post("/api/v1/addressbooks")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request)))
                                .andExpect(status().isBadRequest());
        }

        @Test
        @Order(11)
        @DisplayName("Should reject address book creation with name longer than 100 characters")
        void testCreateAddressBookWithNameTooLong() throws Exception {
                String longName = "A".repeat(101);
                AddressBookRequest request = AddressBookRequest.builder()
                                .name(longName)
                                .description("Valid description")
                                .build();

                mockMvc.perform(post("/api/v1/addressbooks")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request)))
                                .andExpect(status().isBadRequest());
        }

        @Test
        @Order(12)
        @DisplayName("Should reject address book creation with description longer than 200 characters")
        void testCreateAddressBookWithDescriptionTooLong() throws Exception {
                String longDescription = "A".repeat(201);
                AddressBookRequest request = AddressBookRequest.builder()
                                .name("Valid Name")
                                .description(longDescription)
                                .build();

                mockMvc.perform(post("/api/v1/addressbooks")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request)))
                                .andExpect(status().isBadRequest());
        }

        @Test
        @Order(13)
        @DisplayName("Should accept address book creation with name at minimum length (2 characters)")
        void testCreateAddressBookWithNameAtMinLength() throws Exception {
                AddressBookRequest request = AddressBookRequest.builder()
                                .name("AB")
                                .description("Valid description")
                                .build();

                mockMvc.perform(post("/api/v1/addressbooks")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request)))
                                .andExpect(status().isCreated());
        }

        @Test
        @Order(14)
        @DisplayName("Should accept address book creation with name at maximum length (100 characters)")
        void testCreateAddressBookWithNameAtMaxLength() throws Exception {
                String maxName = "A".repeat(100);
                AddressBookRequest request = AddressBookRequest.builder()
                                .name(maxName)
                                .description("Valid description")
                                .build();

                mockMvc.perform(post("/api/v1/addressbooks")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request)))
                                .andExpect(status().isCreated());
        }

        @Test
        @Order(15)
        @DisplayName("Should accept address book creation with description at maximum length (200 characters)")
        void testCreateAddressBookWithDescriptionAtMaxLength() throws Exception {
                String maxDescription = "A".repeat(200);
                AddressBookRequest request = AddressBookRequest.builder()
                                .name(TestDataFactory.generateAddressBookName())
                                .description(maxDescription)
                                .build();

                mockMvc.perform(post("/api/v1/addressbooks")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request)))
                                .andExpect(status().isCreated());
        }

        @Test
        @Order(16)
        @DisplayName("Should accept address book creation with null description")
        void testCreateAddressBookWithNullDescription() throws Exception {
                AddressBookRequest request = AddressBookRequest.builder()
                                .name(TestDataFactory.generateAddressBookName())
                                .description(null)
                                .build();

                mockMvc.perform(post("/api/v1/addressbooks")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request)))
                                .andExpect(status().isCreated());
        }

        @Test
        @Order(17)
        @DisplayName("Should reject address book creation with whitespace-only name")
        void testCreateAddressBookWithWhitespaceOnlyName() throws Exception {
                AddressBookRequest request = AddressBookRequest.builder()
                                .name("   ")
                                .description("Valid description")
                                .build();

                mockMvc.perform(post("/api/v1/addressbooks")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request)))
                                .andExpect(status().isBadRequest());
        }

        @Test
        @Order(18)
        @DisplayName("Should reject contact creation with blank name")
        void testAddContactWithBlankName() throws Exception {
                Long addressBookId = createTestAddressBook(TestDataFactory.generateAddressBookName());

                ContactRequest request = ContactRequest.builder()
                                .name("")
                                .phoneNumber(TestDataFactory.generateAustralianPhoneNumber())
                                .build();

                mockMvc.perform(post("/api/v1/addressbooks/" + addressBookId + "/contacts")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request)))
                                .andExpect(status().isBadRequest());
        }

        @Test
        @Order(19)
        @DisplayName("Should reject contact creation with null name")
        void testAddContactWithNullName() throws Exception {
                Long addressBookId = createTestAddressBook(TestDataFactory.generateAddressBookName());

                ContactRequest request = ContactRequest.builder()
                                .name(null)
                                .phoneNumber(TestDataFactory.generateAustralianPhoneNumber())
                                .build();

                mockMvc.perform(post("/api/v1/addressbooks/" + addressBookId + "/contacts")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request)))
                                .andExpect(status().isBadRequest());
        }

        @Test
        @Order(20)
        @DisplayName("Should reject contact creation with whitespace-only name")
        void testAddContactWithWhitespaceOnlyName() throws Exception {
                Long addressBookId = createTestAddressBook(TestDataFactory.generateAddressBookName());

                ContactRequest request = ContactRequest.builder()
                                .name("   ")
                                .phoneNumber(TestDataFactory.generateAustralianPhoneNumber())
                                .build();

                mockMvc.perform(post("/api/v1/addressbooks/" + addressBookId + "/contacts")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request)))
                                .andExpect(status().isBadRequest());
        }

        @Test
        @Order(21)
        @DisplayName("Should reject contact creation with blank phone number")
        void testAddContactWithBlankPhoneNumber() throws Exception {
                Long addressBookId = createTestAddressBook(TestDataFactory.generateAddressBookName());

                ContactRequest request = ContactRequest.builder()
                                .name(TestDataFactory.generateName())
                                .phoneNumber("")
                                .build();

                mockMvc.perform(post("/api/v1/addressbooks/" + addressBookId + "/contacts")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request)))
                                .andExpect(status().isBadRequest());
        }

        @Test
        @Order(22)
        @DisplayName("Should reject contact creation with null phone number")
        void testAddContactWithNullPhoneNumber() throws Exception {
                Long addressBookId = createTestAddressBook(TestDataFactory.generateAddressBookName());

                ContactRequest request = ContactRequest.builder()
                                .name(TestDataFactory.generateName())
                                .phoneNumber(null)
                                .build();

                mockMvc.perform(post("/api/v1/addressbooks/" + addressBookId + "/contacts")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request)))
                                .andExpect(status().isBadRequest());
        }

        @Test
        @Order(23)
        @DisplayName("Should reject contact creation with whitespace-only phone number")
        void testAddContactWithWhitespaceOnlyPhoneNumber() throws Exception {
                Long addressBookId = createTestAddressBook(TestDataFactory.generateAddressBookName());

                ContactRequest request = ContactRequest.builder()
                                .name(TestDataFactory.generateName())
                                .phoneNumber("   ")
                                .build();

                mockMvc.perform(post("/api/v1/addressbooks/" + addressBookId + "/contacts")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request)))
                                .andExpect(status().isBadRequest());
        }

        @Test
        @Order(24)
        @DisplayName("Should reject contact creation with both name and phone number blank")
        void testAddContactWithBothFieldsBlank() throws Exception {
                Long addressBookId = createTestAddressBook(TestDataFactory.generateAddressBookName());

                ContactRequest request = ContactRequest.builder()
                                .name("")
                                .phoneNumber("")
                                .build();

                mockMvc.perform(post("/api/v1/addressbooks/" + addressBookId + "/contacts")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request)))
                                .andExpect(status().isBadRequest());
        }

        @Test
        @Order(25)
        @DisplayName("Should reject contact creation with both name and phone number null")
        void testAddContactWithBothFieldsNull() throws Exception {
                Long addressBookId = createTestAddressBook(TestDataFactory.generateAddressBookName());

                ContactRequest request = ContactRequest.builder()
                                .name(null)
                                .phoneNumber(null)
                                .build();

                mockMvc.perform(post("/api/v1/addressbooks/" + addressBookId + "/contacts")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request)))
                                .andExpect(status().isBadRequest());
        }

        // ==================== Multiple Address Books and Duplicate Tests
        // ====================

        @Test
        @Order(26)
        @DisplayName("Should create multiple address books successfully")
        void testCreateMultipleAddressBooks() throws Exception {
                String name1 = TestDataFactory.generateAddressBookName();
                String name2 = TestDataFactory.generateAddressBookName();
                String name3 = TestDataFactory.generateAddressBookName();

                // Create first address book
                AddressBookRequest request1 = TestDataFactory.createAddressBookRequest(name1);
                String response1Json = mockMvc.perform(post("/api/v1/addressbooks")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request1)))
                                .andExpect(status().isCreated())
                                .andReturn()
                                .getResponse()
                                .getContentAsString();
                ApiResponse<AddressBookResponse> apiResponse1 = objectMapper.readValue(response1Json,
                                new TypeReference<ApiResponse<AddressBookResponse>>() {
                                });
                AddressBookResponse response1 = apiResponse1.getResponse();

                // Create second address book
                AddressBookRequest request2 = TestDataFactory.createAddressBookRequest(name2);
                String response2Json = mockMvc.perform(post("/api/v1/addressbooks")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request2)))
                                .andExpect(status().isCreated())
                                .andReturn()
                                .getResponse()
                                .getContentAsString();
                ApiResponse<AddressBookResponse> apiResponse2 = objectMapper.readValue(response2Json,
                                new TypeReference<ApiResponse<AddressBookResponse>>() {
                                });
                AddressBookResponse response2 = apiResponse2.getResponse();

                // Create third address book
                AddressBookRequest request3 = TestDataFactory.createAddressBookRequest(name3);
                String response3Json = mockMvc.perform(post("/api/v1/addressbooks")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request3)))
                                .andExpect(status().isCreated())
                                .andReturn()
                                .getResponse()
                                .getContentAsString();
                ApiResponse<AddressBookResponse> apiResponse3 = objectMapper.readValue(response3Json,
                                new TypeReference<ApiResponse<AddressBookResponse>>() {
                                });
                AddressBookResponse response3 = apiResponse3.getResponse();

                // Verify all address books were created with unique IDs
                assertThat(response1.getId()).isNotNull();
                assertThat(response2.getId()).isNotNull();
                assertThat(response3.getId()).isNotNull();
                assertThat(response1.getId()).isNotEqualTo(response2.getId());
                assertThat(response2.getId()).isNotEqualTo(response3.getId());
                assertThat(response1.getId()).isNotEqualTo(response3.getId());

                // Verify all address books can be retrieved
                String allBooksJson = mockMvc.perform(get("/api/v1/addressbooks"))
                                .andExpect(status().isOk())
                                .andReturn()
                                .getResponse()
                                .getContentAsString();

                ApiResponse<PagedResponse<AddressBookResponse>> allBooksApiResponse = objectMapper.readValue(
                                allBooksJson,
                                new TypeReference<ApiResponse<PagedResponse<AddressBookResponse>>>() {
                                });
                PagedResponse<AddressBookResponse> pagedResponse = allBooksApiResponse.getResponse();

                assertThat(pagedResponse.getContent()).hasSize(3);
                assertThat(pagedResponse.getContent()).extracting(AddressBookResponse::getName)
                                .containsExactlyInAnyOrder(name1, name2, name3);
        }

        @Test
        @Order(27)
        @DisplayName("Should prevent creating address book with duplicate name")
        void testPreventDuplicateAddressBookName() throws Exception {
                String duplicateName = "My Address Book";

                AddressBookRequest request1 = AddressBookRequest.builder()
                                .name(duplicateName)
                                .description("First address book")
                                .build();

                String response1Json = mockMvc.perform(post("/api/v1/addressbooks")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request1)))
                                .andExpect(status().isCreated())
                                .andReturn()
                                .getResponse()
                                .getContentAsString();

                ApiResponse<AddressBookResponse> apiResponse1 = objectMapper.readValue(response1Json,
                                new TypeReference<ApiResponse<AddressBookResponse>>() {
                                });
                AddressBookResponse response1 = apiResponse1.getResponse();
                assertThat(response1.getId()).isNotNull();
                assertThat(response1.getName()).isEqualTo(duplicateName);

                AddressBookRequest request2 = AddressBookRequest.builder()
                                .name(duplicateName)
                                .description("Second address book with same name")
                                .build();

                mockMvc.perform(post("/api/v1/addressbooks")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request2)))
                                .andExpect(status().isConflict());

                String allBooksJson = mockMvc.perform(get("/api/v1/addressbooks"))
                                .andExpect(status().isOk())
                                .andReturn()
                                .getResponse()
                                .getContentAsString();

                ApiResponse<PagedResponse<AddressBookResponse>> allBooksApiResponse = objectMapper.readValue(
                                allBooksJson,
                                new TypeReference<ApiResponse<PagedResponse<AddressBookResponse>>>() {
                                });
                PagedResponse<AddressBookResponse> pagedResponse = allBooksApiResponse.getResponse();

                assertThat(pagedResponse.getContent()).hasSize(1);
                assertThat(pagedResponse.getContent().get(0).getName()).isEqualTo(duplicateName);
        }

        @Test
        @Order(28)
        @DisplayName("Should add multiple contacts to an address book successfully")
        void testAddMultipleContactsToAddressBook() throws Exception {
                Long addressBookId = createTestAddressBook(TestDataFactory.generateAddressBookName());

                String name1 = TestDataFactory.generateName();
                String phone1 = TestDataFactory.generateAustralianPhoneNumber();
                String name2 = TestDataFactory.generateName();
                String phone2 = TestDataFactory.generateAustralianPhoneNumber();
                String name3 = TestDataFactory.generateName();
                String phone3 = TestDataFactory.generateAustralianPhoneNumber();

                // Add first contact
                ContactRequest request1 = TestDataFactory.createContactRequest(name1, phone1);
                String response1Json = mockMvc.perform(post("/api/v1/addressbooks/" + addressBookId + "/contacts")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request1)))
                                .andExpect(status().isCreated())
                                .andReturn()
                                .getResponse()
                                .getContentAsString();
                ApiResponse<ContactResponse> apiResponse1 = objectMapper.readValue(response1Json,
                                new TypeReference<ApiResponse<ContactResponse>>() {
                                });
                ContactResponse contact1 = apiResponse1.getResponse();

                // Add second contact
                ContactRequest request2 = TestDataFactory.createContactRequest(name2, phone2);
                String response2Json = mockMvc.perform(post("/api/v1/addressbooks/" + addressBookId + "/contacts")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request2)))
                                .andExpect(status().isCreated())
                                .andReturn()
                                .getResponse()
                                .getContentAsString();
                ApiResponse<ContactResponse> apiResponse2 = objectMapper.readValue(response2Json,
                                new TypeReference<ApiResponse<ContactResponse>>() {
                                });
                ContactResponse contact2 = apiResponse2.getResponse();

                // Add third contact
                ContactRequest request3 = TestDataFactory.createContactRequest(name3, phone3);
                String response3Json = mockMvc.perform(post("/api/v1/addressbooks/" + addressBookId + "/contacts")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request3)))
                                .andExpect(status().isCreated())
                                .andReturn()
                                .getResponse()
                                .getContentAsString();
                ApiResponse<ContactResponse> apiResponse3 = objectMapper.readValue(response3Json,
                                new TypeReference<ApiResponse<ContactResponse>>() {
                                });
                ContactResponse contact3 = apiResponse3.getResponse();

                // Verify all contacts have unique IDs
                assertThat(contact1.getId()).isNotNull();
                assertThat(contact2.getId()).isNotNull();
                assertThat(contact3.getId()).isNotNull();
                assertThat(contact1.getId()).isNotEqualTo(contact2.getId());
                assertThat(contact2.getId()).isNotEqualTo(contact3.getId());
                assertThat(contact1.getId()).isNotEqualTo(contact3.getId());

                // Verify all contacts can be retrieved
                String allContactsJson = mockMvc.perform(get("/api/v1/addressbooks/" + addressBookId + "/contacts"))
                                .andExpect(status().isOk())
                                .andReturn()
                                .getResponse()
                                .getContentAsString();

                ApiResponse<PagedResponse<ContactResponse>> allContactsApiResponse = objectMapper.readValue(
                                allContactsJson,
                                new TypeReference<ApiResponse<PagedResponse<ContactResponse>>>() {
                                });
                PagedResponse<ContactResponse> allContacts = allContactsApiResponse.getResponse();

                assertThat(allContacts.getContent()).hasSize(3);
                assertThat(allContacts.getContent()).extracting(ContactResponse::getName)
                                .containsExactlyInAnyOrder(name1, name2, name3);
                assertThat(allContacts.getContent()).extracting(ContactResponse::getPhoneNumber)
                                .containsExactlyInAnyOrder(phone1, phone2, phone3);
        }

        @Test
        @Order(29)
        @DisplayName("Should prevent adding contact with duplicate phone number in same address book")
        void testPreventDuplicateContactPhoneNumberInSameAddressBook() throws Exception {
                Long addressBookId = createTestAddressBook(TestDataFactory.generateAddressBookName());

                String duplicatePhone = TestDataFactory.generateAustralianPhoneNumber();

                // Add first contact
                ContactRequest request1 = ContactRequest.builder()
                                .name("John Doe")
                                .phoneNumber(duplicatePhone)
                                .build();

                String response1Json = mockMvc.perform(post("/api/v1/addressbooks/" + addressBookId + "/contacts")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request1)))
                                .andExpect(status().isCreated())
                                .andReturn()
                                .getResponse()
                                .getContentAsString();

                ApiResponse<ContactResponse> apiResponse1 = objectMapper.readValue(response1Json,
                                new TypeReference<ApiResponse<ContactResponse>>() {
                                });
                ContactResponse contact1 = apiResponse1.getResponse();
                assertThat(contact1.getId()).isNotNull();
                assertThat(contact1.getPhoneNumber()).isEqualTo(duplicatePhone);

                // Try to add second contact with same phone number
                ContactRequest request2 = ContactRequest.builder()
                                .name("Jane Doe")
                                .phoneNumber(duplicatePhone)
                                .build();

                mockMvc.perform(post("/api/v1/addressbooks/" + addressBookId + "/contacts")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request2)))
                                .andExpect(status().isConflict());

                // Verify only one contact exists
                String allContactsJson = mockMvc.perform(get("/api/v1/addressbooks/" + addressBookId + "/contacts"))
                                .andExpect(status().isOk())
                                .andReturn()
                                .getResponse()
                                .getContentAsString();

                ApiResponse<PagedResponse<ContactResponse>> allContactsApiResponse = objectMapper.readValue(
                                allContactsJson,
                                new TypeReference<ApiResponse<PagedResponse<ContactResponse>>>() {
                                });
                PagedResponse<ContactResponse> allContacts = allContactsApiResponse.getResponse();

                assertThat(allContacts.getContent()).hasSize(1);
                assertThat(allContacts.getContent().get(0).getPhoneNumber()).isEqualTo(duplicatePhone);
        }

        @Test
        @Order(30)
        @DisplayName("Should allow same contact phone number in different address books")
        void testAllowSameContactPhoneNumberInDifferentAddressBooks() throws Exception {
                Long addressBook1 = createTestAddressBook(TestDataFactory.generateAddressBookName());
                Long addressBook2 = createTestAddressBook(TestDataFactory.generateAddressBookName());

                String sharedPhone = TestDataFactory.generateAustralianPhoneNumber();

                // Add contact to first address book
                ContactRequest request1 = ContactRequest.builder()
                                .name("John Doe")
                                .phoneNumber(sharedPhone)
                                .build();

                String response1Json = mockMvc.perform(post("/api/v1/addressbooks/" + addressBook1 + "/contacts")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request1)))
                                .andExpect(status().isCreated())
                                .andReturn()
                                .getResponse()
                                .getContentAsString();

                ApiResponse<ContactResponse> apiResponse1 = objectMapper.readValue(response1Json,
                                new TypeReference<ApiResponse<ContactResponse>>() {
                                });
                ContactResponse contact1 = apiResponse1.getResponse();
                assertThat(contact1.getId()).isNotNull();

                // Add contact with same phone number to second address book (should succeed)
                ContactRequest request2 = ContactRequest.builder()
                                .name("Jane Doe")
                                .phoneNumber(sharedPhone)
                                .build();

                String response2Json = mockMvc.perform(post("/api/v1/addressbooks/" + addressBook2 + "/contacts")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request2)))
                                .andExpect(status().isCreated())
                                .andReturn()
                                .getResponse()
                                .getContentAsString();

                ApiResponse<ContactResponse> apiResponse2 = objectMapper.readValue(response2Json,
                                new TypeReference<ApiResponse<ContactResponse>>() {
                                });
                ContactResponse contact2 = apiResponse2.getResponse();
                assertThat(contact2.getId()).isNotNull();

                // Verify both contacts exist in their respective address books
                String contacts1Json = mockMvc.perform(get("/api/v1/addressbooks/" + addressBook1 + "/contacts"))
                                .andExpect(status().isOk())
                                .andReturn()
                                .getResponse()
                                .getContentAsString();

                String contacts2Json = mockMvc.perform(get("/api/v1/addressbooks/" + addressBook2 + "/contacts"))
                                .andExpect(status().isOk())
                                .andReturn()
                                .getResponse()
                                .getContentAsString();

                ApiResponse<PagedResponse<ContactResponse>> contacts1ApiResponse = objectMapper.readValue(contacts1Json,
                                new TypeReference<ApiResponse<PagedResponse<ContactResponse>>>() {
                                });
                ApiResponse<PagedResponse<ContactResponse>> contacts2ApiResponse = objectMapper.readValue(contacts2Json,
                                new TypeReference<ApiResponse<PagedResponse<ContactResponse>>>() {
                                });
                PagedResponse<ContactResponse> contacts1 = contacts1ApiResponse.getResponse();
                PagedResponse<ContactResponse> contacts2 = contacts2ApiResponse.getResponse();

                assertThat(contacts1.getContent()).hasSize(1);
                assertThat(contacts2.getContent()).hasSize(1);
                assertThat(contacts1.getContent().get(0).getPhoneNumber()).isEqualTo(sharedPhone);
                assertThat(contacts2.getContent().get(0).getPhoneNumber()).isEqualTo(sharedPhone);
        }

        @Test
        @Order(31)
        @DisplayName("Should update contact successfully")
        void testUpdateContactSuccessfully() throws Exception {
                Long addressBookId = createTestAddressBook(TestDataFactory.generateAddressBookName());

                String originalName = TestDataFactory.generateName();
                String originalPhone = TestDataFactory.generateAustralianPhoneNumber();
                Long contactId = addTestContact(addressBookId, originalName, originalPhone);

                String updatedName = TestDataFactory.generateName();
                String updatedPhone = TestDataFactory.generateAustralianPhoneNumber();

                ContactRequest updateRequest = ContactRequest.builder()
                                .name(updatedName)
                                .phoneNumber(updatedPhone)
                                .build();

                String responseJson = mockMvc
                                .perform(put("/api/v1/addressbooks/" + addressBookId + "/contacts/" + contactId)
                                                .contentType(MediaType.APPLICATION_JSON)
                                                .content(objectMapper.writeValueAsString(updateRequest)))
                                .andExpect(status().isOk())
                                .andReturn()
                                .getResponse()
                                .getContentAsString();

                ApiResponse<ContactResponse> apiResponse = objectMapper.readValue(responseJson,
                                new TypeReference<ApiResponse<ContactResponse>>() {
                                });
                ContactResponse updatedContact = apiResponse.getResponse();

                assertThat(updatedContact.getId()).isEqualTo(contactId);
                assertThat(updatedContact.getName()).isEqualTo(updatedName);
                assertThat(updatedContact.getPhoneNumber()).isEqualTo(updatedPhone);
                assertThat(updatedContact.getAddressBookId()).isEqualTo(addressBookId);
        }

        @Test
        @Order(32)
        @DisplayName("Should update contact name only keeping same phone number")
        void testUpdateContactNameOnly() throws Exception {
                Long addressBookId = createTestAddressBook(TestDataFactory.generateAddressBookName());

                String originalName = TestDataFactory.generateName();
                String originalPhone = TestDataFactory.generateAustralianPhoneNumber();
                Long contactId = addTestContact(addressBookId, originalName, originalPhone);

                String updatedName = TestDataFactory.generateName();

                ContactRequest updateRequest = ContactRequest.builder()
                                .name(updatedName)
                                .phoneNumber(originalPhone)
                                .build();

                String responseJson = mockMvc
                                .perform(put("/api/v1/addressbooks/" + addressBookId + "/contacts/" + contactId)
                                                .contentType(MediaType.APPLICATION_JSON)
                                                .content(objectMapper.writeValueAsString(updateRequest)))
                                .andExpect(status().isOk())
                                .andReturn()
                                .getResponse()
                                .getContentAsString();

                ApiResponse<ContactResponse> apiResponse = objectMapper.readValue(responseJson,
                                new TypeReference<ApiResponse<ContactResponse>>() {
                                });
                ContactResponse updatedContact = apiResponse.getResponse();

                assertThat(updatedContact.getName()).isEqualTo(updatedName);
                assertThat(updatedContact.getPhoneNumber()).isEqualTo(originalPhone);
        }

        @Test
        @Order(33)
        @DisplayName("Should fail to update contact with duplicate phone number in same address book")
        void testUpdateContactWithDuplicatePhoneNumber() throws Exception {
                Long addressBookId = createTestAddressBook(TestDataFactory.generateAddressBookName());

                String name1 = TestDataFactory.generateName();
                String phone1 = TestDataFactory.generateAustralianPhoneNumber();
                String name2 = TestDataFactory.generateName();
                String phone2 = TestDataFactory.generateAustralianPhoneNumber();

                addTestContact(addressBookId, name1, phone1);
                Long contactId2 = addTestContact(addressBookId, name2, phone2);

                // Try to update contact2 with phone1 (which already exists)
                ContactRequest updateRequest = ContactRequest.builder()
                                .name(name2)
                                .phoneNumber(phone1)
                                .build();

                mockMvc.perform(put("/api/v1/addressbooks/" + addressBookId + "/contacts/" + contactId2)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(updateRequest)))
                                .andExpect(status().isConflict());
        }

        @Test
        @Order(34)
        @DisplayName("Should fail to update non-existent contact")
        void testUpdateNonExistentContact() throws Exception {
                Long addressBookId = createTestAddressBook(TestDataFactory.generateAddressBookName());

                Long nonExistentContactId = 99999L;

                ContactRequest updateRequest = ContactRequest.builder()
                                .name(TestDataFactory.generateName())
                                .phoneNumber(TestDataFactory.generateAustralianPhoneNumber())
                                .build();

                mockMvc.perform(put("/api/v1/addressbooks/" + addressBookId + "/contacts/" + nonExistentContactId)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(updateRequest)))
                                .andExpect(status().isNotFound());
        }

        @Test
        @Order(35)
        @DisplayName("Should fail to update contact in non-existent address book")
        void testUpdateContactInNonExistentAddressBook() throws Exception {
                Long nonExistentAddressBookId = 99999L;
                Long contactId = 1L;

                ContactRequest updateRequest = ContactRequest.builder()
                                .name(TestDataFactory.generateName())
                                .phoneNumber(TestDataFactory.generateAustralianPhoneNumber())
                                .build();

                mockMvc.perform(put("/api/v1/addressbooks/" + nonExistentAddressBookId + "/contacts/" + contactId)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(updateRequest)))
                                .andExpect(status().isNotFound());
        }

        @Test
        @Order(36)
        @DisplayName("Should fail to update contact with invalid data")
        void testUpdateContactWithInvalidData() throws Exception {
                Long addressBookId = createTestAddressBook(TestDataFactory.generateAddressBookName());
                Long contactId = addTestContact(addressBookId, TestDataFactory.generateName(),
                                TestDataFactory.generateAustralianPhoneNumber());

                // Update with blank name
                ContactRequest updateRequest = ContactRequest.builder()
                                .name("")
                                .phoneNumber(TestDataFactory.generateAustralianPhoneNumber())
                                .build();

                mockMvc.perform(put("/api/v1/addressbooks/" + addressBookId + "/contacts/" + contactId)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(updateRequest)))
                                .andExpect(status().isBadRequest());
        }

        @Test
        @Order(37)
        @DisplayName("Should delete multiple contacts by IDs")
        void testBulkDeleteContacts() throws Exception {
                Long addressBookId = createTestAddressBook(TestDataFactory.generateAddressBookName());

                Long contactId1 = addTestContact(addressBookId, TestDataFactory.generateName(),
                                TestDataFactory.generateAustralianPhoneNumber());
                Long contactId2 = addTestContact(addressBookId, TestDataFactory.generateName(),
                                TestDataFactory.generateAustralianPhoneNumber());
                Long contactId3 = addTestContact(addressBookId, TestDataFactory.generateName(),
                                TestDataFactory.generateAustralianPhoneNumber());

                // Delete contacts 1 and 2
                String responseJson = mockMvc.perform(delete("/api/v1/addressbooks/" + addressBookId + "/contacts/bulk")
                                .param("ids", contactId1.toString(), contactId2.toString()))
                                .andExpect(status().isOk())
                                .andReturn()
                                .getResponse()
                                .getContentAsString();

                assertThat(responseJson).contains("\"deletedCount\":2");
                assertThat(responseJson).contains("\"requestedCount\":2");

                // Verify only contact3 remains
                String contactsJson = mockMvc.perform(get("/api/v1/addressbooks/" + addressBookId + "/contacts"))
                                .andExpect(status().isOk())
                                .andReturn()
                                .getResponse()
                                .getContentAsString();

                ApiResponse<PagedResponse<ContactResponse>> apiResponse = objectMapper.readValue(contactsJson,
                                new TypeReference<ApiResponse<PagedResponse<ContactResponse>>>() {
                                });
                PagedResponse<ContactResponse> pagedContacts = apiResponse.getResponse();

                assertThat(pagedContacts.getContent()).hasSize(1);
                assertThat(pagedContacts.getContent().get(0).getId()).isEqualTo(contactId3);
        }

        @Test
        @Order(38)
        @DisplayName("Should delete all contacts in address book")
        void testDeleteAllContacts() throws Exception {
                Long addressBookId = createTestAddressBook(TestDataFactory.generateAddressBookName());

                addTestContact(addressBookId, TestDataFactory.generateName(),
                                TestDataFactory.generateAustralianPhoneNumber());
                addTestContact(addressBookId, TestDataFactory.generateName(),
                                TestDataFactory.generateAustralianPhoneNumber());
                addTestContact(addressBookId, TestDataFactory.generateName(),
                                TestDataFactory.generateAustralianPhoneNumber());

                String responseJson = mockMvc.perform(delete("/api/v1/addressbooks/" + addressBookId + "/contacts"))
                                .andExpect(status().isOk())
                                .andReturn()
                                .getResponse()
                                .getContentAsString();

                assertThat(responseJson).contains("\"deletedCount\":3");

                // Verify no contacts remain
                String contactsJson = mockMvc.perform(get("/api/v1/addressbooks/" + addressBookId + "/contacts"))
                                .andExpect(status().isOk())
                                .andReturn()
                                .getResponse()
                                .getContentAsString();

                ApiResponse<PagedResponse<ContactResponse>> apiResponse = objectMapper.readValue(contactsJson,
                                new TypeReference<ApiResponse<PagedResponse<ContactResponse>>>() {
                                });
                PagedResponse<ContactResponse> pagedContacts = apiResponse.getResponse();

                assertThat(pagedContacts.getContent()).isEmpty();
        }

        @Test
        @Order(39)
        @DisplayName("Should handle bulk delete with non-existent IDs gracefully")
        void testBulkDeleteWithNonExistentIds() throws Exception {
                Long addressBookId = createTestAddressBook(TestDataFactory.generateAddressBookName());

                Long contactId = addTestContact(addressBookId, TestDataFactory.generateName(),
                                TestDataFactory.generateAustralianPhoneNumber());

                // Try to delete existing and non-existent contacts
                String responseJson = mockMvc.perform(delete("/api/v1/addressbooks/" + addressBookId + "/contacts/bulk")
                                .param("ids", contactId.toString(), "99999", "99998"))
                                .andExpect(status().isOk())
                                .andReturn()
                                .getResponse()
                                .getContentAsString();

                // Should only delete the one that exists
                assertThat(responseJson).contains("\"deletedCount\":1");
                assertThat(responseJson).contains("\"requestedCount\":3");
        }

        @Test
        @Order(40)
        @DisplayName("Should handle delete all on empty address book")
        void testDeleteAllContactsOnEmptyAddressBook() throws Exception {
                Long addressBookId = createTestAddressBook(TestDataFactory.generateAddressBookName());

                String responseJson = mockMvc.perform(delete("/api/v1/addressbooks/" + addressBookId + "/contacts"))
                                .andExpect(status().isOk())
                                .andReturn()
                                .getResponse()
                                .getContentAsString();

                assertThat(responseJson).contains("\"deletedCount\":0");
        }

        @Test
        @Order(41)
        @DisplayName("Should search address books by partial name (case-insensitive)")
        void testSearchAddressBooksByPartialName() throws Exception {
                // Create address books with different names
                createTestAddressBook("Customer Sydney");
                createTestAddressBook("Customer Melbourne");
                createTestAddressBook("Personal Contacts");
                createTestAddressBook("Work Customers");

                // Search for "customer" - should find 3 matches
                String responseJson = mockMvc.perform(get("/api/v1/addressbooks/search")
                                .param("name", "customer"))
                                .andExpect(status().isOk())
                                .andReturn()
                                .getResponse()
                                .getContentAsString();

                ApiResponse<PagedResponse<AddressBookResponse>> apiResponse = objectMapper.readValue(responseJson,
                                new TypeReference<ApiResponse<PagedResponse<AddressBookResponse>>>() {
                                });
                PagedResponse<AddressBookResponse> pagedResponse = apiResponse.getResponse();

                assertThat(pagedResponse.getContent()).hasSize(3);
                assertThat(pagedResponse.getContent()).extracting(AddressBookResponse::getName)
                                .containsExactlyInAnyOrder("Customer Sydney", "Customer Melbourne", "Work Customers");
        }

        @Test
        @Order(42)
        @DisplayName("Should search address books with pagination")
        void testSearchAddressBooksWithPagination() throws Exception {
                // Create 5 address books with "Test" in name
                createTestAddressBook("Test Book 1");
                createTestAddressBook("Test Book 2");
                createTestAddressBook("Test Book 3");
                createTestAddressBook("Test Book 4");
                createTestAddressBook("Test Book 5");

                // Search with pagination - page 0, size 2
                String responseJson = mockMvc.perform(get("/api/v1/addressbooks/search")
                                .param("name", "Test")
                                .param("page", "0")
                                .param("size", "2")
                                .param("sortBy", "name")
                                .param("sortDir", "asc"))
                                .andExpect(status().isOk())
                                .andReturn()
                                .getResponse()
                                .getContentAsString();

                ApiResponse<PagedResponse<AddressBookResponse>> apiResponse = objectMapper.readValue(responseJson,
                                new TypeReference<ApiResponse<PagedResponse<AddressBookResponse>>>() {
                                });
                PagedResponse<AddressBookResponse> pagedResponse = apiResponse.getResponse();

                assertThat(pagedResponse.getContent()).hasSize(2);
                assertThat(pagedResponse.getTotalElements()).isEqualTo(5);
                assertThat(pagedResponse.getTotalPages()).isEqualTo(3);
                assertThat(pagedResponse.isFirst()).isTrue();
                assertThat(pagedResponse.isLast()).isFalse();
        }

        @Test
        @Order(43)
        @DisplayName("Should return empty result when search finds no matches")
        void testSearchAddressBooksNoResults() throws Exception {
                createTestAddressBook("Personal Contacts");
                createTestAddressBook("Work Colleagues");

                String responseJson = mockMvc.perform(get("/api/v1/addressbooks/search")
                                .param("name", "nonexistent"))
                                .andExpect(status().isOk())
                                .andReturn()
                                .getResponse()
                                .getContentAsString();

                ApiResponse<PagedResponse<AddressBookResponse>> apiResponse = objectMapper.readValue(responseJson,
                                new TypeReference<ApiResponse<PagedResponse<AddressBookResponse>>>() {
                                });
                PagedResponse<AddressBookResponse> pagedResponse = apiResponse.getResponse();

                assertThat(pagedResponse.getContent()).isEmpty();
                assertThat(pagedResponse.getTotalElements()).isEqualTo(0);
                assertThat(pagedResponse.isEmpty()).isTrue();
        }

        @Test
        @Order(44)
        @DisplayName("Should search address books case-insensitively")
        void testSearchAddressBooksCaseInsensitive() throws Exception {
                createTestAddressBook("UPPERCASE Book");
                createTestAddressBook("lowercase book");
                createTestAddressBook("MixedCase Book");

                // Search with lowercase
                String responseJson = mockMvc.perform(get("/api/v1/addressbooks/search")
                                .param("name", "book"))
                                .andExpect(status().isOk())
                                .andReturn()
                                .getResponse()
                                .getContentAsString();

                ApiResponse<PagedResponse<AddressBookResponse>> apiResponse = objectMapper.readValue(responseJson,
                                new TypeReference<ApiResponse<PagedResponse<AddressBookResponse>>>() {
                                });
                PagedResponse<AddressBookResponse> pagedResponse = apiResponse.getResponse();

                assertThat(pagedResponse.getContent()).hasSize(3);
        }

        private Long createTestAddressBook(String name) throws Exception {
                AddressBookRequest request = TestDataFactory.createAddressBookRequest(name);

                String response = mockMvc.perform(post("/api/v1/addressbooks")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request)))
                                .andExpect(status().isCreated())
                                .andReturn()
                                .getResponse()
                                .getContentAsString();

                ApiResponse<AddressBookResponse> apiResponse = objectMapper.readValue(response,
                                new TypeReference<ApiResponse<AddressBookResponse>>() {
                                });
                return apiResponse.getResponse().getId();
        }

        private Long addTestContact(Long addressBookId, String name, String phone)
                        throws Exception {
                ContactRequest request = TestDataFactory.createContactRequest(name, phone);

                String response = mockMvc.perform(post("/api/v1/addressbooks/" +
                                addressBookId + "/contacts")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request)))
                                .andExpect(status().isCreated())
                                .andReturn()
                                .getResponse()
                                .getContentAsString();

                ApiResponse<ContactResponse> apiResponse = objectMapper.readValue(response,
                                new TypeReference<ApiResponse<ContactResponse>>() {
                                });
                return apiResponse.getResponse().getId();
        }
}