package com.project.dto.addressbook;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;
import java.util.List;

import com.project.dto.contact.ContactRequest;



@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AddressBookRequest {
    
    @NotBlank(message = "Address book name is required")
    @Size(min = 2, max = 100, message = "Name must be between 2 and 100 characters")
    private String name;
    
    @Size(max = 200, message = "Description cannot exceed 200 characters")
    private String description;
    
    @Valid
    private List<ContactRequest> contacts;  
}
