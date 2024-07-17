package iam.bookme.exception;

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
import org.springframework.web.context.request.WebRequest;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

@ExtendWith(MockitoExtension.class)
class ExceptionControllerTest {

    @Mock
    private WebRequest request;
    @InjectMocks
    private ExceptionController exceptionController;

    @Test
    void handleClientNotFoundException_shouldReturnResourceNotFound() {
        // Given
        ResourceNotFound ex = new ResourceNotFound("Resource not found");
        String expectedPath = "/api/resources/123";
        given(request.getDescription(false)).willReturn("uri=" + expectedPath);

        // When
        ResponseEntity<APIError> response = exceptionController.handleClientNotFoundException(ex, request);

        // Then
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        APIError errorDetails = response.getBody();
        assertNotNull(errorDetails);
        assertEquals(errorDetails.message(), ex.getMessage());
        assertEquals(errorDetails.path(), expectedPath);
    }

    @Test
    void testHandleClientAlreadyExistsException_shouldReturnResourceAlreadyExists() {
        final String RESOURCE_ALREADY_EXISTS = "Resource already exists";
        ResourceAlreadyExists ex = new ResourceAlreadyExists(RESOURCE_ALREADY_EXISTS);
        given(request.getDescription(false)).willReturn("uri=/test");

        var response = exceptionController.handleClientAlreadyExistsException(ex, request);

        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        APIError errorDetails = response.getBody();
        assertEquals(RESOURCE_ALREADY_EXISTS, errorDetails.message());
        assertEquals("/test", errorDetails.path());
    }

    @Test
    void handleMethodArgumentNotValid_shouldReturnBadRequestWithValidationErrors() {
        // Given
        BindingResult bindingResult = mock(BindingResult.class);
        FieldError fieldError = new FieldError("objectName", "fieldName", "defaultMessage");
        given(bindingResult.getFieldErrors()).willReturn(Collections.singletonList(fieldError));

        MethodArgumentNotValidException ex = new MethodArgumentNotValidException(null, bindingResult);
        given(request.getDescription(false)).willReturn("uri=/test");

        // When
        var response = exceptionController.handleMethodArgumentNotValid(ex, null, HttpStatus.BAD_REQUEST, request);

        // Then
        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        APIError errorDetails = (APIError) response.getBody();
        assertNotNull(errorDetails);
        assertEquals("defaultMessage", errorDetails.message());
        assertEquals("/test", ((APIError) response.getBody()).path());
    }

    @Test
    void testHandleAll() {
        // Given
        Exception ex = new Exception("Internal Server Error");
        given(request.getDescription(false)).willReturn("uri=/test");

        // When
        ResponseEntity<Object> response = exceptionController.handleAll(ex, request);

        // Then
        assertNotNull(response);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Internal Server Error", ((APIError) response.getBody()).message());
        assertEquals("/test", ((APIError) response.getBody()).path());
    }
}