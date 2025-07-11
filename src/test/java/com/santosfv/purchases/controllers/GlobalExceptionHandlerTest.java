package com.santosfv.purchases.controllers;

import com.santosfv.purchases.ResourceNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GlobalExceptionHandlerTest {

    @InjectMocks
    private GlobalExceptionHandler subject;

    @Mock
    private MethodArgumentNotValidException validationException;

    @Mock
    private BindingResult bindingResult;

    @Test
    void shouldHandleValidationException() {
        List<FieldError> fieldErrors = Arrays.asList(
                new FieldError("purchaseRequest", "description", "Description cannot be blank"),
                new FieldError("purchaseRequest", "amount", "Amount must be positive")
        );

        when(validationException.getBindingResult()).thenReturn(bindingResult);
        when(bindingResult.getAllErrors()).thenReturn(new ArrayList<>(fieldErrors));

        ResponseEntity<GlobalExceptionHandler.ErrorResponse> response = subject.handleValidationExceptions(validationException);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().status()).isEqualTo(400);
        assertThat(response.getBody().errors()).hasSize(2);

        // Verify first error
        GlobalExceptionHandler.Error firstError = response.getBody().errors().get(0);
        assertThat(firstError.field()).isEqualTo("description");
        assertThat(firstError.description()).isEqualTo("Description cannot be blank");

        // Verify second error
        GlobalExceptionHandler.Error secondError = response.getBody().errors().get(1);
        assertThat(secondError.field()).isEqualTo("amount");
        assertThat(secondError.description()).isEqualTo("Amount must be positive");
    }

    @Test
    void shouldHandleGeneralException() {
        RuntimeException exception = new RuntimeException("Something went wrong");

        ResponseEntity<GlobalExceptionHandler.ErrorResponse> response = subject.handleGeneralExceptions(exception);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().status()).isEqualTo(500);
        assertThat(response.getBody().errors()).hasSize(1);

        GlobalExceptionHandler.Error error = response.getBody().errors().get(0);
        assertThat(error.field()).isEqualTo("An unexpected error occurred");
        assertThat(error.description()).isEqualTo("Something went wrong");
    }

    @Test
    void shouldHandleResourceNotFoundException() {
        ResourceNotFoundException exception = new ResourceNotFoundException("Resource not found");

        ResponseEntity<GlobalExceptionHandler.ErrorResponse> response = subject.handleResourceNotFoundException(exception);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().status()).isEqualTo(404);
        assertThat(response.getBody().errors()).hasSize(1);

        GlobalExceptionHandler.Error error = response.getBody().errors().get(0);
        assertThat(error.field()).isEqualTo("Resource not found");
        assertThat(error.description()).isEqualTo("Resource not found");
    }
}