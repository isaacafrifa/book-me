package iam.bookme.exception;

import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.util.List;

@RestControllerAdvice
public class ExceptionController extends ResponseEntityExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<APIError> handleClientNotFoundException(ResourceNotFoundException ex, WebRequest request) {
        APIError errorDetails = new APIError(ex.getMessage(),
                extractPath(request.getDescription(false)));
        return new ResponseEntity<>(errorDetails, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(ResourceAlreadyExistsException.class)
    public ResponseEntity<APIError> handleClientAlreadyExistsException(ResourceAlreadyExistsException ex, WebRequest request) {
        APIError errorDetails = new APIError(ex.getMessage(),
                extractPath(request.getDescription(false)));
        return new ResponseEntity<>(errorDetails, HttpStatus.CONFLICT);
    }

    /* handle validation exceptions */
    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {
        List<String> errors = ex.getBindingResult().getFieldErrors().stream()
                .map(DefaultMessageSourceResolvable::getDefaultMessage)
                .toList();
        String errorString = String.join(", ", errors);
        APIError apiError = new APIError(errorString, extractPath(request.getDescription(false)));
        return handleExceptionInternal(ex, apiError, headers, status, request);
    }

    /* This exception is thrown when a requested resource cannot be located e.g. user enters an invalid URL path. */
    @ExceptionHandler({NoResourceFoundException.class})
    public ResponseEntity<Object> handleNoResourceFoundException(NoResourceFoundException ex, WebRequest request) {
        APIError apiError = new APIError(
                ex.getLocalizedMessage(), extractPath(request.getDescription(false)));
        return new ResponseEntity<>(apiError, HttpStatus.NOT_FOUND);
    }

    /* This exception hanldes missing or unexpected required content type e.g. request requires 'application/json and receives 'text' */
    @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
    public ResponseEntity<APIError> handleMediaTypeNotSupportedException(HttpMediaTypeNotSupportedException ex, WebRequest request) {
        APIError apiError = new APIError(
                ex.getLocalizedMessage(), extractPath(request.getDescription(false)));
        return new ResponseEntity<>(apiError, HttpStatus.UNSUPPORTED_MEDIA_TYPE);
    }

    @ExceptionHandler({Exception.class})
    public ResponseEntity<Object> fallBack(Exception ex, WebRequest request) {
        APIError apiError = new APIError(
                ex.getLocalizedMessage(), extractPath(request.getDescription(false)));
        return new ResponseEntity<>(apiError, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    private String extractPath(String originalPath) {
        // Assuming "uri=" is always present, find its position and extract the path by removing "uri="
        int uriIndex = originalPath.indexOf("uri=");
        if (uriIndex != -1) {
            return originalPath.substring(uriIndex + 4);
        } else {
            return originalPath;
        }
    }
}
