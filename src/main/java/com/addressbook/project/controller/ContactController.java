package com.addressbook.project.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Map;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.addressbook.project.dto.ApiResponse;
import com.addressbook.project.dto.ContactRequest;
import com.addressbook.project.dto.ContactResponse;
import com.addressbook.project.dto.PagedResponse;
import com.addressbook.project.services.ContactService;

@RestController
@RequestMapping("/api/v1/addressbooks/{addressBookId}/contacts")
@RequiredArgsConstructor
@Tag(name = "Contacts", description = "Contact management APIs")
public class ContactController {

    private final ContactService contactService;

    @PostMapping
    @Operation(summary = "Add a new contact to an address book")
    public ResponseEntity<ApiResponse<ContactResponse>> addContact(
            @PathVariable Long addressBookId,
            @Valid @RequestBody ContactRequest request) {
        ContactResponse contact = contactService.addContact(addressBookId, request);
        return new ResponseEntity<>(
                ApiResponse.success(contact, "Contact added successfully"),
                HttpStatus.CREATED);
    }

    @GetMapping
    @Operation(summary = "Get all contacts in an address book (paginated)")
    public ResponseEntity<ApiResponse<PagedResponse<ContactResponse>>> getAllContacts(
            @PathVariable Long addressBookId,
            @Parameter(description = "Page number (0-based)") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size (max 100)") @RequestParam(defaultValue = "20") int size,
            @Parameter(description = "Sort field") @RequestParam(defaultValue = "id") String sortBy,
            @Parameter(description = "Sort direction (asc/desc)") @RequestParam(defaultValue = "asc") String sortDir) {

        Sort sort = sortDir.equalsIgnoreCase("desc")
                ? Sort.by(sortBy).descending()
                : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);

        PagedResponse<ContactResponse> pagedResponse = contactService.getAllContactsPaged(addressBookId, pageable);
        return ResponseEntity.ok(ApiResponse.success(pagedResponse, "Contacts retrieved successfully"));
    }

    @GetMapping("/{contactId}")
    @Operation(summary = "Get a specific contact by ID")
    public ResponseEntity<ApiResponse<ContactResponse>> getContactById(
            @PathVariable Long addressBookId,
            @PathVariable Long contactId) {
        ContactResponse contact = contactService.getContactById(addressBookId, contactId);
        return ResponseEntity.ok(ApiResponse.success(contact, "Contact retrieved successfully"));
    }

    @DeleteMapping("/{contactId}")
    @Operation(summary = "Remove a contact from an address book")
    public ResponseEntity<ApiResponse<Void>> removeContact(
            @PathVariable Long addressBookId,
            @PathVariable Long contactId) {
        contactService.removeContact(addressBookId, contactId);
        return ResponseEntity.ok(ApiResponse.success(null, "Contact deleted successfully"));
    }

    @DeleteMapping
    @Operation(summary = "Remove all contacts from an address book")
    public ResponseEntity<ApiResponse<Map<String, Object>>> removeAllContacts(@PathVariable Long addressBookId) {
        int deletedCount = contactService.removeAllContacts(addressBookId);
        Map<String, Object> result = Map.of("deletedCount", deletedCount);
        return ResponseEntity.ok(ApiResponse.success(result, "All contacts deleted successfully"));
    }

    @DeleteMapping("/bulk")
    @Operation(summary = "Remove multiple contacts by IDs")
    public ResponseEntity<ApiResponse<Map<String, Object>>> removeContacts(
            @PathVariable Long addressBookId,
            @Parameter(description = "Comma-separated list of contact IDs") @RequestParam List<Long> ids) {
        int deletedCount = contactService.removeContacts(addressBookId, ids);
        Map<String, Object> result = Map.of(
                "requestedCount", ids.size(),
                "deletedCount", deletedCount);
        return ResponseEntity.ok(ApiResponse.success(result, "Contacts deleted successfully"));
    }

    @PutMapping("/{contactId}")
    @Operation(summary = "Update a contact in an address book")
    public ResponseEntity<ApiResponse<ContactResponse>> updateContact(
            @PathVariable Long addressBookId,
            @PathVariable Long contactId,
            @Valid @RequestBody ContactRequest request) {
        ContactResponse contact = contactService.updateContact(addressBookId, contactId, request);
        return ResponseEntity.ok(ApiResponse.success(contact, "Contact updated successfully"));
    }

    @GetMapping("/unique")
    @Operation(summary = "Get unique contacts across all address books (paginated)")
    public ResponseEntity<ApiResponse<PagedResponse<ContactResponse>>> getUniqueContacts(
            @Parameter(description = "Page number (0-based)") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size (max 100)") @RequestParam(defaultValue = "20") int size,
            @Parameter(description = "Sort field") @RequestParam(defaultValue = "id") String sortBy,
            @Parameter(description = "Sort direction (asc/desc)") @RequestParam(defaultValue = "asc") String sortDir) {

        Sort sort = sortDir.equalsIgnoreCase("desc")
                ? Sort.by(sortBy).descending()
                : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);

        PagedResponse<ContactResponse> pagedResponse = contactService.getUniqueContactsPaged(pageable);
        return ResponseEntity.ok(ApiResponse.success(pagedResponse, "Unique contacts retrieved successfully"));
    }

    @GetMapping("/count")
    @Operation(summary = "Get total contact count in an address book")
    public ResponseEntity<ApiResponse<Long>> getContactCount(@PathVariable Long addressBookId) {
        long count = contactService.getContactCount(addressBookId);
        return ResponseEntity.ok(ApiResponse.success(count, "Contact count retrieved successfully"));
    }
}
