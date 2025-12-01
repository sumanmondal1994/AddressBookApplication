package com.project.services.contact;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.project.dto.contact.ContactRequest;
import com.project.dto.contact.ContactResponse;
import com.project.dto.response.PagedResponse;
import com.project.entity.addressbook.AddressBook;
import com.project.entity.contact.Contact;
import com.project.exception.DuplicateContactException;
import com.project.exception.ResourceNotFoundException;
import com.project.mapper.contact.EntityMapper;
import com.project.repository.addressbook.AddressBookRepository;
import com.project.repository.contact.ContactRepository;
import com.project.util.PaginationHelper;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class ContactServiceImpl implements ContactService {

    private final ContactRepository contactRepository;
    private final AddressBookRepository addressBookRepository;
    private final EntityMapper<Contact, ContactResponse> contactMapper;
    private final PaginationHelper paginationHelper;

    @Override
    public ContactResponse addContact(Long addressBookId, ContactRequest request) {
        log.info("Adding contact to address book: {}", addressBookId);

        AddressBook addressBook = findAddressBookById(addressBookId);
        validateUniquePhoneNumber(request.getPhoneNumber(), addressBookId);

        Contact contact = Contact.builder()
                .name(request.getName())
                .phoneNumber(request.getPhoneNumber())
                .addressBook(addressBook)
                .build();

        Contact savedContact = contactRepository.save(contact);
        return contactMapper.mapToResponse(savedContact);
    }

    private void validateUniquePhoneNumber(String phoneNumber, Long addressBookId) {
        if (contactRepository.existsByPhoneNumberAndAddressBookId(phoneNumber, addressBookId)) {
            throw new DuplicateContactException(
                    "Contact with phone number " + phoneNumber + " already exists in this address book");
        }
    }

    @Override
    @Transactional(readOnly = true)
    public ContactResponse getContactById(Long addressBookId, Long contactId) {
        log.info("Fetching contact {} from address book {}", contactId, addressBookId);
        Contact contact = contactRepository.findByIdAndAddressBookId(contactId, addressBookId)
                .orElseThrow(() -> new ResourceNotFoundException("Contact not found with id: " + contactId));
        return contactMapper.mapToResponse(contact);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ContactResponse> getAllContacts(Long addressBookId) {
        log.warn("Using non-paginated getAllContacts - consider using paginated version");
        findAddressBookById(addressBookId);

        Pageable limitedPage = PageRequest.of(0, paginationHelper.getMaxPageSize());
        return contactRepository.findByAddressBookId(addressBookId, limitedPage)
                .getContent().stream()
                .map(contactMapper::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public PagedResponse<ContactResponse> getAllContactsPaged(Long addressBookId, Pageable pageable) {
        log.info("Fetching contacts for address book: {} - page: {}, size: {}",
                addressBookId, pageable.getPageNumber(), pageable.getPageSize());

        findAddressBookById(addressBookId);

        Pageable safePageable = paginationHelper.sanitizePageable(pageable);
        Page<Contact> page = contactRepository.findByAddressBookId(addressBookId, safePageable);

        return paginationHelper.createPagedResponse(page, contactMapper::mapToResponse);
    }

    @Override
    public void removeContact(Long addressBookId, Long contactId) {
        log.info("Removing contact {} from address book {}", contactId, addressBookId);
        Contact contact = contactRepository.findByIdAndAddressBookId(contactId, addressBookId)
                .orElseThrow(() -> new ResourceNotFoundException("Contact not found with id: " + contactId));
        contactRepository.delete(contact);
    }

    @Override
    public ContactResponse updateContact(Long addressBookId, Long contactId, ContactRequest request) {
        log.info("Updating contact {} in address book {}", contactId, addressBookId);

        Contact existingContact = contactRepository.findByIdAndAddressBookId(contactId, addressBookId)
                .orElseThrow(() -> new ResourceNotFoundException("Contact not found with id: " + contactId));

        // Check if phone number is being changed and if new phone number already exists
        if (!existingContact.getPhoneNumber().equals(request.getPhoneNumber())) {
            validateUniquePhoneNumber(request.getPhoneNumber(), addressBookId);
        }

        existingContact.setName(request.getName());
        existingContact.setPhoneNumber(request.getPhoneNumber());

        Contact updatedContact = contactRepository.save(existingContact);
        return contactMapper.mapToResponse(updatedContact);
    }

    @Override
    public int removeContacts(Long addressBookId, List<Long> contactIds) {
        log.info("Removing {} contacts from address book {}", contactIds.size(), addressBookId);
        findAddressBookById(addressBookId);

        if (contactIds == null || contactIds.isEmpty()) {
            return 0;
        }

        // Find contacts that exist in this address book
        List<Contact> contactsToDelete = contactRepository.findByIdInAndAddressBookId(contactIds, addressBookId);
        int count = contactsToDelete.size();

        if (!contactsToDelete.isEmpty()) {
            contactRepository.deleteAll(contactsToDelete);
        }

        log.info("Deleted {} contacts from address book {}", count, addressBookId);
        return count;
    }

    @Override
    public int removeAllContacts(Long addressBookId) {
        log.info("Removing all contacts from address book {}", addressBookId);
        findAddressBookById(addressBookId);

        long count = contactRepository.countByAddressBookId(addressBookId);
        contactRepository.deleteByAddressBookId(addressBookId);

        log.info("Deleted {} contacts from address book {}", count, addressBookId);
        return (int) count;
    }

    @Override
    @Transactional(readOnly = true)
    public List<ContactResponse> getUniqueContactsAcrossAllAddressBooks() {
        log.warn("Using non-paginated getUniqueContacts - consider using paginated version");

        Pageable limitedPage = PageRequest.of(0, paginationHelper.getMaxPageSize());
        Page<Contact> page = contactRepository.findUniqueContacts(limitedPage);

        return page.getContent().stream()
                .map(contactMapper::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public PagedResponse<ContactResponse> getUniqueContactsPaged(Pageable pageable) {
        log.info("Fetching unique contacts - page: {}, size: {}",
                pageable.getPageNumber(), pageable.getPageSize());

        Pageable safePageable = paginationHelper.sanitizePageable(pageable);
        Page<Contact> page = contactRepository.findUniqueContacts(safePageable);

        return paginationHelper.createPagedResponse(page, contactMapper::mapToResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public long getContactCount(Long addressBookId) {
        findAddressBookById(addressBookId);
        return contactRepository.countByAddressBookId(addressBookId);
    }

    @Override
    @Transactional(readOnly = true)
    public long getUniqueContactCount() {
        return contactRepository.countDistinctPhoneNumbers();
    }

    private AddressBook findAddressBookById(Long id) {
        return addressBookRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Address book not found with id: " + id));
    }
}
