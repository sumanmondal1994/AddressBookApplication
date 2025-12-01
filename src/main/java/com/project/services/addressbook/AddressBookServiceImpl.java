package com.project.services.addressbook;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.project.dto.addressbook.AddressBookRequest;
import com.project.dto.addressbook.AddressBookResponse;
import com.project.dto.response.PagedResponse;
import com.project.entity.addressbook.AddressBook;
import com.project.exception.DuplicateAddressBookException;
import com.project.exception.ResourceNotFoundException;
import com.project.mapper.contact.EntityMapper;
import com.project.repository.addressbook.AddressBookRepository;
import com.project.services.contact.ContactCreationService;
import com.project.util.PaginationHelper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Service implementation for Address Book operations.
 * Follows Single Responsibility Principle - handles only address book
 * operations.
 * Contact creation is delegated to ContactCreationService.
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class AddressBookServiceImpl implements AddressBookService {

	private final AddressBookRepository addressBookRepository;
	private final EntityMapper<AddressBook, AddressBookResponse> addressBookMapper;
	private final ContactCreationService contactCreationService;
	private final PaginationHelper paginationHelper;

	@Override
	public AddressBookResponse createAddressBook(AddressBookRequest request) {
		return createAddressBookInternal(request, false);
	}

	@Override
	public AddressBookResponse createAddressBookWithContacts(AddressBookRequest request) {
		return createAddressBookInternal(request, true);
	}

	private AddressBookResponse createAddressBookInternal(AddressBookRequest request, boolean includeContacts) {
		log.info("Creating address book: {}, includeContacts: {}", request.getName(), includeContacts);

		validateUniqueAddressBookName(request.getName());

		AddressBook addressBook = AddressBook.builder()
				.name(request.getName().trim())
				.description(request.getDescription())
				.build();

		// Delegate contact creation to specialized service (SRP)
		if (includeContacts && request.getContacts() != null && !request.getContacts().isEmpty()) {
			contactCreationService.addContactsToAddressBook(addressBook, request.getContacts());
		}

		AddressBook savedAddressBook = addressBookRepository.save(addressBook);
		return addressBookMapper.mapToResponse(savedAddressBook);
	}

	private void validateUniqueAddressBookName(String name) {
		if (addressBookRepository.existsByName(name)) {
			throw new DuplicateAddressBookException(
					"Address book with name '" + name + "' already exists");
		}
	}

	@Override
	@Transactional(readOnly = true)
	public AddressBookResponse getAddressBookById(Long id) {
		log.info("Fetching address book by id: {}", id);
		AddressBook addressBook = findAddressBookById(id);
		return addressBookMapper.mapToResponse(addressBook);
	}

	@Override
	@Transactional(readOnly = true)
	public AddressBookResponse getAddressBookByName(String name) {
		log.info("Fetching address book by exact name: {}", name);
		AddressBook addressBook = addressBookRepository.findByName(name)
				.orElseThrow(() -> new ResourceNotFoundException("Address book not found with name " + name));
		return addressBookMapper.mapToResponse(addressBook);
	}

	@Override
	@Transactional(readOnly = true)
	public PagedResponse<AddressBookResponse> searchByName(String name, Pageable pageable) {
		log.info("Searching address books by name containing: {}", name);
		Pageable safePageable = paginationHelper.sanitizePageable(pageable);
		Page<AddressBook> page = addressBookRepository.findByNameContainingIgnoreCase(name, safePageable);
		return paginationHelper.createPagedResponse(page, addressBookMapper::mapToResponse);
	}

	@Override
	@Transactional(readOnly = true)
	public List<AddressBookResponse> getAllAddressBooks() {
		log.info("Fetching all address books");
		List<AddressBook> addressBooks = addressBookRepository.findAll();
		return addressBooks.stream()
				.map(addressBookMapper::mapToResponse)
				.collect(Collectors.toList());
	}

	@Override
	@Transactional(readOnly = true)
	public PagedResponse<AddressBookResponse> getAllAddressBooks(Pageable pageable) {
		log.info("Fetching address books - page: {}, size: {}", pageable.getPageNumber(), pageable.getPageSize());

		Pageable safePageable = paginationHelper.sanitizePageable(pageable);
		Page<AddressBook> page = addressBookRepository.findAll(safePageable);

		return paginationHelper.createPagedResponse(page, addressBookMapper::mapToResponse);
	}

	@Override
	public AddressBookResponse updateAddressBook(Long id, AddressBookRequest request) {
		log.info("Updating address book: {}", id);
		AddressBook addressBook = findAddressBookById(id);
		addressBook.setName(request.getName().trim());
		addressBook.setDescription(request.getDescription().trim());
		AddressBook updated = addressBookRepository.save(addressBook);
		return addressBookMapper.mapToResponse(updated);
	}

	@Override
	public void deleteAddressBook(Long id) {
		log.info("Delete address book: {}", id);
		AddressBook addressBook = findAddressBookById(id);
		addressBookRepository.deleteById(addressBook.getId());
	}

	private AddressBook findAddressBookById(Long id) {
		log.debug("Fetching the addressbook with id {}", id);
		return addressBookRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Address book not found with id: " + id));
	}
}
