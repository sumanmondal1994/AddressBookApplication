package com.addressbook.project.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.addressbook.project.entity.Contact;

@Repository
public interface ContactRepository extends JpaRepository<Contact, Long> {

        List<Contact> findByAddressBookId(Long addressBookId);

        Page<Contact> findByAddressBookId(Long addressBookId, Pageable pageable);

        Optional<Contact> findByIdAndAddressBookId(Long id, Long addressBookId);

        boolean existsByPhoneNumberAndAddressBookId(String phoneNumber, Long addressBookId);

        long countByAddressBookId(Long addressBookId);

        @Query("SELECT DISTINCT c FROM Contact c")
        List<Contact> findAllUniqueContacts();

        @Query("SELECT c FROM Contact c WHERE c.id IN " +
                        "(SELECT MIN(c2.id) FROM Contact c2 GROUP BY c2.phoneNumber)")
        Page<Contact> findUniqueContacts(Pageable pageable);

        @Query("SELECT COUNT(DISTINCT c.phoneNumber) FROM Contact c")
        long countDistinctPhoneNumbers();

        Page<Contact> findByNameContainingIgnoreCase(String name, Pageable pageable);

        Page<Contact> findByAddressBookIdAndNameContainingIgnoreCase(
                        Long addressBookId, String name, Pageable pageable);

        void deleteByAddressBookId(Long addressBookId);

        void deleteByIdInAndAddressBookId(List<Long> ids, Long addressBookId);

        List<Contact> findByIdInAndAddressBookId(List<Long> ids, Long addressBookId);
}
