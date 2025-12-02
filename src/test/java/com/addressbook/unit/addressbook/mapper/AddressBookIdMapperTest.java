package com.addressbook.unit.addressbook.mapper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.project.dto.addressbook.AddressBookIdResponse;
import com.project.entity.addressbook.AddressBook;
import com.project.mapper.addressbook.AddressBookIdMapper;

import java.time.LocalDateTime;
import java.util.HashSet;

import static org.assertj.core.api.Assertions.*;

@DisplayName("Address Book ID Mapper Unit Tests")
class AddressBookIdMapperTest {

    private AddressBookIdMapper mapper;
    private AddressBook addressBook;

    @BeforeEach
    void setUp() {
        mapper = new AddressBookIdMapper();

        addressBook = AddressBook.builder()
                .id(1L)
                .name("Work Contacts")
                .description("Work related contacts")
                .contacts(new HashSet<>())
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }

    @Test
    @DisplayName("Should map AddressBook to AddressBookIdResponse")
    void testMapToIdResponse() {
        AddressBookIdResponse response = mapper.mapToResponse(addressBook);

        assertThat(response).isNotNull();
        assertThat(response.getId()).isEqualTo(1L);
        assertThat(response.getName()).isEqualTo("Work Contacts");
    }

    @Test
    @DisplayName("Should extract only id and name fields")
    void testMapToIdResponseExtractsOnlyRequiredFields() {
        AddressBookIdResponse response = mapper.mapToResponse(addressBook);

        assertThat(response.getId()).isNotNull();
        assertThat(response.getName()).isNotNull();
        // Verify it doesn't include other fields
        assertThat(response).hasNoNullFieldsOrProperties();
    }

    @Test
    @DisplayName("Should handle AddressBook with different names")
    void testMapToIdResponseWithDifferentNames() {
        AddressBook personalBook = AddressBook.builder()
                .id(2L)
                .name("Personal Contacts")
                .description("Personal contacts")
                .contacts(new HashSet<>())
                .build();

        AddressBookIdResponse response = mapper.mapToResponse(personalBook);

        assertThat(response.getId()).isEqualTo(2L);
        assertThat(response.getName()).isEqualTo("Personal Contacts");
    }
}
