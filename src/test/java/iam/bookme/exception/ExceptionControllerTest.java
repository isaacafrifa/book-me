package iam.bookme.exception;

import org.junit.jupiter.api.BeforeEach;
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
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.util.ArrayList;
import java.util.List;

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

    @BeforeEach
    void setUp() {
        given(request.getDescription(false)).willReturn("uri=/test");
    }

    @Test
    void handleClientNotFoundException_shouldReturnResourceNotFound() {
        // Given
        ResourceNotFoundException ex = new ResourceNotFoundException("Resource not found");
        String expectedPath = "/api/resources/123";
        given(request.getDescription(false)).willReturn("uri=" + expectedPath);

        // When
        ResponseEntity<APIError> response = exceptionController.handleResourceNotFoundException(ex, request);

        // Then
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        APIError errorDetails = response.getBody();
        assertNotNull(errorDetails);
        assertEquals(errorDetails.message(), ex.getMessage());
        assertEquals(errorDetails.path(), expectedPath);
    }

    @Test
    void testHandleClientAlreadyExistsException_shouldReturnResourceAlreadyExists() {
        // Given
        final String RESOURCE_ALREADY_EXISTS = "Resource already exists";
        ResourceAlreadyExistsException ex = new ResourceAlreadyExistsException(RESOURCE_ALREADY_EXISTS);

        // When
        var response = exceptionController.handleResourceAlreadyExistsException(ex, request);

        // Then
        assertNotNull(response);
        final APIError errorDetails = response.getBody();
        assertNotNull(errorDetails);
        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        assertEquals(RESOURCE_ALREADY_EXISTS, errorDetails.message());
        assertEquals("/test", errorDetails.path());
    }

    @Test
    void handleMethodArgumentNotValid_shouldReturnBadRequestWithValidationErrors() {
        // Given
        final MethodArgumentNotValidException exception = mock(MethodArgumentNotValidException.class);
        final BindingResult bindingResult = mock(BindingResult.class);

        final List<FieldError> fieldErrors = new ArrayList<>();
        final FieldError fieldError1 = new FieldError("object", "field 1", "violation message");
        final FieldError fieldError2 = new FieldError("object", "field 2", "violation message");
        fieldErrors.add(fieldError1);
        fieldErrors.add(fieldError2);

        given(bindingResult.getFieldErrors()).willReturn(fieldErrors);
        given(exception.getBindingResult()).willReturn(bindingResult);

        // When
        var response = exceptionController.handleMethodArgumentNotValid(exception, null, HttpStatus.BAD_REQUEST, request);

        // Then
        assertNotNull(response);
        final APIError errorDetails = (APIError) response.getBody();
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(errorDetails);
        assertEquals("field 1 violation message, field 2 violation message", errorDetails.message());
        assertEquals("/test", errorDetails.path());
    }

    @Test
    void handleNoResourceFoundException_shouldReturnNoResourceFound() {
        // Given
        final NoResourceFoundException ex = mock(NoResourceFoundException.class);

        // When
        var response = exceptionController.handleNoResourceFoundException(ex, null, HttpStatus.NOT_FOUND, request);

        // Then
        assertNotNull(response);
        final APIError errorDetails = (APIError) response.getBody();
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNotNull(errorDetails);
        assertEquals(errorDetails.message(), ex.getMessage());
        assertEquals("/test", errorDetails.path());
    }

    @Test
    void testFallBack() {
        // Given
        Exception ex = new Exception("Internal Server Error");

        // When
        ResponseEntity<Object> response = exceptionController.fallBack(ex, request);

        // Then
        assertNotNull(response);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Internal Server Error", ((APIError) response.getBody()).message());
        assertEquals("/test", ((APIError) response.getBody()).path());
    }
}