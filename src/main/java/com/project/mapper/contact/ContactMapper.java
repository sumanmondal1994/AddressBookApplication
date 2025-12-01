package com.project.mapper.contact;

import org.springframework.stereotype.Component;

import com.project.dto.contact.ContactResponse;
import com.project.entity.contact.Contact;

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