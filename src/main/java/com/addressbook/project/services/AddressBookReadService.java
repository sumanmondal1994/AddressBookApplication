package com.addressbook.project.services;

import java.util.List;

import org.springframework.data.domain.Pageable;

import com.addressbook.project.dto.AddressBookResponse;
import com.addressbook.project.dto.PagedResponse;

public interface AddressBookReadService {

    AddressBookResponse getAddressBookById(Long id);

    AddressBookResponse getAddressBookByName(String name);

    PagedResponse<AddressBookResponse> searchByName(String name, Pageable pageable);

    List<AddressBookResponse> getAllAddressBooks();

    PagedResponse<AddressBookResponse> getAllAddressBooks(Pageable pageable);
}
