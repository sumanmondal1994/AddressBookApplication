package com.addressbook.project.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.addressbook.project.dto.AddressBookRequest;
import com.addressbook.project.dto.AddressBookResponse;
import com.addressbook.project.dto.ApiResponse;
import com.addressbook.project.dto.PagedResponse;
import com.addressbook.project.services.AddressBookService;

@RestController
@RequestMapping("/api/v1/addressbooks")
@RequiredArgsConstructor
@Tag(name = "Address Books", description = "Address book management APIs")
public class AddressBookController {

	private final AddressBookService addressBookService;

	@PostMapping
	@Operation(summary = "Create a new address book")
	public ResponseEntity<ApiResponse<AddressBookResponse>> createAddressBook(
			@Valid @RequestBody AddressBookRequest req) {
		AddressBookResponse addressBook = addressBookService.createAddressBook(req);
		return new ResponseEntity<>(
				ApiResponse.success(addressBook, "Address book created successfully"),
				HttpStatus.CREATED);
	}

	@GetMapping
	@Operation(summary = "Get all address books (paginated)")
	public ResponseEntity<ApiResponse<PagedResponse<AddressBookResponse>>> getAllAddressBooks(
			@Parameter(description = "Page number (0-based)") @RequestParam(defaultValue = "0") int page,
			@Parameter(description = "Page size (max 100)") @RequestParam(defaultValue = "20") int size,
			@Parameter(description = "Sort field") @RequestParam(defaultValue = "id") String sortBy,
			@Parameter(description = "Sort direction (asc/desc)") @RequestParam(defaultValue = "asc") String sortDir) {

		Sort sort = sortDir.equalsIgnoreCase("desc")
				? Sort.by(sortBy).descending()
				: Sort.by(sortBy).ascending();
		Pageable pageable = PageRequest.of(page, size, sort);

		PagedResponse<AddressBookResponse> pagedResponse = addressBookService.getAllAddressBooks(pageable);
		return ResponseEntity.ok(ApiResponse.success(pagedResponse, "Address books retrieved successfully"));
	}

	@GetMapping("/{id}")
	@Operation(summary = "Get address book by ID")
	public ResponseEntity<ApiResponse<AddressBookResponse>> getAddressBookById(@PathVariable Long id) {
		AddressBookResponse addressBook = addressBookService.getAddressBookById(id);
		return ResponseEntity.ok(ApiResponse.success(addressBook, "Address book retrieved successfully"));
	}

	@GetMapping("/name/{name}")
	@Operation(summary = "Get address book by exact name")
	public ResponseEntity<ApiResponse<AddressBookResponse>> getAddressBookByName(@PathVariable String name) {
		AddressBookResponse addressBook = addressBookService.getAddressBookByName(name);
		return ResponseEntity.ok(ApiResponse.success(addressBook, "Address book retrieved successfully"));
	}

	@GetMapping("/search")
	@Operation(summary = "Search address books by partial name (case-insensitive)")
	public ResponseEntity<ApiResponse<PagedResponse<AddressBookResponse>>> searchByName(
			@Parameter(description = "Name to search for (partial match)") @RequestParam String name,
			@Parameter(description = "Page number (0-based)") @RequestParam(defaultValue = "0") int page,
			@Parameter(description = "Page size (max 100)") @RequestParam(defaultValue = "20") int size,
			@Parameter(description = "Sort field") @RequestParam(defaultValue = "name") String sortBy,
			@Parameter(description = "Sort direction (asc/desc)") @RequestParam(defaultValue = "asc") String sortDir) {

		Sort sort = sortDir.equalsIgnoreCase("desc")
				? Sort.by(sortBy).descending()
				: Sort.by(sortBy).ascending();
		Pageable pageable = PageRequest.of(page, size, sort);

		PagedResponse<AddressBookResponse> pagedResponse = addressBookService.searchByName(name, pageable);
		return ResponseEntity.ok(ApiResponse.success(pagedResponse, "Search results retrieved successfully"));
	}

	@PutMapping("/{id}")
	@Operation(summary = "Update an address book")
	public ResponseEntity<ApiResponse<AddressBookResponse>> updateAddressBook(
			@PathVariable Long id,
			@Valid @RequestBody AddressBookRequest request) {
		AddressBookResponse addressBook = addressBookService.updateAddressBook(id, request);
		return ResponseEntity.ok(ApiResponse.success(addressBook, "Address book updated successfully"));
	}

	@DeleteMapping("/{id}")
	@Operation(summary = "Delete an address book")
	public ResponseEntity<ApiResponse<Void>> deleteAddressBook(@PathVariable Long id) {
		addressBookService.deleteAddressBook(id);
		return ResponseEntity.ok(ApiResponse.success(null, "Address book deleted successfully"));
	}
}
