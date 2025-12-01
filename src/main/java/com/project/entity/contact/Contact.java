package com.project.entity.contact;

import jakarta.persistence.*;
import jakarta.validation.constraints.Pattern;
import lombok.*;
import java.time.LocalDateTime;

import com.project.entity.addressbook.AddressBook;

@Entity
@Table(name = "contacts", uniqueConstraints = @UniqueConstraint(columnNames = { "phone_number",
        "address_book_id" }), indexes = {
                @Index(name = "idx_contact_phone", columnList = "phone_number"),
                @Index(name = "idx_contact_addressbook", columnList = "address_book_id"),
                @Index(name = "idx_contact_name", columnList = "name")
        })
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Contact {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(name = "phone_number", nullable = false)
    @Pattern(
            regexp = "^\\+?[0-9\\s\\-()]{6,20}$",
            message = "Phone number must be between 6 and 20 characters and can only contain digits, spaces, hyphens, parentheses, and an optional leading plus sign (+)."
        )
    private String phoneNumber;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "address_book_id", nullable = false)
    private AddressBook addressBook;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (!(o instanceof Contact))
            return false;
        Contact contact = (Contact) o;
        return phoneNumber != null && phoneNumber.equals(contact.phoneNumber);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

}