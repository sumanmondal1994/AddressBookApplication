package com.project.mapper.addressbook;

import org.springframework.stereotype.Component;

import com.project.dto.addressbook.AddressBookIdResponse;
import com.project.entity.addressbook.AddressBook;
import com.project.mapper.contact.EntityMapper;

import lombok.RequiredArgsConstructor;



@Component
@RequiredArgsConstructor
public class AddressBookIdMapper implements  EntityMapper<AddressBook, AddressBookIdResponse> {

	 public AddressBookIdResponse mapToResponse(AddressBook addressBook) {
	        return AddressBookIdResponse.builder()
	                .id(addressBook.getId())
	                .name(addressBook.getName())
	                .build();
	    }

}
