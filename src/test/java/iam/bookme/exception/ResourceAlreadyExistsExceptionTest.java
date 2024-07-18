package iam.bookme.exception;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DisplayName("Running ResourceAlreadyExistsException tests")
class ResourceAlreadyExistsExceptionTest {
    private static final String MESSAGE = "Resource already exists";

    @Test
    void testCreateWithMessage() {
        var ex = new ResourceAlreadyExistsException(MESSAGE);
        assertEquals(MESSAGE, ex.getMessage());
    }
}