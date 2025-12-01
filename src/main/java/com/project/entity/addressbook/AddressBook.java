package com.project.entity.addressbook;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

import com.project.entity.contact.Contact;

@Entity
@Table(name = "addressbooks", indexes = {
	    @Index(name = "idx_addressbook_name", columnList = "name"),
	    @Index(name = "idx_addressbook_created_at", columnList = "created_at")
	})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AddressBook {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false, unique = true)
    private String name;
    
    @Column(length = 500)
    private String description;
    
    @OneToMany(mappedBy = "addressBook", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private Set<Contact> contacts = new HashSet<>();
    
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
    
    public void addContact(Contact contact) {
        contacts.add(contact);
        contact.setAddressBook(this);
    }
    
    public void removeContact(Contact contact) {
        contacts.remove(contact);
        contact.setAddressBook(null);
    }
}
