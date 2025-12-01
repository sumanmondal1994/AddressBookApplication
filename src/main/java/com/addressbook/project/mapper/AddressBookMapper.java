package com.addressbook.project.mapper;

import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.addressbook.project.dto.AddressBookResponse;
import com.addressbook.project.dto.ContactResponse;
import com.addressbook.project.entity.AddressBook;
import com.addressbook.project.entity.Contact;

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
