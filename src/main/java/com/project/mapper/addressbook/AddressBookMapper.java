package com.project.mapper.addressbook;

import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.project.dto.addressbook.AddressBookResponse;
import com.project.dto.contact.ContactResponse;
import com.project.entity.addressbook.AddressBook;
import com.project.entity.contact.Contact;
import com.project.mapper.contact.EntityMapper;

import lombok.RequiredArgsConstructor;

/**
 * Mapper for AddressBook entity to AddressBookResponse DTO.
 * Implements EntityMapper interface for Dependency Inversion Principle
 * compliance.
 */
@Component
@RequiredArgsConstructor
public class AddressBookMapper implements EntityMapper<AddressBook, AddressBookResponse> {

	private final EntityMapper<Contact,ContactResponse> contactMapper;

	@Override
	public AddressBookResponse mapToResponse(AddressBook addressBook) {
		return AddressBookResponse.builder()
				.id(addressBook.getId())
				.name(addressBook.getName())
				.description(addressBook.getDescription())
				.contactCount(addressBook.getContacts().size())
				.contacts(addressBook.getContacts().stream()
						.map(contactMapper::mapToResponse)
						.collect(Collectors.toList()))
				.createdAt(addressBook.getCreatedAt())
				.updatedAt(addressBook.getUpdatedAt())
				.build();
	}
}
