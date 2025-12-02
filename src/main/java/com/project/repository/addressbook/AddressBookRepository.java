package com.project.repository.addressbook;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.project.entity.addressbook.AddressBook;

@Repository
public interface AddressBookRepository extends JpaRepository<AddressBook, Long> {

	Optional<AddressBook> findByName(String name);

	boolean existsByName(String name);

	Page<AddressBook> findAll(Pageable pageable);

	Page<AddressBook> findByNameContainingIgnoreCase(String name, Pageable pageable);

	@Query("SELECT COUNT(a) FROM AddressBook a")
	long countAllAddressBooks();
	
    @Query("SELECT DISTINCT ab FROM AddressBook ab LEFT JOIN FETCH ab.contacts ORDER BY ab.id")
    Page<AddressBook> findAllWithContacts(Pageable pageable);

    @Query("SELECT ab FROM AddressBook ab LEFT JOIN FETCH ab.contacts WHERE ab.id = :id")
    Optional<AddressBook> findByIdWithContacts(@Param("id") Long id);

    @Query("SELECT ab FROM AddressBook ab LEFT JOIN FETCH ab.contacts WHERE ab.name = :name")
    Optional<AddressBook> findByNameWithContacts(@Param("name") String name);

    @Query("SELECT DISTINCT ab FROM AddressBook ab LEFT JOIN FETCH ab.contacts " +
           "WHERE LOWER(ab.name) LIKE LOWER(CONCAT('%', :name, '%')) ORDER BY ab.id")
    Page<AddressBook> findByNameContainingIgnoreCaseWithContacts(@Param("name") String name, Pageable pageable);

	
}
