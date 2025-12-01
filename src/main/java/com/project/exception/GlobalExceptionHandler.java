package com.project.exception;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import com.project.dto.response.ApiResponse;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

	@ExceptionHandler(ResourceNotFoundException.class)
	public ResponseEntity<ApiResponse<Void>> handleResourceNotFoundException(ResourceNotFoundException ex,
			WebRequest request) {
		log.error("Resource not found: {}", ex.getMessage());

		String path = request.getDescription(false).replace("uri=", "");
		ApiResponse<Void> response = ApiResponse.error(ex.getMessage(), path);

		return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
	}

	@ExceptionHandler(DuplicateContactException.class)
	public ResponseEntity<ApiResponse<Void>> handleDuplicateContactException(
			DuplicateContactException ex, WebRequest request) {
		log.error("Duplicate contact: {}", ex.getMessage());

		String path = request.getDescription(false).replace("uri=", "");
		ApiResponse<Void> response = ApiResponse.error(ex.getMessage(), path);

		return new ResponseEntity<>(response, HttpStatus.CONFLICT);
	}

	@ExceptionHandler(DuplicateAddressBookException.class)
	public ResponseEntity<ApiResponse<Void>> handleDuplicateAddressBookException(
			DuplicateAddressBookException ex, WebRequest request) {
		log.error("Duplicate address book: {}", ex.getMessage());

		String path = request.getDescription(false).replace("uri=", "");
		ApiResponse<Void> response = ApiResponse.error(ex.getMessage(), path);

		return new ResponseEntity<>(response, HttpStatus.CONFLICT);
	}

	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<ApiResponse<Void>> handleValidationExceptions(
			MethodArgumentNotValidException ex,
			WebRequest request) {

		log.error("Validation failed: {}", ex.getBindingResult().getErrorCount() + " error(s)");

		Map<String, String> fieldErrors = new HashMap<>();
		for (FieldError error : ex.getBindingResult().getFieldErrors()) {
			String fieldName = error.getField();
			String errorMessage = error.getDefaultMessage();
			if (fieldErrors.containsKey(fieldName)) {
				fieldErrors.put(fieldName,
						fieldErrors.get(fieldName) + "; " + errorMessage);
			} else {
				fieldErrors.put(fieldName, errorMessage);
			}

			log.debug("Field error - {}: {}", fieldName, errorMessage);
		}

		String path = request.getDescription(false).replace("uri=", "");
		ApiResponse<Void> response = ApiResponse.validationError(
				"Validation failed for one or more fields",
				fieldErrors,
				path);

		return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
	}

	@ExceptionHandler(ConstraintViolationException.class)
	public ResponseEntity<ApiResponse<Void>> handleConstraintViolationException(
			ConstraintViolationException ex,
			WebRequest request) {

		log.error("Entity constraint violation: {}", ex.getMessage());

		Map<String, String> fieldErrors = new HashMap<>();
		for (ConstraintViolation<?> violation : ex.getConstraintViolations()) {
			String propertyPath = violation.getPropertyPath().toString();
			String message = violation.getMessage();
			if (fieldErrors.containsKey(propertyPath)) {
				fieldErrors.put(propertyPath,
						fieldErrors.get(propertyPath) + "; " + message);
			} else {
				fieldErrors.put(propertyPath, message);
			}

			log.debug("Constraint violation - {}: {}", propertyPath, message);
		}

		String path = request.getDescription(false).replace("uri=", "");
		ApiResponse<Void> response = ApiResponse.validationError(
				"Entity validation failed",
				fieldErrors,
				path);

		return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
	}

	@ExceptionHandler(Exception.class)
	public ResponseEntity<ApiResponse<Void>> handleGenericException(Exception ex, WebRequest request) {
		log.error("Unexpected error: {}", ex.getMessage(), ex);

		String path = request.getDescription(false).replace("uri=", "");
		ApiResponse<Void> response = ApiResponse.error("An unexpected error occurred: " + ex.getLocalizedMessage(),
				path);

		return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
	}
}
