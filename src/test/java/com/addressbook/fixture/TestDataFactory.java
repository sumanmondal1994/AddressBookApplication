package com.addressbook.fixture;

import com.project.dto.addressbook.AddressBookRequest;
import com.project.dto.addressbook.AddressBookResponse;
import com.project.dto.contact.ContactRequest;
import com.project.dto.contact.ContactResponse;
import com.project.entity.addressbook.AddressBook;
import com.project.entity.contact.Contact;

import net.datafaker.Faker;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class TestDataFactory {

    private static final Faker faker = new Faker();

    public static AddressBook createAddressBook() {
        return AddressBook.builder()
                .id(faker.number().randomNumber())
                .name(faker.company().name())
                .description(faker.lorem().sentence())
                .contacts(new HashSet<>())
                .build();
    }

    public static AddressBook createAddressBook(Long id) {
        return AddressBook.builder()
                .id(id)
                .name(faker.company().name())
                .description(faker.lorem().sentence())
                .contacts(new HashSet<>())
                .build();
    }

    public static AddressBook createAddressBookWithName(String name) {
        return AddressBook.builder()
                .id(faker.number().randomNumber())
                .name(name)
                .description(faker.lorem().sentence())
                .contacts(new HashSet<>())
                .build();
    }

    public static AddressBook createAddressBook(Long id, String name) {
        return AddressBook.builder()
                .id(id)
                .name(name)
                .description(faker.lorem().sentence())
                .contacts(new HashSet<>())
                .build();
    }

    public static AddressBookRequest createAddressBookRequest() {
        return AddressBookRequest.builder()
                .name(faker.company().name())
                .description(faker.lorem().sentence())
                .build();
    }

    public static AddressBookRequest createAddressBookRequest(String name) {
        return AddressBookRequest.builder()
                .name(name)
                .description(faker.lorem().sentence())
                .build();
    }

    public static AddressBookRequest createAddressBookRequest(String name, String description) {
        return AddressBookRequest.builder()
                .name(name)
                .description(description)
                .build();
    }

    public static AddressBookResponse createAddressBookResponse() {
        return AddressBookResponse.builder()
                .id(faker.number().randomNumber())
                .name(faker.company().name())
                .description(faker.lorem().sentence())
                .contactCount(0)
                .contacts(new ArrayList<>())
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }

    public static AddressBookResponse createAddressBookResponse(Long id) {
        return AddressBookResponse.builder()
                .id(id)
                .name(faker.company().name())
                .description(faker.lorem().sentence())
                .contactCount(0)
                .contacts(new ArrayList<>())
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }

    public static AddressBookResponse createAddressBookResponse(Long id, String name) {
        return AddressBookResponse.builder()
                .id(id)
                .name(name)
                .description(faker.lorem().sentence())
                .contactCount(0)
                .contacts(new ArrayList<>())
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }

    public static AddressBookResponse createAddressBookResponse(Long id, String name, String description) {
        return AddressBookResponse.builder()
                .id(id)
                .name(name)
                .description(description)
                .contactCount(0)
                .contacts(new ArrayList<>())
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }

    /**
     * Creates a list of AddressBook entities.
     */
    public static List<AddressBook> createAddressBooks(int count) {
        return IntStream.range(0, count)
                .mapToObj(i -> createAddressBook((long) (i + 1)))
                .collect(Collectors.toList());
    }

    /**
     * Creates a Contact entity with random data.
     */
    public static Contact createContact() {
        return Contact.builder()
                .id(faker.number().randomNumber())
                .name(faker.name().fullName())
                .phoneNumber(generateAustralianPhoneNumber())
                .build();
    }

    public static Contact createContact(Long id) {
        return Contact.builder()
                .id(id)
                .name(faker.name().fullName())
                .phoneNumber(generateAustralianPhoneNumber())
                .build();
    }

    public static Contact createContact(Long id, AddressBook addressBook) {
        return Contact.builder()
                .id(id)
                .name(faker.name().fullName())
                .phoneNumber(generateAustralianPhoneNumber())
                .addressBook(addressBook)
                .build();
    }

    public static Contact createContact(String name, String phoneNumber) {
        return Contact.builder()
                .id(faker.number().randomNumber())
                .name(name)
                .phoneNumber(phoneNumber)
                .build();
    }

    public static Contact createContact(Long id, String name, String phoneNumber, AddressBook addressBook) {
        return Contact.builder()
                .id(id)
                .name(name)
                .phoneNumber(phoneNumber)
                .addressBook(addressBook)
                .build();
    }

    public static ContactRequest createContactRequest() {
        return ContactRequest.builder()
                .name(faker.name().fullName())
                .phoneNumber(generateAustralianPhoneNumber())
                .build();
    }

    public static ContactRequest createContactRequest(String name, String phoneNumber) {
        return ContactRequest.builder()
                .name(name)
                .phoneNumber(phoneNumber)
                .build();
    }

    public static ContactResponse createContactResponse() {
        return ContactResponse.builder()
                .id(faker.number().randomNumber())
                .name(faker.name().fullName())
                .phoneNumber(generateAustralianPhoneNumber())
                .createdAt(LocalDateTime.now())

                .build();
    }

    public static ContactResponse createContactResponse(Long id) {
        return ContactResponse.builder()
                .id(id)
                .name(faker.name().fullName())
                .phoneNumber(generateAustralianPhoneNumber())
                .createdAt(LocalDateTime.now())

                .build();
    }

    public static ContactResponse createContactResponse(Long id, Long addressBookId) {
        return ContactResponse.builder()
                .id(id)
                .name(faker.name().fullName())
                .phoneNumber(generateAustralianPhoneNumber())
                .addressBookId(addressBookId)
                .createdAt(LocalDateTime.now())

                .build();
    }

    public static ContactResponse createContactResponse(Long id, String name, String phoneNumber) {
        return ContactResponse.builder()
                .id(id)
                .name(name)
                .phoneNumber(phoneNumber)
                .createdAt(LocalDateTime.now())

                .build();
    }

    public static ContactResponse createContactResponse(Long id, String name, String phoneNumber, Long addressBookId) {
        return ContactResponse.builder()
                .id(id)
                .name(name)
                .phoneNumber(phoneNumber)
                .addressBookId(addressBookId)
                .createdAt(LocalDateTime.now())

                .build();
    }

    public static List<Contact> createContacts(int count, AddressBook addressBook) {
        return IntStream.range(0, count)
                .mapToObj(i -> createContact((long) (i + 1), addressBook))
                .collect(Collectors.toList());
    }

    public static List<ContactResponse> createContactResponses(int count) {
        return IntStream.range(0, count)
                .mapToObj(i -> createContactResponse((long) (i + 1)))
                .collect(Collectors.toList());
    }

    public static String generateAustralianPhoneNumber() {
        return "+61" + faker.number().digits(9);
    }

    public static String generateName() {
        return faker.name().fullName();
    }

    public static String generateFirstName() {
        return faker.name().firstName();
    }

    public static String generateAddressBookName() {
        return faker.company().name();
    }

    public static String generateDescription() {
        return faker.lorem().sentence();
    }

    public static Faker getFaker() {
        return faker;
    }
}
