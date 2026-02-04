package com.project.controller.addressbook;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.project.dto.addressbook.AddressBookRequest;
import com.project.dto.addressbook.AddressBookResponse;
import com.project.dto.response.ApiResponse;
import com.project.services.addressbook.AddressBookService;

/**
 * V2 Address Book Controller with contacts support.This would be required.
 */
@RestController
@RequestMapping("/api/v2/addressbooks")
@RequiredArgsConstructor
@Tag(name = "Address Books V2", description = "Address book management APIs - Version 2 (with contacts support)")
public class AddressBookControllerV2 {

    private final AddressBookService addressBookService;

    @PostMapping
    @Operation(summary = "Create a new address book with optional contacts")
    public ResponseEntity<ApiResponse<AddressBookResponse>> createAddressBook(
            @Valid @RequestBody AddressBookRequest request) {
        AddressBookResponse addressBook = addressBookService.createAddressBookWithContacts(request);
        return new ResponseEntity<>(
                ApiResponse.success(addressBook, "Address book created successfully with contacts"),
                HttpStatus.CREATED);
    }

}