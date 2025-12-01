package com.addressbook.project.mapper;

import org.springframework.stereotype.Component;

import com.addressbook.project.dto.ContactResponse;
import com.addressbook.project.entity.Contact;

/**
 * Mapper for Contact entity to ContactResponse DTO.
 * Implements EntityMapper interface for Dependency Inversion Principle
 * compliance.
 */
@Component
public class ContactMapper implements EntityMapper<Contact, ContactResponse> {

	@Override
	public ContactResponse mapToResponse(Contact contact) {
		return ContactResponse.builder()
				.id(contact.getId()).name(contact.getName())
				.phoneNumber(contact.getPhoneNumber())
				.addressBookId(contact.getAddressBook() != null ? contact.getAddressBook().getId() : null)
				.addressBookName(contact.getAddressBook() != null ? contact.getAddressBook().getName() : null)
				.createdAt(contact.getCreatedAt()).build();
	}
}