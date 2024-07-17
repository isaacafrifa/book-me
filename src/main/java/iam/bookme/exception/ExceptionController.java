package iam.bookme.exception;

import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.List;

@RestControllerAdvice
public class ExceptionController extends ResponseEntityExceptionHandler {

    @ExceptionHandler(ResourceNotFound.class)
    public ResponseEntity<APIError> handleClientNotFoundException(ResourceNotFound ex, WebRequest request) {
        APIError errorDetails = new APIError(ex.getMessage(),
                extractPath(request.getDescription(false)));
        return new ResponseEntity<>(errorDetails, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(ResourceAlreadyExists.class)
    public ResponseEntity<APIError> handleClientAlreadyExistsException(ResourceAlreadyExists ex, WebRequest request) {
        APIError errorDetails = new APIError(ex.getMessage(),
                extractPath(request.getDescription(false)));
        return new ResponseEntity<>(errorDetails, HttpStatus.CONFLICT);
    }

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {
        List<String> errors = ex.getBindingResult().getFieldErrors().stream()
                .map(DefaultMessageSourceResolvable::getDefaultMessage)
                .toList();
        String errorString = String.join(", ", errors);
        APIError apiError = new APIError(errorString, extractPath(request.getDescription(false)));
        return handleExceptionInternal(ex, apiError, headers, status, request);
    }

    @ExceptionHandler({Exception.class})
    public ResponseEntity<Object> handleAll(Exception ex, WebRequest request) {
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
