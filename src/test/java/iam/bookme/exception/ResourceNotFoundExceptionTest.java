package iam.bookme.exception;


import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DisplayName("Running ResourceNotFoundException tests")
class ResourceNotFoundExceptionTest {
    private static final String MESSAGE = "message";

    @Test
    void testConstructor() {
        final ResourceNotFoundException exception = new ResourceNotFoundException(MESSAGE);
        assertEquals(MESSAGE, exception.getMessage());
    }
}