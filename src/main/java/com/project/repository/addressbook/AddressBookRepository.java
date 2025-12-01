package com.project.repository.addressbook;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
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
}
