package com.addressbook.project.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;
import java.time.LocalDateTime;
import java.util.Map;

/**
 * Unified API response wrapper for consistent response structure.
 * All endpoints return this wrapper for both success and error cases.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiResponse<T> {

    private boolean success;
    private String message;
    private T response;
    private LocalDateTime timestamp;
    private String path;

    // For validation errors only
    private Map<String, String> errors;

    /**
     * Create a success response with data
     */
    public static <T> ApiResponse<T> success(T response, String message) {
        return ApiResponse.<T>builder()
                .success(true)
                .message(message)
                .response(response)
                .timestamp(LocalDateTime.now())
                .build();
    }

    /**
     * Create a success response with data and path
     */
    public static <T> ApiResponse<T> success(T response, String message, String path) {
        return ApiResponse.<T>builder()
                .success(true)
                .message(message)
                .response(response)
                .timestamp(LocalDateTime.now())
                .path(path)
                .build();
    }

    /**
     * Create an error response
     */
    public static <T> ApiResponse<T> error(String message, String path) {
        return ApiResponse.<T>builder()
                .success(false)
                .message(message)
                .timestamp(LocalDateTime.now())
                .path(path)
                .build();
    }

    /**
     * Create a validation error response
     */
    public static <T> ApiResponse<T> validationError(String message, Map<String, String> errors, String path) {
        return ApiResponse.<T>builder()
                .success(false)
                .message(message)
                .errors(errors)
                .timestamp(LocalDateTime.now())
                .path(path)
                .build();
    }
}
