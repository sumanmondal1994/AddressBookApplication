package com.project.dto.contact;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ContactRequest {

    @NotBlank(message = "Contact name is required")
    private String name;

    @NotBlank(message = "Phone number is required")
    private String phoneNumber;
}