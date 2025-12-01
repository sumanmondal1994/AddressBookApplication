package com.project.services.addressbook;

import java.util.List;

import org.springframework.data.domain.Pageable;

import com.project.dto.addressbook.AddressBookResponse;
import com.project.dto.response.PagedResponse;

public interface AddressBookReadService {

    AddressBookResponse getAddressBookById(Long id);

    AddressBookResponse getAddressBookByName(String name);

    PagedResponse<AddressBookResponse> searchByName(String name, Pageable pageable);

    List<AddressBookResponse> getAllAddressBooks();

    PagedResponse<AddressBookResponse> getAllAddressBooks(Pageable pageable);
}
