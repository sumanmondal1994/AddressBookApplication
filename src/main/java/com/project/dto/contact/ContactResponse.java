package com.project.dto.contact;

import lombok.*;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ContactResponse {
    private Long id;
    private String name;
    private String phoneNumber;
    private Long addressBookId;
    private String addressBookName;
    private LocalDateTime createdAt;
}