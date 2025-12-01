package com.addressbook.project.dto;

import lombok.*;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AddressBookResponse {
    private Long id;
    private String name;
    private String description;
    private Integer contactCount;
    private List<ContactResponse> contacts;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}